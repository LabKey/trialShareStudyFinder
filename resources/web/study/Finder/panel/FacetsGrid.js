/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.FacetsGrid", {

    extend : 'Ext.grid.Panel',

    alias: 'widget.labkey-study-facet-panel',

    cls: 'labkey-finder-facets',

    ui: 'custom',

    itemSelector: 'span.x4-grid-data-row',

    autoScroll: false,

    bubbleEvents : ["filterSelectionChanged", "countsUpdated"],

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
            cls:'labkey-facet',
            tpl: new Ext4.XTemplate(
                    '   <tpl if="count==0">',
                    '       <span class="labkey-facet-member labkey-empty-member">',
                    '   <tpl else>',
                    '       <span class="labkey-facet-member">',
                    '   </tpl>',
                    '       <span class="labkey-facet-member-name" title="{name}">{name}</span>',
                    '       <span class="labkey-facet-member-count">{count:this.formatNumber} / {unfilteredCount:this.formatNumber}</span>',
                    '   <tpl if="unfilteredCount">',
                    '       <span class="labkey-facet-unfilteredPercent-bar" style="width:{unfilteredPercent}%;"></span>',
                    '   </tpl>',
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

    statics: {
        hasFilters : function(objectName, facetName) {
            var facetStore = Ext4.getStore(objectName + 'Facets');
            if (!facetStore)
            {
                console.error("Unable to find facet store", objectName + 'Facets' );
                return false;
            }
            else if (!facetStore.isLoaded)
            {
                return false;
            }
            if (facetName)
                return facetStore.getById(facetName).data.selectedMembers.length > 0;
            else {
                for (var i = 0; i < facetStore.count(); i++) {
                    var facet = facetStore.getAt(i);
                    if (facet.get("name") != facetStore.cubeConfig.objectName && facetStore.getAt(i).data.selectedMembers.length > 0)
                        return true;
                }
                return false;
            }
        }
    },

    initComponent: function() {
        this.cls = 'labkey-finder-facets labkey-' + this.cubeConfig.objectName.toLowerCase() + '-facets';
        this.facetStore = Ext4.create("LABKEY.study.store.Facets", {
            dataModuleName: this.dataModuleName,
            cubeConfig: this.cubeConfig,
            storeId: this.cubeConfig.objectName + "Facets"
        });

        this.store = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");

        // This makes the objectName available to the header
        this.features = this.getGroupHeaderFeature(this.cubeConfig.objectName);

        this.callParent();

        this.mon(this.view, {
            groupclick: this.onGroupClick,
            groupcollapse: this.onGroupCollapse,
            groupexpand: this.onGroupExpand,
            scope: this
        });

        var facetSelectionChange = function() {
            this.facetStore.updateCountsAsync();
            this.fireEvent("filterSelectionChanged", LABKEY.study.panel.FacetsGrid.hasFilters(this.cubeConfig.objectName));
        };

        var facetChangeTask = new Ext4.util.DelayedTask(facetSelectionChange, this);

        this.getSelectionModel().on('selectionchange', function(selModel, records){
            this.facetStore.clearAllSelectedMembers();
            if (records.length > 0)
                this.facetStore.selectMembers(records);
            facetChangeTask.delay(500);
        }, this);

        this.facetStore.on(
            'countsUpdated' , this.onCountsUpdated, this
        )
    },

    onCountsUpdated : function() {
        this.fireEvent("countsUpdated");
    },

    getGroupHeaderFeature: function(objectName) {
        return Ext4.create('Ext.grid.feature.Grouping',
                {
                    ftype: 'grouping',
                    collapsible: true,

                    groupHeaderTpl: new Ext4.XTemplate(
                            '<div class="labkey-facet-header">',
                            '       <div class="labkey-facet-caption">',
                            '           <span class="labkey-facet-name">{name}</span>',
                            '           {[this.displayClearLabel(values)]}',
                            '       </div>',
                            '         {[this.makeFilterOptions(values)]}',
                            '</div>',
                            {
                                objectName: objectName,
                                displayClearLabel: function(values)
                                {
                                    var html = '<span class="labkey-clear-filter inactive">[clear]</span>';
                                    if (LABKEY.study.panel.FacetsGrid.hasFilters(this.objectName, values.name))
                                    {
                                        html = '<span class="labkey-clear-filter active">[clear]</span>';
                                    }
                                    return  html;
                                },
                                makeFilterOptions: function(values) {
                                    var facetStore = Ext4.getStore(this.objectName + "Facets");
                                    var facet = facetStore.getById(values.name);
                                    var selectedMembers = facet.get("selectedMembers");
                                    var html = '<div class="labkey-filter-options">';

                                    if ( selectedMembers && selectedMembers.length > 1)
                                    {
                                        var pointerClass = (facet.filterOptionsStore.count() < 2) ? "inactive" : "active";

                                        html += '<span class="labkey-filter-caption ' + pointerClass + '">' + facet.get("currentFilterCaption");
                                        if (facet.filterOptionsStore.count() > 1)
                                            html += '&nbsp;<i class="fa fa-caret-down labkey-filter-caption"></i>';
                                        html +=  '</span>';
                                    }
                                    html += '</div>';
                                    return html;
                                }
                            }
                    )

                }
        );
    },

    onCubeReady : function(mdx) {
        this.facetStore.mdx = mdx;
        this.facetStore.loadFromCube();
    },

    onSubsetChanged: function() {
        var objectStore = Ext4.getStore(this.cubeConfig.objectName);
        objectStore.selectAll();
        if (this.facetStore.getById(this.cubeConfig.objectName))
        {
            this.facetStore.updateCountsAsync();
        }
    },

    onSelectionChange: function(selModel, records) {
        this.facetStore.clearAllSelectedMembers();
        if (records.length > 0)
            this.facetStore.selectMembers(records);
        this.facetStore.updateCountsAsync();

        this.fireEvent("filterSelectionChanged", LABKEY.study.panel.FacetsGrid.hasFilters(this.cubeConfig.objectName));
    },

    onGroupCollapse: function(view, node, facetName, eOpts) {
        this.updateFacetHeader(facetName, false);
    },

    onGroupExpand: function(view, node, facetName, eOpts) {
        this.updateFacetHeader(facetName, true);
    },

    onGroupClick : function(view, node, facetName, e, eOpts ) {
        if (e.target.className.indexOf("labkey-clear-filter") >= 0) {
            this.clearFilter(facetName);
            return false;
        }
        if (e.target.className.indexOf("labkey-filter-options") >= 0 || e.target.className.indexOf("labkey-filter-caption") >= 0)
        {
            this.displayFilterChoice(facetName, e);
            return false;
        }
    },

    updateFacetHeader: function(facetName, isExpanded) {
        var facet = this.facetStore.getById(facetName);
        if (facet)
            facet.set("isExpanded", isExpanded);
    },

    updateCurrentFacetOption: function(facetName, isExpanded) {
        this.facetStore.updateCountsAsync();
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
            showSeparator: false
        });

        filterOptionsMenu.on('click', function(menu, item) {
                    facet.set("currentFilterType",  item.value);
                    facet.set("currentFilterCaption",  item.text);
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
            var facet = this.facetStore.getAt(i);
            facet.data.selectedMembers = [];
        }
        this.getSelectionModel().deselectAll(false);
        if (updateCounts)
            this.facetStore.updateCountsAsync();
        this.fireEvent("filterSelectionChanged", false);
    },

    clearFilter : function (facetName, suppressEvent) {
        var facet = this.facetStore.getById(facetName);
        facet.data.selectedMembers = [];
        for (var i = 0; i < this.store.count(); i++) {
            var member = this.store.getAt(i);
            if (member.get("facetName") == facetName) {
                this.getSelectionModel().deselect(member);
            }
        }

        if (!suppressEvent)
            this.fireEvent("filterSelectionChanged",  LABKEY.study.panel.FacetsGrid.hasFilters(this.cubeConfig.objectName));
    }
});