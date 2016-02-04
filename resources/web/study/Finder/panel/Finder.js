Ext4.define('LABKEY.study.panel.Finder', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    itemId : 'labkey-data-finder-panel',

    layout: 'vbox',

    cls: 'labkey-data-finder-view',

    border: false,

    height: '500px',

    autoScroll : true,

    searchTerms : '',

    initComponent : function() {

        this.items = [];

        if (this.cubeConfigs.length > 1)
            this.items.push(this.getObjectSelectionPanel());
        this.items.push(this.getFinderCardDeck());

        this.callParent();

        this._initResize();

        this.on({
            finderObjectChanged: this.updateFinderObject
        });
    },

    getObjectSelectionPanel: function() {
        if (!this.objectSelectionPanel) {

            this.objectSelectionPanel = Ext4.create("LABKEY.study.panel.FinderObjectSelection", {
                width: '100%',
                cubeConfigs: this.cubeConfigs
            });
        }
        return this.objectSelectionPanel;
    },

    getFinderCardDeck : function() {
        if (!this.finderCardDeck) {
            this.finderCardDeck = Ext4.create("LABKEY.study.panel.FinderCardDeck", {
                cubeConfigs: this.cubeConfigs,
                dataModuleName: this.dataModuleName
            });
        }
        return this.finderCardDeck;
    },

    _initResize : function() {
        var resize = function(w, h) {
            LABKEY.ext4.Util.resizeToViewport(this, w, h, 46, 32);
        };

        Ext4.EventManager.onWindowResize(resize, this);

        this.on('afterrender', function() {
            Ext4.defer(function() {
                var size = Ext4.getBody().getBox();
                resize.call(this, size.width, size.height);
            }, 300, this);
        });
    },

    updateFinderObject : function(objectName)
    {
        this.getFinderCardDeck().getLayout().setActiveItem(objectName + '-finder-card');
    }

});


