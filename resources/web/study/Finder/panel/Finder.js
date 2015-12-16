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

        this._initResize();

        this.on(
                'filterSelectionChanged', this.onFilterSelectionChange
                //'studySubsetChanged', this.onStudySubsetChanged,
                //'searchTermsChanged', this.onSearchTermsChanged
        );
    },

    //onStudySubsetChanged : function(value) {
    //    this.getStudiesPanel().getStudyCards().store.filter('availability', value);
    //},
    //
    onFilterSelectionChange : function(){
        console.log("Filter selection changed!");
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


