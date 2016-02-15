Ext4.define('LABKEY.study.panel.FinderCardDeck', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-card-deck',

    layout: 'card',

    cls: 'labkey-data-finder-card-deck-view',

    border: false,

    initComponent : function() {

        this.width = Ext4.getBody().getWidth() - 70;
        this.items = [];
        var activeIndex = 0;
        for (var i = 0; i < this.cubeConfigs.length; i++)
        {
            this.items.push(Ext4.create('LABKEY.study.panel.FinderCard', {
                dataModuleName: this.dataModuleName,
                cubeConfig: this.cubeConfigs[i],
                itemId: this.cubeConfigs[i].objectName + '-finder-card'
            }));
            if (this.cubeConfigs[i].isDefault)
                activeIndex = i;
        }

        this.callParent();

        this.getLayout().setActiveItem(activeIndex);

        Ext4.EventManager.onWindowResize(function()
        {
            this.setWidth(Ext4.getBody().getWidth() - 70);
        }, this);
    }
});


