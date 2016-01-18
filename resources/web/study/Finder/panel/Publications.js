Ext4.define("LABKEY.study.panel.Publications", {
    extend: 'Ext.panel.Panel',

    layout : 'vbox',
    border: false,
    alias : 'widget.labkey-publications-panel',
    cls: 'labkey-publications-panel',

    padding: "5 0 0 0",

    objectName: 'Publication',

    autoScroll: true,

    initComponent : function() {
        this.items = [
            this.getCardPanelHeader(),
            this.getCards()
        ];
        this.callParent();

        //this.getCards().store.addListener('filterChange',this.onFilterSelectionChanged, this);
        this.on(
                {'studySubsetChanged': this.onStudySubsetChanged,
                 'searchTermsChanged': this.onSearchTermsChanged
                 //'filterSelectionChanged': this.onFilterSelectionChanged
                }
        );
    },

    //onFilterSelectionChanged: function() {
    //    console.log('filterChange happened!')
    //},

    onStudySubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getCardPanelHeader().getStudySubsetMenu().getValue();
        this.getCards().store.updateFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(value) {
        console.log("search terms changed to " + value)
    },

    getCardPanelHeader : function() {
        if (!this.cardPanelHeader) {
            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.StudyPanelHeader", {
                dataModuleName: this.dataModuleName,
                padding: 8,
                showSearch : this.showSearch,
                objectName: this.objectName
            });
        }
        return this.cardPanelHeader;
    },

    getCards : function() {
        if (!this.cards) {
            this.cards = Ext4.create("LABKEY.study.panel.PublicationCards", {
                dataModuleName: this.dataModuleName
            });
        }
        PC = this.cards;
        return this.cards;
    }
});