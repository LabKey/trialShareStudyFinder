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
    dataModuleName: "",
    selectedSubset : null,
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

    updateFilters: function(selectedMembers, selectedSubset) {
        if (selectedMembers)
            this.selectedMembers = selectedMembers;
        if (selectedSubset)
            this.selectedSubset = selectedSubset;
        var object;

        this.clearFilter();
        for (var i = 0; i < this.count(); i++) {
            object = this.getAt(i);
            object.set("isSelected", this.selectedMembers[object.get("id")] !== undefined);
        }

        this.filter([
            {property: 'isSelected', value: true}
        ]);
    },

    selectAll : function() {
        for (var i = 0; i < this.count(); i++) {
            var object = this.getAt(i);
            object.set("isSelected", true);
        }
    },

    constructor: function(config) {
        config.selectedMembers = {};
        this.callParent(config);
    }
});