Ext4.define("LABKEY.study.panel.FacetsGrid", {

    extend : 'Ext.grid.Panel',

    alias: 'widget.labkey-study-facet-panel',

    cls: 'labkey-study-facets',

    ui: 'custom',

    itemSelector: 'div.facet',

    dataModuleName: 'study',

    autoScroll: true,

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
            //header: false,
            dataIndex: 'name',
            sortable: false,
            menuDisabled: true,
            cls:'facet',
            tpl: new Ext4.XTemplate(
                    '   <tpl if="count==0">',
                    '       <span id="member_{facetName}_{uniqueName}" style="position:relative;" class="member empty-member">',
                    '   <tpl else>',
                    '       <span id="member_{facetName}_{uniqueName}" style="position:relative;" class="member">',
                    '   </tpl>',
                    //'{facetName:this.displaySelection}',
                    '       <span class="member-name">{name}</span>',
                    '       &nbsp;',
                    '       <span class="member-count">{count:this.formatNumber}</span>',
                    '   <tpl if="count">',
                    '       <span class="bar" style="width:25%;"></span>',
                    '   </tpl>',
                    {
                        formatNumber :  Ext4.util.Format.numberRenderer('0,000'),
                        displaySelection : function(facetName) {
                            var facet = Ext4.getStore("facets").getById(facetName);
                            if (facet)
                            {
                                var selectedMembers = facet.get("selectedMembers");
                                if (selectedMembers || selectedMembers.length == 0)
                                    return '<span class="active member-indicator none-selected"></span>';
                                else
                                    return '<span class="active member-indicator not-selected"></span>';
                            }
                        }
                    }
            )
        }
    ],

    features: {
        ftype: 'grouping',
        collapsible: true,
        id: 'facetMemberGrouping',

        groupHeaderTpl: new Ext4.XTemplate(
            '<div class="facet-header facet-toggle" id="{name:this.genId}">',
            '       <div class="facet-caption facet-toggle">',
            '           <span class="facet-toggle">{name}</span>',
            '           <span class="clear-filter inactive">[clear]</span>',
            '       </div>',
            '       <div class="labkey-filter-options"></div>',
            '</div>',
                {
                    genId: function(name) {
                        var id = Ext4.id();
                        LABKEY.study.panel.FacetsGrid.headerLookup[name] = id;

                        return id;
                    }
                }
            )
        //groupHeaderTpl: new Ext4.XTemplate(
        //                '{name:this.displayFilterHeader}',
        //                {
        //                    displayFilterHeader : function(name) {
        //                        var facet = Ext4.getStore("facets").getById(name);
        //                        var selectedMembers = facet.get("selectedMembers");
        //                        var filterOptions = facet.get("filterOptions");
        //                        var header =
        //                                '<div class="facet-header facet-toggle" id="{name:this.genId}">' +
        //                                '       <div class="facet-caption facet-toggle active">' +
        //                                '           <span class="facet-toggle">' + name + '</span>';
        //                        if (selectedMembers && selectedMembers.length > 0)
        //                            header +=
        //                                '           <span class="clear-filter active">[clear]</span>';
        //                        header +=
        //                                '       </div>';
        //                        //if (selectedMembers && selectedMembers.length > 0 && filterOptions.length > 0)
        //                        if (filterOptions.length > 0)
        //                        {
        //                            header +=
        //                                    '       <div class="labkey-filter-options" >';
        //                            if (filterOptions.length < 2)
        //                            {
        //                                header +=
        //                                    '           <span class="filter-option-menu x4-menu-item-text inactive" href="#">';
        //                            }
        //                            else
        //                            {
        //                                header +=
        //                                    '           <span class="labkey-filter-options x4-menu-item-text" href="#">';
        //                            }
        //                            header +=
        //                                    '           ' + facet.get("currentFilterCaption");
        //                            if (filterOptions.length > 1)
        //                                header +=
        //                                    '               <i class="fa fa-caret-down"></i>';
        //                            header +=
        //                                    '           </span>' +
        //                                    '       </div>';
        //                        }
        //                        header +=
        //                                '   </div>';
        //
        //                        return header;
        //                    },
        //                    genId: function(name) {
        //                            var id = Ext4.id();
        //                            LABKEY.study.panel.FacetsGrid.headerLookup[name] = id;
        //
        //                            return id;
        //                    }
        //                }
        //        )
    },

    statics: {
        headerLookup: {}
    },

    initComponent: function() {
        this.facetStore = Ext4.create("LABKEY.study.store.Facets");

        this.store = Ext4.create('LABKEY.study.store.FacetMembers', {
            dataModuleName: this.dataModuleName
        });
        //this.store.proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "studyFacetMembers.api", LABKEY.containerPath);

        this.store.load();

        this.callParent();

        this.mon(this.view, {
            groupclick: this.onGroupClick,
            //groupcollapse: this.onGroupToggle,
            groupexpand: this.onGroupToggle,
            scope: this
        });

        this.getSelectionModel().on('selectionchange', this.onSelectionChange, this);
    },

    onSelectionChange: function(selModel, records) {
        this.facetStore.clearAllSelectedMembers();
        if (records.length > 0)
            this.facetStore.selectMembers(records);
        for (var f = 0; f < this.facetStore.count(); f++) {
            this.updateFacetHeader(this.facetStore.getAt(f).data.name);
        }
        this.store.updateCountsAsync();

        this.fireEvent("filterSelectionChanged", this.hasFilters());
    },

    onGroupToggle: function(view, node, facetName, eOpts) {
        this.updateFacetHeader(facetName);
    },

    onGroupClick : function(view, node, facetName, e, eOpts ) {
        console.log("Group " + facetName + " clicked");
        if (e.target.className.includes("clear-filter")) {
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

    updateFacetHeader: function(facetName) {
        if (this.hasFilters(facetName))
        {
            Ext4.get(Ext4.DomQuery.select('.clear-filter', LABKEY.study.panel.FacetsGrid.headerLookup[facetName])[0]).replaceCls('inactive', 'active');
        }
        else
        {
            Ext4.get(Ext4.DomQuery.select('.clear-filter', LABKEY.study.panel.FacetsGrid.headerLookup[facetName])[0]).replaceCls('active', 'inactive');
        }
        this.updateCurrentFacetOption(facetName);
    },

    updateCurrentFacetOption: function(facetName) {
        var facet = this.facetStore.getById(facetName);
        var selectedMembers = facet.get("selectedMembers");
        var html = "";
        if (selectedMembers && selectedMembers.length > 1)
        {
            html += '<span class="labkey-filter-caption">' + facet.get("currentFilterCaption") + '</span>';
            if (facet.get("filterOptions").length > 1)
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
        var filterOptions = facet.get("filterOptions");
        if (filterOptions.length < 2)
            return;

        var filterOptionsMenu = Ext4.create('Ext.menu.Menu', {
            //cls: 'basemenu dropdownmenu',
            showSeparator: false,
        });

        filterOptionsMenu.on('click', function(menu, item) {
                    facet.data.currentFilterType = item.value;
                    facet.data.currentFilterCaption = item.text;
                    this.updateCurrentFacetOption(facetName);
                },
                this
        );

        for (var i = 0; i < filterOptions.length; i++) {
            filterOptionsMenu.add({
                value: filterOptions[i].get("type"),
                text: filterOptions[i].get("caption")
            });
        }
        filterOptionsMenu.showAt(event.xy);

        //if (event.stopPropagation)
        //    event.stopPropagation();
    },

    clearAllFilters : function (updateCounts) {
        for (var i = 0; i < this.getFacetStore().count(); i++)
        {
            var name = this.facetStore.getAt(i).get("name");
            if (name == "Study")
                continue;
            this.clearFilter(name);
        }
        if (updateCounts)
            this.store.updateCountsAsync();
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

        this.updateFacetHeader(facetName);
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