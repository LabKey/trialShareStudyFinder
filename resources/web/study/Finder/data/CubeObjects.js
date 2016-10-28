/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.CubeObjects', {
    extend: 'Ext.data.Store',
    autoLoad: false,

    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.isLoaded = true;
            },
            scope: this
        }
    },

    setSearchFilters: function(searchSelectedMembers) {
        // console.log("setting searchSelectedMembers ", searchSelectedMembers);
        this.searchSelectedMembers = searchSelectedMembers;
    },

    updateSearchFilters: function(searchSelectedMembers) {
        // console.log("searchSelectedMembers: ", searchSelectedMembers);
        if (searchSelectedMembers == null || searchSelectedMembers) // null is a value we want to retain but undefined is not
            this.searchSelectedMembers = searchSelectedMembers;
        this.updateFilters();
    },

    updateFacetFilters: function(facetSelectedMembers, selectedSubset) {
        // console.log("updateFacetFilters: this.facetSelectedMembers ", this.facetSelectedMembers, " facetSelectedMembers ", facetSelectedMembers, " this.selectedSubset ", this.selectedSubset, " selectedSubset ", selectedSubset);
        if (facetSelectedMembers != undefined)
            this.facetSelectedMembers = facetSelectedMembers;
        if (selectedSubset)
            this.selectedSubset = selectedSubset;
        // console.log("update facet filters with searchSelectedMembers ", this.searchSelectedMembers);
        this.updateFilters()
    },

    setUnfilteredCount: function()
    {
        if (!this.countField || this.countField == this.storeId)
            this.unfilteredCount = this.count();
        else
            this.unfilteredCount = this.sum(this.countField);
    },

    updateFilters: function()
    {
        var object;

        // console.log("updateFilters with this.searchSelectedMembers ", this.searchSelectedMembers, " this.facetSelectedMembers ", this.facetSelectedMembers);
        this.suspendEvents(false);
        this.clearFilter();
        this.setUnfilteredCount();
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