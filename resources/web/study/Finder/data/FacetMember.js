Ext4.define('LABKEY.study.data.FacetMember', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name'},
        {name: 'count'},
        {name: 'percent'},
        {name: 'currentFilters'}
    ]
});