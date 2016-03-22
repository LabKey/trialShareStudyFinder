/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.Publications', {
    extend: 'Ext.data.Store',
    storeId: 'Publication',
    model: 'LABKEY.study.data.Publication',
    autoLoad: false,
    proxy : {
        type: "ajax",
        //url: set before calling "load".
        reader: {
            type: 'json',
            root: 'data'
        }
    },
    sorters: [{
        property: 'title',
        direction: 'ASC'
    }],

    updateSearchFilters: function(searchSelectedMembers) {
        this.updateFilters(this.facetSelectedMembers, searchSelectedMembers, this.selectedSubset);
    },

    clearSearchFilters: function() {
        this.searchSelectedMembers = null;
    },

    updateFacetFilters: function(selectedMembers, selectedSubset) {
        this.updateFilters(selectedMembers, this.searchSelectedMembers, selectedSubset)
    },

    clearFacetFilters: function() {
        this.facetSelectedMembers = null;
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

        // this.suspendEvents(false);
        this.clearFilter();
        for (var i = 0; i < this.count(); i++) {
            object = this.getAt(i);
            object.set("isSelected", this.facetSelectedMembers[object.get("id")] !== undefined);
            object.set("isSelectedBySearch", this.searchSelectedMembers == null || this.searchSelectedMembers[object.get("id")] !== undefined);
        }

        // this.resumeEvents();
        this.filter([
            {property: 'isSelected', value: true},
            {property: 'isSelectedBySearch', value: true}
        ]);
    },

    selectAll : function() {
        for (var i = 0; i < this.count(); i++) {
            var object = this.getAt(i);
            object.set("isSelected", true);
        }
    }
});