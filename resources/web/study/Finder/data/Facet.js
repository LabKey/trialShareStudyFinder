Ext4.define('LABKEY.study.data.Facet', {
    extend: 'Ext.data.Model',

    idProperty : 'name',

    fields: [
        {name: 'name'},
        {name: 'pluralName'},
        {name: 'members'},
        {name: 'selectedMembers'},
        {name: 'memberMap'},
        {name: 'currentFilterType'},
        {name: 'currentFilterCaption'},
        {name: 'summaryCount', type:'int', default: 0},
        {name: 'allMemberCount', type:'int', default: 0},
        {name: 'hierarchy'},
        {name: 'hierarchyName'},
        {name: 'levelName'},
        {name: 'allMemberName'},
        {name: 'ordinal'}

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


