Ext4.define('LABKEY.study.data.FacetMember', {
    extend: 'Ext.data.Model',

    idProperty : 'uniqueName',

    fields: [
        {name: 'name'},
        {name: 'uniqueName'},
        {name: 'count'},
        {name: 'percent'},
        {name: 'facet'},
        {name: 'facetName'},
        {name: 'level'}
    ]

});
