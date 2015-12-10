Ext4.define('LABKEY.study.panel.Finder', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    layout: 'border',

    cls: 'labkey-data-finder-view',

    title: "Data Finder",

    showParticipantFilters: false,

    studyData: [],

    studySubsets: null,

    height: '500px',

    dataModuleName: 'study',

    initComponent : function() {

        this.items = [
            this.getFacetsPanel(),
            this.getStudiesPanel()
        ];

        this.callParent();
    },

    getFacetsPanel: function() {
        if (!this.facetsPanel) {

            this.facetsPanel = Ext4.create("LABKEY.study.panel.FacetSelection", {
                region: 'west',
                width: '21%',
                maxWidth: '265px',
                dataModuleName: this.dataModuleName,
                showParticipantFilters : this.showParticipantFilters
            });
        }
        FACETS = this.facetsPanel;
        return this.facetsPanel;
    },

    getStudiesPanel: function() {
        if (!this.studiesPanel) {
            this.studiesPanel = Ext4.create("LABKEY.study.panel.Studies", {
                studySubsets : this.studySubsets,
                showSearch : this.showSearch,
                dataModuleName: this.dataModuleName,
                region: 'center',
                width: '80%',
                id: 'studies-view'
            });
        }
        STUDIES = this.studiesPanel;
        return this.studiesPanel;
    }

});


