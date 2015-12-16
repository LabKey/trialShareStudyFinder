Ext4.define("LABKEY.study.panel.Studies", {
    extend: 'Ext.panel.Panel',

    layout : 'vbox',
    border: false,
    alias : 'widget.labkey-studies-panel',
    cls: 'labkey-studies-panel',

    padding: "5 0 0 0",

    dataModuleName: 'study',

    initComponent : function() {
        this.items = [
            this.getStudyPanelHeader(),
            this.getStudyCards()
        ];
        this.callParent();

        this.on(
                {'studySubsetChanged': this.onStudySubsetChanged,
                 'searchTermsChanged': this.onSearchTermsChanged}
        );
    },

    onStudySubsetChanged : function(value) {
        this.getStudyCards().store.clearFilter();
        this.getStudyCards().store.filter('availability', value);
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