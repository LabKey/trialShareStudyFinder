Ext4.define("LABKEY.study.panel.Studies", {
    extend: 'Ext.panel.Panel',

    layout : 'vbox',
    border: false,
    alias : 'widget.labkey-studies-panel',
    cls: 'labkey-studies-panel',

    padding: "5 0 0 0",

    dataModuleName: 'study',

    autoScroll: true,

    initComponent : function() {
        this.items = [
            this.getStudyPanelHeader(),
            this.getStudyCards()
        ];
        this.callParent();

        this.getStudyCards().store.addListener('filterChange',this.onFilterSelectionChanged, this);
        this.on(
                {'studySubsetChanged': this.onStudySubsetChanged,
                 'searchTermsChanged': this.onSearchTermsChanged,
                 'filterSelectionChanged': this.onFilterSelectionChanged
                }
        );
    },

    onFilterSelectionChanged: function() {
        console.log('filterChange happened!')
    },

    onStudySubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getStudyPanelHeader().getStudySubsetMenu().getValue();
        this.getStudyCards().store.updateFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(value) {
        console.log("search terms changed to " + value)
    },

    getStudyPanelHeader : function() {
        if (!this.studyPanelHeader) {
            this.studyPanelHeader = Ext4.create("LABKEY.study.panel.StudyPanelHeader", {
                dataModuleName: this.dataModuleName,
                padding: 8
            });
        }
        return this.studyPanelHeader;
    },

    getStudyCards : function() {
        if (!this.studyCards) {
            this.studyCards = Ext4.create("LABKEY.study.panel.StudyCards", {
                dataModuleName: this.dataModuleName
            });
        }
        return this.studyCards;
    }
});