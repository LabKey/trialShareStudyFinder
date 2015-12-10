Ext4.define('LABKEY.study.data.Facet', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name'},
        {name: 'caption'},
        {name: 'pluralName'},
        {name: 'filterOptions'},
        {name: 'members'},
        {name: 'currentFilters'}
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'LABKEY.study.data.FacetMember',
            name: 'members',
            associationKey: 'members'
        },
        {
            type: 'hasMany',
            model: 'LABKEY.study.data.FacetFilter',
            name: 'filterOptions',
            associationKey: 'filterOptions'
        },
        {
            type: 'hasMany',
            model: 'LABKEY.study.data.FacetFilter',
            name: 'currentFilters',
            associationKey: 'currentFilters'
        }
    ]
});

Ext4.define('LABKEY.study.data.FacetMember', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name'},
        {name: 'count'}
    ]
});


Ext4.define('LABKEY.study.data.FacetFilter', {
    extend: 'Ext.data.Model',
    fields :[
        {name: 'type'},
        {name: 'caption'}
    ]
});
