Ext4.define("LABKEY.study.panel.FacetSelection", {
    extend: 'Ext.panel.Panel',

    layout: { type: 'vbox', align: 'stretch' },

    border: false,

    alias : 'widget.study-facet-selection-panel',

    cls: 'selection-panel',

    padding: "10 8 8 10",

    autoScroll: true,

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
        }
        );
    },

    onCubeReady: function(mdx) {
        this.getFacets().onCubeReady(mdx);
    },

    onClearAllFilters: function() {
        this.getFacets().clearAllFilters(true);
        this.onFilterSelectionChange(false);
    },

    onStudySubsetChanged: function() {
        this.getFacets().onStudySubsetChanged();
    },

    onFilterSelectionChange: function(hasFilters) {
        console.log("FacetSelection filterSelectionChanged handler");
        if (hasFilters)
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('inactive', 'active');
        else
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('active', 'inactive');
    },


    getFacetPanelHeader : function() {
        if (!this.facetPanelHeader) {
            //this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeaderTpl", {
            this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeader", {
                dataModuleName: this.dataModuleName
            });
        }
        return this.facetPanelHeader;
    },

    getFacetSelectionSummary: function() {
        if (!this.facetSelectionSummary) {
            var studiesStore = Ext4.getStore("studies");
            this.facetSelectionSummary = Ext4.create("LABKEY.study.panel.SelectionSummary", {
                dataModuleName: this.dataModuleName
            });
        }
        return this.facetSelectionSummary;
    },

    getFacets : function() {
        if (!this.facets) {
            //this.facets = Ext4.create("LABKEY.study.panel.Facets", {
            //    dataModuleName: this.dataModuleName
            //});
            this.facets = Ext4.create("LABKEY.study.panel.FacetsGrid", {
                dataModuleName: this.dataModuleName
            });
        }
        FG = this.facets;
        return this.facets;
    }

});