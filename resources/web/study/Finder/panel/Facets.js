Ext4.define("LABKEY.study.panel.Facets", {

    extend : 'Ext.view.View',

    alias: 'widget.labkey-study-facet-panel',

    cls: 'labkey-study-facets',

    itemSelector: 'div.facet',

    autoScroll: true,

    studyData : [],

    store: Ext4.create('Ext.data.Store', {
        model: 'LABKEY.study.data.Facet',
        autoLoad: true,
        proxy : {
            type: "ajax",
            url:  LABKEY.ActionURL.buildURL("trialshare", "getStudyFacets", LABKEY.containerPath),
            reader: {
                type: 'json',
                root: 'data'
            }
        },
        listeners: {
            'load' : {
                fn : function(store, records, options) {
                    console.log('Facet store loaded');
                    this.facetsLoaded = true;
                },
                scope: this
            }
        }
    }),
    tpl: new Ext4.XTemplate(

            '<span id="facetPanel">',
            '<tpl for=".">',
            '<div id="group_{name}" class="facet collapsed">',
            '   <div class="facet-header facet-toggle">',
            '       <div class="facet-caption facet-toggle active">',
            '           <i class="fa fa-plus-square facet-toggle"></i>',
            '           <i class="fa fa-minus-square facet-toggle"></i>',
            '           &nbsp;',
            '       <tpl if="caption != null">',
            '           <span class="facet-toggle">{caption}</span>',
            '       <tpl else>',
            '           <span class="facet-toggle">{name}</span>',
            '       </tpl>',
            '       <tpl if="currentFilters && currentFilters.length &gt; 0">',
            '           <span class="clear-filter active">[clear]</span>',
            '       </tpl>',
            '       </div>',

            '   <tpl if="currentFilters && currentFilters.length &gt; 0 && filterOptions.length &gt; 0">',
            '       <div class="labkey-filter-options" >',
            '       <tpl if="filterOptions.length < 2">',
            '           <a onclick="displayFilterChoice(name, $event)" class="x4-menu-item-text inactive" href="#">',
            '       <tpl else>',
            '           <a onclick="displayFilterChoice(name, $event)" class="x4-menu-item-text" href="#">',
            '       </tpl>',
            '           {filterCaption} ',
            '           <tpl if="filterOptions.length > 1">',
            '               <i class="fa fa-caret-down"></i>',
            '           </tpl>',
            '           </a>',
            '       </div>',
            '   </tpl>',
            '   </div>',
            '   <ul>',
            '   <tpl for="members">',
            '   <tpl if="count==0">',
            '       <li id="member_{parent.name}_{uniqueName}" style="position:relative;" class="member empty-member">',
            '   <tpl else>',
            '       <li id="member_{parent.name}_{uniqueName}" style="position:relative;" class="member">',
            '   </tpl>',
            '   <tpl if="!currentFilters || !currentFilters.length">',
            '       <span class="active member-indicator none-selected"></span>',
            '   <tpl else>',
            '       <span class="active member-indicator not-selected"></span>',
            '   </tpl>',
            '       <span class="member-name">{name}</span>',
            '       &nbsp;',
            '       <span class="member-count">{count:this.formatNumber}</span>',
            '   <tpl if="count">',
            '       <span class="bar" style="width:{percent}%;"></span>',
            '   </tpl>',
            '       </li>',
            '   </tpl>',
            '   </ul>',
            '</div>',
            '</tpl>',
            '</span>',
            {
                formatNumber :  Ext4.util.Format.numberRenderer('0,000'),
            }
    ),

    listeners: {
        itemClick: function(view, record, item, index, event, eOpts) {
            var targetEl = event.getTarget();
            if (targetEl.className.includes("member"))
                this.selectMember(record, item, index, event);
            else if (targetEl.className.includes("facet-toggle"))
            {
                if (item.className.includes("expanded"))
                    item.className = item.className.replace("expanded", "collapsed");
                else
                    item.className = item.className.replace("collapsed", "expanded");
            }
        },
        mouseover: function(view, record, item, index, event, eOpts) {
            console.log("display filter choice");
            this.displayFilterChoice(record, event);
        }
    },

    displayFilterChoice : function (record, event)
    {
        if (record.filters.length < 2)

        var locationElement = event.target;
        if (locationElement.className.includes('fa-caret'))
            locationElement = event.target.parentElement;
        var xy = Ext4.fly(locationElement).getXY();
        this.filterChoice =
        {
            show: true,
            dimName: dimName,
            x: xy[0],
            y: xy[1],
            options: dim.filterOptions
        };
        if (event.stopPropagation)
            event.stopPropagation();
    },

    selectMember : function (facet, item, facetIndex, event) {
        var shiftClick = event && (event.ctrlKey || event.altKey || event.metaKey);
        this._selectMember(facet, item, facetIndex, event, shiftClick);
    },

    toggleMember : function (facet, item, facetIndex, event) {
        this._selectMember(facet, item, facetIndex, event, true);
    },

    _selectMember : function (facet, item, facetIndex, event, shiftClick) {

        var filterMembers = facet.get("filters");

        var name = facet.get("name");
        var element = event.target;
        while (element.parentElement && !element.id.includes("member_")) {
            element = element.parentElement;
        }
        if (!element)
        {
            if (0 == filterMembers.length)  // no change
                return;
            this._clearFilter(name);
        }
        else if (!shiftClick)
        {
            this._clearFilter(name);
            facet.currentFilters = [element.id]; // TODO add currentFilters to model
            member.selected = true; // TODO change classes here: li gets 'selected-member' and span gets 'selected'
        }
        else
        {
            var index = -1;
            for (var m = 0; m < filterMembers.length; m++)
            {
                if (member.uniqueName == filterMembers[m].uniqueName)
                    index = m;
            }
            if (index == -1) // unselected -> selected
            {
                filterMembers.push(member);
                member.selected = true;
            }
            else // selected --> unselected
            {
                filterMembers.splice(index, 1);
                member.selected = false;
                this.fireEvent("filterSelectionCleared", this.hasFilters());
            }
        }

        this.updateCountsAsync();
        if (event.stopPropagation)
            event.stopPropagation();
    },

    clearAllFilters : function (updateCounts) {
        for (var i = 0; i < this.getStore().count(); i++)
        {
            if (this.store.getAt(i).get("id") == "Study")
                continue;
            this._clearFilter(this.store.getAt(i));
        }
        if (updateCounts)
            this.updateCountsAsync();
        this.fireEvent("filterSelectionCleared", false);
    },

    _clearFilter : function (facet) {
        var filterMembers = facet.get("filters");
        for (var m = 0; m < filterMembers.length; m++)
            filterMembers[m].selected = false;
        dim.filters = [];
        this.fireEvent("filterSelectionCleared", this.hasFilters());
    }
});