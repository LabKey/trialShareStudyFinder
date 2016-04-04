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
        if (searchSelectedMembers == null || searchSelectedMembers) // null is a value we want to retain but undefined is not
            this.searchSelectedMembers = searchSelectedMembers;
        this.updateFilters();
    },

    updateFacetFilters: function(facetSelectedMembers, selectedSubset) {
        if (facetSelectedMembers != undefined)
            this.facetSelectedMembers = facetSelectedMembers;
        if (selectedSubset)
            this.selectedSubset = selectedSubset;
        // console.log("update facet filters with searchSelectedMembers ", this.searchSelectedMembers);
        this.updateFilters()
    },

    updateFilters: function()
    {
        var object;

        this.suspendEvents(false);
        this.clearFilter();
        this.unfilteredCount = this.count();
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