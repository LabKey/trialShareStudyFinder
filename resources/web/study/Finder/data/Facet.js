Ext4.define('LABKEY.study.data.Facet', {
    extend: 'Ext.data.Model',

    idProperty : 'name',

    fields: [
        {name: 'name'},
        {name: 'pluralName'},
        {name: 'uniqueName'},
        {name: 'members'},
        {name: 'selectedMembers'},
        {name: 'filterOptions'},
        {name: 'currentFilterType'},
        {name: 'currentFilterCaption'},
        {name: 'summaryCount', type:'int', default: 0},
        {name: 'allMemerCount', type:'int', default: 0}

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
            model: 'LABKEY.study.data.FacetMember',
            name: 'selectedMembers',
            associationKey: 'selectedMembers'
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

