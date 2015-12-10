Ext4.define("LABKEY.study.panel.FacetSelection", {
    extend: 'Ext.panel.Panel',

    layout: { type: 'vbox', align: 'stretch' },

    border: false,

    alias : 'widget.study-facet-selection-panel',

    cls: 'selection-panel',

    padding: "10 8 8 10",

    initComponent : function() {
        this.items = [
            this.getFacetPanelHeader(),
            this.getFacetSelectionSummary(),
            this.getFacets()
        ];
        this.callParent();
    },

    getFacetPanelHeader : function() {
        if (!this.facetPanelHeader) {
            this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeader", {

            });
        }
        return this.facetPanelHeader;
    },

    getFacetSelectionSummary: function() {
        if (!this.facetSelectionSummary) {
            this.facetSelectionSummary = Ext4.create("LABKEY.study.panel.SelectionSummary", {

            });
        }
        return this.facetSelectionSummary;
    },

    getFacets : function() {
        if (!this.facets) {
            this.facets = Ext4.create("LABKEY.study.panel.Facets", {

            });
        }
        return this.facets;
    }

});