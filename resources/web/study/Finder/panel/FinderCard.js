/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.FinderCard', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    layout: 'border',

    cls: 'labkey-data-finder-view',

    border: false,

    showParticipantFilters: false,

    dataModuleName: null,  // the module responsible for serving up the cube data

    searchTerms : '',

    bubbleEvents : ['detailsChange','countsUpdated'],

    initComponent : function() {

        this.cls += ' labkey-' +  this.cubeConfig.objectName.toLowerCase() + '-finder-card';
        this.items = [
            this.getFacetsPanel(),
            this.getCubeMemberPanel()
        ];

        this.callParent();

        this.getCubeDefinition();

        this.on({
            subsetChanged: this.onSubsetChanged,
            clearAllFilters: this.onClearAllFilters,
            searchTermsChanged: this.onSearchTermsChanged
        });
    },

    getCubeDefinition: function() {
        var me = this;
        this.cube = LABKEY.query.olap.CubeManager.getCube({
            configId: this.cubeConfig.configId,
            schemaName: this.cubeConfig.schemaName,
            name: this.cubeConfig.cubeName,
            container: this.cubeConfig.cubeContainerId,
            containerPath: this.cubeConfig.cubeContainerPath,
            deferLoad: false
        });
        this.cube.onReady(function (m)
        {
            me.mdx = m;
            me.onCubeReady();
        });
    },


    onCubeReady: function() {
        this.getFacetsPanel().onCubeReady(this.mdx);
    },

    onSubsetChanged : function(value) {
        this.getFacetsPanel().onSubsetChanged();
    },

    onClearAllFilters: function() {
        this.getCubeMemberPanel().onClearAllFilters();
    },

    onSearchTermsChanged: function(terms) {
        this.getFacetsPanel().onSearchTermsChanged(terms);
    },

    getFacetsPanel: function() {
        if (!this.facetsPanel) {

            this.facetsPanel = Ext4.create("LABKEY.study.panel.FacetSelection", {
                region: 'west',
                flex: 1,
                minWidth: 300,
                maxWidth: 350,
                dataModuleName: this.dataModuleName,
                showParticipantFilters : this.showParticipantFilters,
                cubeConfig: this.cubeConfig
            });
        }
        return this.facetsPanel;
    },

    getCubeMemberPanel : function() {
        if (this.cubeConfig.objectName == "Study")
            return this.getStudiesPanel();
        else if (this.cubeConfig.objectName == "Publication")
            return this.getPublicationsPanel();
    },

    getStudiesPanel: function() {
        if (!this.studiesPanel) {
            this.studiesPanel = Ext4.create("LABKEY.study.panel.CubeObjects", {
                alias : 'widget.labkey-studies-panel',
                cls: 'labkey-studies-panel',
                cubeConfig: this.cubeConfig,
                dataModuleName: this.dataModuleName,
                region: 'center',
                flex:4
            });
        }
        return this.studiesPanel;
    },

    getPublicationsPanel: function() {
        if (!this.publicationsPanel) {
            this.publicationsPanel = Ext4.create("LABKEY.study.panel.CubeObjects", {
                alias : 'widget.labkey-publications-panel',
                cls: 'labkey-publications-panel',
                cubeConfig: this.cubeConfig,
                dataModuleName: this.dataModuleName,
                region: 'center',
                flex:4
            });
        }
        return this.publicationsPanel;
    }

});


