Ext4.define('LABKEY.study.store.Publications', {
    extend: 'Ext.data.Store',
    storeId: 'Publication',
    model: 'LABKEY.study.data.Publication',
    autoLoad: false,
    dataModuleName: "",
    isLoaded: false,
    selectedMembers : {},
    selectedSubset : 'completed',
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

    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.isLoaded = true;
                //store.updateFilters(this.selectedSubset ? null : {}); // initial load should have no studies selected
            },
            scope: this
        }
    },

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
    }
});