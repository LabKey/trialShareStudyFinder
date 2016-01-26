Ext4.define("LABKEY.study.panel.Studies", {
    extend: 'Ext.panel.Panel',

    layout : 'vbox',
    border: false,
    alias : 'widget.labkey-studies-panel',
    cls: 'labkey-studies-panel',

    padding: "5 0 0 0",

    objectName: 'Study',

    showSearch : true,

    autoScroll: true,

    initComponent : function() {
        this.items = [
            this.getStudyPanelHeader(),
            this.getStudyCards()
        ];
        this.callParent();

        //this.getStudyCards().store.addListener('filterChange',this.onFilterSelectionChanged, this);
        this.on(
                {'subsetChanged': this.onSubsetChanged,
                 'searchTermsChanged': this.onSearchTermsChanged
                 //'filterSelectionChanged': this.onFilterSelectionChanged
                }
        );
    },

    //onFilterSelectionChanged: function() {
    //    console.log('filterChange happened!')
    //},

    onSubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getStudyPanelHeader().getSubsetMenu().getValue();
        this.getStudyCards().store.updateFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(value) {
        console.log("search terms changed to " + value)
    },

    getStudyPanelHeader : function() {
        if (!this.cardPanelHeader) {
            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
                dataModuleName: this.dataModuleName,
                padding: 8,
                showSearch : this.showSearch,
                objectName: this.objectName
            });
        }
        return this.cardPanelHeader;
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