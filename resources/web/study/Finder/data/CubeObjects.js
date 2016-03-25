/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.CubeObjects', {
    extend: 'Ext.data.Store',
    autoLoad: false,

    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.updateFacetFilters(store.selectedSubset ? null : {}); // initial load should have no objects selected
            }
        }
    },
    
    setSearchFilters: function(searchSelectedMembers) {
        this.searchSelectedMembers = searchSelectedMembers;
    },

    updateSearchFilters: function(searchSelectedMembers) {
        this.updateFilters(this.facetSelectedMembers, searchSelectedMembers, this.selectedSubset);
    },

    updateFacetFilters: function(selectedMembers, selectedSubset) {
        // console.log("update facet filters with searchSelectedMembers ", this.searchSelectedMembers);
        this.updateFilters(selectedMembers, this.searchSelectedMembers, selectedSubset)
    },

    updateFilters: function(facetSelectedMembers, searchSelectedMembers, selectedSubset)
    {
        if (facetSelectedMembers != undefined)
            this.facetSelectedMembers = facetSelectedMembers;
        if (searchSelectedMembers == null || searchSelectedMembers) // null is a value we want to retain but undefined is not
            this.searchSelectedMembers = searchSelectedMembers;
        if (selectedSubset)
            this.selectedSubset = selectedSubset;

        var object;

        this.suspendEvents(false);
        this.clearFilter();
        for (var i = 0; i < this.count(); i++) {
            object = this.getAt(i);
            object.set({
                "isSelected":this.facetSelectedMembers[object.get(object.idProperty)] !== undefined,
                "isSelectedBySearch": this.searchSelectedMembers == null || this.searchSelectedMembers[object.get(object.idProperty)] !== undefined
            });
        }
        this.resumeEvents();

        this.filter([
            {property: 'isSelected', value: true},
            {property: 'isSelectedBySearch', value: true}
        ]);
    },

    selectAll : function() {
        for (var i = 0; i < this.count(); i++) {
            var cubeObj = this.getAt(i);
            cubeObj.set({
                "isSelected":  true,
                "isSelectedBySearch": true
            });
        }
    }
});