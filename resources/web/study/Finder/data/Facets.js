Ext4.define('LABKEY.study.store.Facets', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.Facet',
    storeId: 'facets',
    autoLoad: false,
    dataModuleName: 'study',
    //proxy : {
    //    type: "ajax",
    //    url:  LABKEY.ActionURL.buildURL("trialshare", "studyFacets.api", LABKEY.containerPath),
    //    reader: {
    //        type: 'json',
    //        root: 'data'
    //    }
    //},
    //listeners: {
    //    'load' : {
    //        fn : function(store, records, options) {
    //            console.log('Facet store loaded');
    //            store.computePercents();
    //        },
    //        scope: this
    //    }
    //},

    computePercents: function() {
        console.log("Computing percents now");
    },

    clearAllSelectedMembers: function() {
        for (var f = 0; f < this.count(); f++) {
            this.getAt(f).data.selectedMembers = [];
        }
    },

    selectMembers : function(members) {
        for (var i = 0; i < members.length; i++)
        {
            this.getById(members[i].data.facetName).data.selectedMembers.push(members[i]);
        }
    }
});