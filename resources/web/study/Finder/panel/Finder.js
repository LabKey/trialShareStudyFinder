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

    initComponent : function()
    {
        this.createCubeConfigStore(this.cubeConfigs);

        this.items = [this.getFinderCardDeck()];

        this.callParent();

        this._initResize();

        this.on({
            finderObjectChanged: this.updateFinderObject,
            filterSelectionChanged: this.onFilterSelectionChange,
            clearAllFilters: this.onClearAllFilters
        });
    },

    createCubeConfigStore : function(cubeConfigs) {
        this.cubeConfigStore = Ext4.create("Ext.data.Store", {
            model: 'LABKEY.study.data.CubeConfig',
            storeId: 'CubeConfigs',
            data: cubeConfigs
        });
        for (var i = 0; i < cubeConfigs.length; i++)
        {
            if (cubeConfigs[i].isDefault)
                this.cubeConfigStore.selectedValue = cubeConfigs[i].objectName;
        }
    },

    getControlsPanel: function()
    {
        if (!this.controlsPanel)
        {
            this.controlsPanel = Ext4.create("LABKEY.study.panel.FinderControls");
            //this.controlsPanel = Ext4.create("Ext.Container", {
            //    layout: "hbox",
            //    cls: 'labkey-controls-panel',
            //    items: [
            //            this.getFacetPanelHeader(),
            //            this.getFinderCardPanelHeader()
            //    ]
            //});
        }
        return this.controlsPanel;
    },

    getFacetPanelHeader : function() {
        if (!this.facetPanelHeader) {
            this.facetPanelHeader = Ext4.create("LABKEY.study.panel.FacetPanelHeader", {
                //dataModuleName: this.dataModuleName
            });
        }
        return this.facetPanelHeader;
    },

    getFinderCardPanelHeader : function() {
        if (!this.cardPanelHeaders) {
            //this.cardPanelHeaders = {};
            //this.cubeConfigStore.each(function(cubeConfig)
            //{
            //    this.cardPanelHeaders[cubeConfig.get("objectName")] = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
            //        dataModuleName: cubeConfig.get("dataModuleName"),
            //        padding: 8,
            //        showSearch : cubeConfig.get("showSearch"),
            //        objectName: cubeConfig.get("objectName"),
            //        hidden: cubeConfig.get("objectName") != this.cubeConfigStore.selectedValue
            //    }
            //    );
            //});
            var cubeConfig = this.cubeConfigStore.getById(this.cubeConfigStore.selectedValue);

            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
                dataModuleName: cubeConfig.get("dataModuleName"),
                padding: 8,
                showSearch : cubeConfig.get("showSearch"),
                objectName: cubeConfig.get("objectName")
            });
        }
        return this.cardPanelHeader;
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

    // This seems to be necessary for Firefox (at least)
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
    },

    onFilterSelectionChange: function(hasFilters)
    {
        this.getFacetPanelHeader().onFilterSelectionChange(hasFilters);
    },

    onClearAllFilters: function()
    {
        this.getFinderCardDeck().getLayout().getActiveItem().onClearAllFilters();
    }

});


