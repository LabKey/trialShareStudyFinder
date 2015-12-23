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


    onFilterSelectionChange: function(hasFilters) {
        console.log("FacetSelection filterSelectionChanged handler");
        if (hasFilters)
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('inactive', 'active');
        else
            Ext4.get(Ext4.DomQuery.select('.labkey-clear-all', this.id)[0]).replaceCls('active', 'inactive');
        this.updateSummaryPanel(); // TODO is this necessary?
    },

    updateSummaryPanel : function() {
        var studiesStore = Ext4.getStore("studies");
        this.getFacetSelectionSummary().update({
            studyCount: studiesStore.count(),
            participantCount: studiesStore.sum("participantCount")
        });
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
                dataModuleName: this.dataModuleName,
                data: {
                    studyCount: studiesStore.count(),
                    participantCount: studiesStore.sum("participantCount")
                }
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