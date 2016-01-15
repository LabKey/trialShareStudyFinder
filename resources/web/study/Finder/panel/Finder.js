Ext4.define('LABKEY.study.panel.Finder', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    itemId : 'labkey-data-finder-panel',

    layout: 'card',

    cls: 'labkey-data-finder-view',

    border: false,

    height: '500px',

    autoScroll : true,

    searchTerms : '',

    initComponent : function() {

        this.items = [];
        var activeIndex = 0;
        for (var i = 0; i < this.cubeConfigs.length; i++)
        {
            this.items.push(Ext4.create('LABKEY.study.panel.FinderCard', {
                dataModuleName: this.dataModuleName,
                olapConfig: this.cubeConfigs[i]
            }));
            if (this.cubeConfigs[i].isDefault)
                acttiveIndex = i;
        }


        this.callParent();

        this.getLayout().setActiveItem(activeIndex);
        this._initResize();
        FINDER = this;

        this.on({
            finderObjectChanged: this.updateFinderObject
        });
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

    updateFinderObject : function(index)
    {
        this.getLayout().setActiveItem(index);
    }

});


