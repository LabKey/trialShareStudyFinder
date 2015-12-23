Ext4.define('LABKEY.study.panel.Finder', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.labkey-data-finder-panel',

    layout: 'border',

    cls: 'labkey-data-finder-view',

    border: false,

    showParticipantFilters: false,

    studyData: [],

    studySubsets: null,

    height: '500px',

    dataModuleName: 'study',

    autoScroll : true,

    initComponent : function() {

        this.items = [
            this.getFacetsPanel(),
            this.getStudiesPanel()
        ];

        this.callParent();

        this.getCubeDefinition();

        this._initResize();

        this.on({
                    filterSelectionChanged: this.onFilterSelectionChange,
                    studySubsetChanged: this.onStudySubsetChanged
                    //searchTermsChanged: this.onSearchTermsChanged
                }
        );
    },

    getCubeDefinition: function() {
        var me = this;
        this.cube = LABKEY.query.olap.CubeManager.getCube({
            configId: 'TrialShare:/StudyCube',
            schemaName: 'lists',
            name: 'StudyCube',
            deferLoad: false
        });
        this.cube.onReady(function (m)
        {
            me.mdx = m;
            me.onCubeReady();
            //this.loadFilterState();

            //this.onStudySubsetChanged();
            // doShowAllStudiesChanged() has side-effect of calling updateCountsAsync()
            //$scope.updateCountsAsync();
        });
    },


    onCubeReady: function() {
        this.getFacetsPanel().onCubeReady(this.mdx);
        //this.getStudiesPanel().onCubeReady(this.cube);
    },

    onStudySubsetChanged : function(value) {
        this.getFacetsPanel().onStudySubsetChanged();
    },

    onFilterSelectionChange : function(){
        console.log("Filter selection changed!");
        this.getStudiesPanel().onFilterSelectionChanged();
    },

//    $scope.onStudySubsetChanged = function ()
//{
//    if ($scope.localStorageService.isSupported)
//        $scope.localStorageService.add("studySubset", $scope.studySubset);
//    // if there are search terms, just act as if the search terms have changed
//    if ($scope.searchTerms)
//    {
//        $scope.onSearchTermsChanged();
//    }
//    else
//    {
//        $scope.clearStudyFilter();
//    }
//};
//
//$scope.doSearchTermsChanged_promise = null;
//
//$scope.doSearchTermsChanged = function ()
//{
//    if ($scope.doSearchTermsChanged_promise)
//    {
//        // UNDONE:cancel doesn't seem to really be supported for $http
//        //$scope.http.cancel($scope.doSearchTermsChanged_promise);
//    }
//
//    if (!$scope.searchTerms)
//    {
//        $scope.searchMessage = "";
//        $scope.clearStudyFilter();
//        return;
//    }
//
//    var scope = $scope;
//    var url = LABKEY.ActionURL.buildURL("search", "json", "/home/", {
//        "category": "immport_study",
//        "scope": "Folder",
//        "q": $scope.searchTerms
//    });
//    var promise = $scope.http.get(url);
//    $scope.doSearchTermsChanged_promise = promise;
//    promise.success(function (data)
//    {
//        // NOOP if we're not current (poor man's cancel)
//        if (promise != $scope.doSearchTermsChanged_promise)
//            return;
//        $scope.doSearchTermsChanged_promise = null;
//        var hits = data.hits;
//        var searchStudies = [];
//        var found = {};
//        for (var h = 0; h < hits.length; h++)
//        {
//            var id = hits[h].id;
//            var accession = id.substring(id.lastIndexOf(':') + 1);
//            if (found[accession])
//                continue;
//            found[accession] = true;
//            searchStudies.push("[Study].[" + accession + "]");
//        }
//        if (!searchStudies.length)
//        {
//            $scope.setStudyFilter(searchStudies);
//            $scope.searchMessage = 'No studies match your search criteria';
//        }
//        else
//        {
//            $scope.searchMessage = '';
//            // intersect with study subset list
//            var result = $scope.intersect(searchStudies, $scope.getStudySubsetList());
//            if (!result.length)
//                $scope.searchMessage = 'No studies match your search criteria';
//            $scope.setStudyFilter(result);
//        }
//    });
//};

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
                width: '21%',
                maxWidth: '265px',
                dataModuleName: this.dataModuleName,
                showParticipantFilters : this.showParticipantFilters
            });
        }
        FACETS = this.facetsPanel;
        return this.facetsPanel;
    },

    getStudiesPanel: function() {
        if (!this.studiesPanel) {
            this.studiesPanel = Ext4.create("LABKEY.study.panel.Studies", {
                studySubsets : this.studySubsets,
                showSearch : this.showSearch,
                dataModuleName: this.dataModuleName,
                region: 'center',
                width: '80%',
                id: 'studies-view'
            });
        }
        STUDIES = this.studiesPanel;
        return this.studiesPanel;
    }

});


