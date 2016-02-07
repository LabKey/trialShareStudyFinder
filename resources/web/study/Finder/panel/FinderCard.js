Ext4.define('LABKEY.study.panel.FinderCard', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    layout: 'border',

    cls: 'labkey-data-finder-view',

    border: false,

    showParticipantFilters: false,

    dataModuleName: null,  // the module responsible for serving up the cube data

    autoScroll : true,

    searchTerms : '',

    initComponent : function() {

        this.cls += ' labkey-' +  this.cubeConfig.objectName.toLowerCase() + '-finder-card';
        this.items = [
            this.getFacetsPanel()
        ];

        this.items.push(this.getCubeMemberPanel());

        this.callParent();

        this.getCubeDefinition();

        this._initResize();

        this.on({
            //filterSelectionChanged: this.onFilterSelectionChange,
            subsetChanged: this.onSubsetChanged,
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
            //this.loadFilterState();

            //this.onSubsetChanged();
            // doShowAllStudiesChanged() has side-effect of calling updateCountsAsync()
            //$scope.updateCountsAsync();
        });
    },


    onCubeReady: function() {
        this.getFacetsPanel().onCubeReady(this.mdx);
    },

    onSubsetChanged : function(value) {
        this.getFacetsPanel().onSubsetChanged();
    },

    //onFilterSelectionChange : function(){
    //    console.log("Filter selection changed!");
    //    this.getStudiesPanel().onFilterSelectionChanged();
    //},

    onSearchTermsChanged: function(searchTerms) {

        if (!searchTerms)
        {
            this.searchMessage = "";
            this.clearStudyFilter();
            return;
        }

        var url = LABKEY.ActionURL.buildURL("search", "json", "/home/", {
            "category": "List",
            "q": searchTerms
        });
        Ext4.Ajax.request({
            url: url,
            success: function (response)
            {
                //// NOOP if we're not current (poor man's cancel)
                //if (promise != $scope.doSearchTermsChanged_promise)
                //    return;
                //$scope.doSearchTermsChanged_promise = null;
                var data = Ext4.decode(response.responseText);
                var hits = data.hits;
                var searchStudies = [];
                var found = {};
                for (var h = 0; h < hits.length; h++)
                {
                    var id = hits[h].id;
                    var accession = id.substring(id.lastIndexOf(':') + 1);
                    if (found[accession])
                        continue;
                    found[accession] = true;
                    searchStudies.push("[Study].[" + accession + "]");
                }
                if (!searchStudies.length)
                {
                    console.log("No studies match your search criteria");
                    //$scope.setStudyFilter(searchStudies);
                    //$scope.searchMessage = 'No studies match your search criteria';
                }
                else
                {
                    console.log("found " + searchStudies.length + " studies matching terms " + searchTerms);
                    //$scope.searchMessage = '';
                    //// intersect with study subset list
                    //var result = $scope.intersect(searchStudies, $scope.getStudySubsetList());
                    //if (!result.length)
                    //    $scope.searchMessage = 'No studies match your search criteria';
                    //$scope.setStudyFilter(result);
                }
            }
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

    getFacetsPanel: function() {
        if (!this.facetsPanel) {

            this.facetsPanel = Ext4.create("LABKEY.study.panel.FacetSelection", {
                region: 'west',
                width: '20%',
                maxWidth: '265px',
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
            this.studiesPanel = Ext4.create("LABKEY.study.panel.Studies", {
                showSearch : this.cubeConfig.showSearch,
                dataModuleName: this.dataModuleName,
                cubeContainerPath: this.cubeConfig.cubeContainerPath,
                region: 'center',
                width: '80%'
            });
        }
        return this.studiesPanel;
    },

    getPublicationsPanel: function() {
        if (!this.publicationsPanel) {
            this.publicationsPanel = Ext4.create("LABKEY.study.panel.Publications", {
                showSearch : this.cubeConfig.showSearch,
                dataModuleName: this.dataModuleName,
                cubeContainerPath: this.cubeConfig.cubeContainerPath,
                region: 'center',
                width: '80%'
            });
        }
        return this.publicationsPanel;
    }

});


