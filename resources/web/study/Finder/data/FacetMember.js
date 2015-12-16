Ext4.define('LABKEY.study.data.FacetMember2', {
    extend: 'Ext.data.Model',

    idProperty : 'uniqueName',

    fields: [
        {name: 'name'},
        {name: 'uniqueName'},
        {name: 'count'},
        {name: 'percent'},
        {name: 'facetName'},
        {name: 'facetUniqueName'}
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'LABKEY.study.data.FacetFilter',
            name: 'filterOptions',
            associationKey: 'filterOptions'
        }
    ]
});
