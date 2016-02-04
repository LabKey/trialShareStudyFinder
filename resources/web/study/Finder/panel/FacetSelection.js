Ext4.define("LABKEY.study.panel.FacetSelection", {
    extend: 'Ext.panel.Panel',

    layout: { type: 'vbox', align: 'stretch' },

    border: false,

    alias : 'widget.study-facet-selection-panel',

    cls: 'labkey-facet-selection-panel',

    padding: "10 8 8 10",

    autoScroll: false,

    initComponent : function() {
        this.items = [
            this.getFacetPanelHeader(),
            this.getFacetSelectionSummary(),
            this.getFacets()
        ];
        this.callParent();

        this.on({
            filterSelectionChanged: this.onFilterSelectionChange,
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

    onFilterSelectionChange: function(hasFilters) {
        if (hasFilters)
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('inactive', 'active');
        else
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('active', 'inactive');
    },


    getFacetPanelHeader : function() {
        if (!this.facetPanelHeader) {
            this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeader", {
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
    }

});