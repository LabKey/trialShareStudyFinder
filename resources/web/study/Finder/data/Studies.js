Ext4.define('LABKEY.study.store.Studies', {
    extend: 'Ext.data.Store',
    storeId: 'studies',
    model: 'LABKEY.study.data.StudyCard',
    autoLoad: false,
    dataModuleName: "study",
    isLoaded: false,
    selectedStudies : {},
    selectedSubset : 'public',
    proxy : {
        type: "ajax",
        //url: set before calling "load".
        reader: {
            type: 'json',
            root: 'data'
        }
    },
    sorters: [{
        property: 'studyId',
        direction: 'ASC'
    }],

    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.isLoaded = true;
                store.updateFilters(this.selectedSubset ? null : {}); // initial load should have no studies selected
            },
            scope: this
        }
    },

    updateFilters: function(selectedStudies, selectedSubset) {
        if (selectedStudies)
            this.selectedStudies = selectedStudies;
        if (selectedSubset)
            this.selectedSubset = selectedSubset;
        var study;

        this.clearFilter();
        for (var i = 0; i < this.count(); i++) {
            study = this.getAt(i);
            study.set("isSelected", this.selectedStudies[study.get("shortName")] !== undefined);
        }

        this.filter([
                {property: 'isSelected', value: true},
                {property: 'isPublic', value: this.selectedSubset !== "operational"}
        ]);
    },

    selectAll : function() {
        for (var i = 0; i < this.count(); i++) {
            study = this.getAt(i);
            study.set("isSelected", true);
        }
    },

    selectSubset : function(subsetId, subset) {

    }
});