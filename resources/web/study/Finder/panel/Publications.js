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
            selectedSubset = this.getCardPanelHeader().getSubsetMenu().getValue();
        this.getCards().store.updateFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(value) {
        console.log("search terms changed to " + value)
    },

    // TODO move to base class
    getCardPanelHeader : function() {
        if (!this.cardPanelHeader) {
            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
                dataModuleName: this.dataModuleName,
                cubeContainerPath: this.cubeContainerPath,
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
                dataModuleName: this.dataModuleName,
                cubeContainerPath : this.cubeContainerPath
            });
        }
        return this.cards;
    }
});