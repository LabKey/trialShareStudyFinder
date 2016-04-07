/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.Finder', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    itemId : 'labkey-data-finder-panel',

    layout: 'border',

    cls: 'labkey-data-finder-view',

    border: false,

    searchTerms : '',

    initComponent : function()
    {
        this.createCubeConfigStore(this.cubeConfigs);

        this.items = [this.getFinderCardDeck()];

        this.callParent();

        this._initResize();
        
        this.on({
            finderObjectChanged: this.updateFinderObject,
            render : this.mask
        });

        this.on("countsUpdated", this.unmask, this, {single: true});
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

    getFinderCardDeck : function() {
        if (!this.finderCardDeck) {
            this.finderCardDeck = Ext4.create("LABKEY.study.panel.FinderCardDeck", {
                cubeConfigs: this.cubeConfigs,
                dataModuleName: this.dataModuleName,
                region: 'center'
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

    mask : function() {
        this.getEl().mask("Loading study and publication data ...");
    },

    unmask : function() {
        if (this.getEl().isMasked())
        {
            this.getEl().unmask()
        }
    }

});


