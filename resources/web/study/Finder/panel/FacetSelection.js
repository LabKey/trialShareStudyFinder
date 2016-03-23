/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.FacetSelection", {
    extend: 'Ext.panel.Panel',

    layout: {
        type: 'vbox',
        align: 'stretch',
        pack: 'start'
    },

    border: false,

    alias : 'widget.study-facet-selection-panel',

    cls: 'labkey-facet-selection-panel',

    bubbleEvents: [
        "clearAllFilters"
    ],

    autoScroll: false,

    initComponent : function() {
        Ext4.create('LABKEY.study.store.FacetMembers', {
            storeId : this.cubeConfig.objectName + "FacetMembers"
        });

        this.items = [
            this.getFacetPanelHeader(),
            this.getFacetSelectionSummary(),
            this.getFacetsContainer()
        ];
        this.callParent();

        this.on({
            filterSelectionChanged: this.onFilterSelectionChange,
            searchTermsChanged: this.onFilterSelectionChange,
            clearAllFilters: this.onClearAllFilters
        });
    },

    onCubeReady: function(mdx) {
        this.getFacets().onCubeReady(mdx);
    },

    onClearAllFilters: function() {
        this.getFacets().clearAllFilters(false);
    },

    onSubsetChanged: function() {
        this.getFacets().onSubsetChanged();
    },
    
    onSearchTermsChanged: function(terms)
    {
        if (this.hasFilters)
            return;
        else if (terms)
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('inactive', 'active');
        else
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('active', 'inactive');
    },

    onFilterSelectionChange: function(hasFilters) {
        this.hasFilters = hasFilters;
        if (hasFilters)
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('inactive', 'active');
        else
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('active', 'inactive');
    },

    getFacetPanelHeader : function() {
        if (!this.facetPanelHeader) {
            this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeader", {
                objectName : this.cubeConfig.objectName,
                width: "100%",
                dataModuleName: this.dataModuleName
            });
        }
        return this.facetPanelHeader;
    },

    getFacetSelectionSummary: function() {
        if (!this.facetSelectionSummary) {
            if (this.cubeConfig.objectName == "Study")
            {
                this.facetSelectionSummary = Ext4.create("LABKEY.study.panel.StudySummary", {
                    dataModuleName: this.dataModuleName,
                    objectName: this.cubeConfig.objectName
                });
            }
            else
            {
                this.facetSelectionSummary = Ext4.create("LABKEY.study.panel.PublicationSummary", {
                    dataModuleName: this.dataModuleName,
                    objectName: this.cubeConfig.objectName
                });
            }
        }
        return this.facetSelectionSummary;
    },

    getFacets : function() {
        if (!this.facets) {
            this.facets = Ext4.create("LABKEY.study.panel.FacetsGrid", {
                dataModuleName: this.dataModuleName,
                cubeConfig: this.cubeConfig
            });
        }
        return this.facets;
    },

    getFacetsContainer: function() {
        if (!this.facetsContainer) {
            this.facetsContainer = Ext4.create("Ext.Container", {
                xtype: 'container',
                itemId: 'facetsContainer',
                flex: 10,
                autoScroll: true,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    pack: 'start'
                },
                items: [
                    this.getFacets()
                ]
            });
        }
        return this.facetsContainer;
    }

});