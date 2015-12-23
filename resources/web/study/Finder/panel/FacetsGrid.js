Ext4.define("LABKEY.study.panel.FacetsGrid", {

    extend : 'Ext.grid.Panel',

    alias: 'widget.labkey-study-facet-panel',

    cls: 'labkey-study-facets',

    ui: 'custom',

    //itemSelector: 'div.facet',
    itemSelector: 'span.labkey-facet-member',

    dataModuleName: 'study',

    autoScroll: false,

    bubbleEvents : ["filterSelectionChanged"],

    viewConfig : { stripeRows : false },

    selType: 'checkboxmodel',
    selModel: {
        mode: 'SIMPLE',
        checkSelector: 'td.x-grid-cell-row-checker'
    },
    multiSelect: true,

    header: false,

    hideHeaders: true,

    enableColumnHide: false,
    enableColumnResize: false,
    columns: [
        {
            xtype: 'templatecolumn',
            flex: 1,
            dataIndex: 'name',
            sortable: false,
            menuDisabled: true,
            cls:'facet',
            tpl: new Ext4.XTemplate(
                    '   <tpl if="count==0">',
                    '       <span class="labkey-facet-member labkey-empty-member">',
                    '   <tpl else>',
                    '       <span class="labkey-facet-member">',
                    '   </tpl>',
                    '       <span class="labkey-facet-member-name">{name}&nbsp;</span>',
                    '       <span class="labkey-facet-member-count">{count:this.formatNumber}</span>',
                    '   <tpl if="count">',
                    '       <span class="labkey-facet-percent-bar" style="width:{percent}%;"></span>',
                    '   </tpl>',
                    '       </span>',
                    {
                        formatNumber :  Ext4.util.Format.numberRenderer('0,000')
                    }
            )
        }
    ],

    features: {
        ftype: 'grouping',
        collapsible: true,
        id: 'facetMemberGrouping',

        groupHeaderTpl: new Ext4.XTemplate(
            '<div class="labkey-facet-header" id="{name:this.genId}">',
            '       <div class="labkey-facet-caption">',
            '           <span>{name}</span>',
            '           <span class="labkey-clear-filter inactive">[clear]</span>',
            '       </div>',
            '       <div class="labkey-filter-options"></div>',
            '</div>',
                {
                    genId: function(name) {
                        var id = Ext4.id();
                        LABKEY.study.panel.FacetsGrid.headerLookup[name] = id;
                        return id;
                    },
                    makeFilterOptions: function(name) {
                        var facetStore = Ext4.getStore("facets");
                        var facet = facetStore.get(name);
                        // TODO render menu or not
                    }
                }
            )

    },

    statics: {
        headerLookup: {}
    },

    initComponent: function() {
        this.facetStore = Ext4.create("LABKEY.study.store.Facets");

        this.store = Ext4.create('LABKEY.study.store.FacetMembers', {
            dataModuleName: this.dataModuleName
        });

        this.callParent();

        this.mon(this.view, {
            groupclick: this.onGroupClick,
            groupcollapse: this.onGroupCollapse,
            groupexpand: this.onGroupExpand,
            scope: this
        });

        this.getSelectionModel().on('selectionchange', this.onSelectionChange, this);
    },

    onCubeReady : function(mdx) {
        this.facetStore.mdx = mdx;
        this.facetStore.loadFromCube();
    },

    onSelectionChange: function(selModel, records) {
        this.facetStore.clearAllSelectedMembers();
        if (records.length > 0)
            this.facetStore.selectMembers(records);
        for (var f = 0; f < this.facetStore.count(); f++) {
            this.updateFacetHeader(this.facetStore.getAt(f).data.name, true);
        }
        this.facetStore.updateCountsAsync();

        this.fireEvent("filterSelectionChanged", this.hasFilters());
    },

    onGroupCollapse: function(view, node, facetName, eOpts) {
        this.updateFacetHeader(facetName, false);
    },

    onGroupExpand: function(view, node, facetName, eOpts) {
        this.updateFacetHeader(facetName, true);
    },

    onGroupClick : function(view, node, facetName, e, eOpts ) {
        console.log("Group " + facetName + " clicked");
        if (e.target.className.includes("labkey-clear-filter")) {
            console.log("Clearing filter for group " + facetName);
            this.clearFilter(facetName);
            return false;
        }
        if (e.target.className.includes("labkey-filter-options") || e.target.className.includes("labkey-filter-caption"))
        {
            console.log("Showing filter options for facet " + facetName);
            this.displayFilterChoice(facetName, e);
            return false;
        }
    },

    updateFacetHeaders: function() {
        this.facetStore.each(function(facet) {
            this.updateFacetHeaders(facet.get("name"), facet.get("isExpanded"));
        }, this);
    },

    updateFacetHeader: function(facetName, isExpanded) {

        if (this.hasFilters(facetName))
        {
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-filter', LABKEY.study.panel.FacetsGrid.headerLookup[facetName])[0]).replaceCls('inactive', 'active');
        }
        else
        {
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-filter', LABKEY.study.panel.FacetsGrid.headerLookup[facetName])[0]).replaceCls('active', 'inactive');
        }
        this.updateCurrentFacetOption(facetName, isExpanded);
        var facet = this.facetStore.getById(facetName);
        if (facet)
            facet.set("isExpanded", isExpanded);
    },

    updateCurrentFacetOption: function(facetName, isExpanded) {
        var facet = this.facetStore.getById(facetName);
        var selectedMembers = facet.get("selectedMembers");
        var html = "";
        if (isExpanded && selectedMembers && selectedMembers.length > 1)
        {
            html += '<span class="labkey-filter-caption">' + facet.get("currentFilterCaption") + '</span>';
            if (facet.filterOptionsStore.count() > 1)
                html += '&nbsp;<i class="fa fa-caret-down"></i>';
        }
        Ext4.get(Ext4.DomQuery.select('.labkey-filter-options', LABKEY.study.panel.FacetsGrid.headerLookup[facetName])[0]).setHTML(html);
    },

    displayFilterChoice : function (facetName, event)
    {
        var facet = this.facetStore.getById(facetName);

        if (!facet)
        {
            console.error("could not find facet " + facetName);
            return;
        }
        var filterOptions = facet.filterOptionsStore;
        if (filterOptions.count() < 2)
            return;

        var filterOptionsMenu = Ext4.create('Ext.menu.Menu', {
            //cls: 'basemenu dropdownmenu',
            showSeparator: false
        });

        filterOptionsMenu.on('click', function(menu, item) {
                    facet.data.currentFilterType = item.value;
                    facet.data.currentFilterCaption = item.text;
                    this.updateCurrentFacetOption(facetName, true);
                },
                this
        );

        for (var i = 0; i < filterOptions.count(); i++) {
            filterOptionsMenu.add({
                value: filterOptions.getAt(i).get("type"),
                text: filterOptions.getAt(i).get("caption")
            });
        }
        filterOptionsMenu.showAt(event.xy);
    },

    clearAllFilters : function (updateCounts) {
        for (var i = 0; i < this.facetStore.count(); i++)
        {
            var name = this.facetStore.getAt(i).get("name");
            if (name == "Study")
                continue;
            this.clearFilter(name);
        }
        if (updateCounts)
            this.facetStore.updateCountsAsync();
        this.fireEvent("filterSelectionCleared", false);
    },

    clearFilter : function (facetName) {
        var facet = this.facetStore.getById(facetName);
        facet.data.selectedMembers = [];
        for (var i = 0; i < this.store.count(); i++) {
            var member = this.store.getAt(i);
            if (member.get("facetName") == facetName) {
                this.getSelectionModel().deselect(member);
            }
        }

        this.updateFacetHeader(facetName, true);
        this.fireEvent("filterSelectionCleared", this.hasFilters(facetName));
    },

    hasFilters : function(facetName) {
        if (facetName)
            return this.facetStore.getById(facetName).data.selectedMembers.length > 0;
        else {
            for (var i = 0; i < this.facetStore.count(); i++) {
                if (this.facetStore.getAt(i).data.selectedMembers.length > 0)
                    return true;
            }
            return false;
        }
    }
});