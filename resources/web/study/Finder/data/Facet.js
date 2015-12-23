Ext4.define('LABKEY.study.data.Facet', {
    extend: 'Ext.data.Model',

    idProperty : 'name',

    fields: [
        {name: 'name'},
        {name: 'pluralName'}, // TODO not currently used
        {name: 'members'},
        {name: 'selectedMembers'},
        {name: 'filterOptions'},
        {name: 'memberMap'}, // TODO is this used?
        {name: 'currentFilterType'},
        {name: 'currentFilterCaption'},
        {name: 'allMemberCount', type:'int', defaultValue: 0}, // TODO not currently used
        {name: 'hierarchy'},
        {name: 'hierarchyName'},
        {name: 'levelName'},
        {name: 'allMemberName'},
        {name: 'ordinal'},
        {name: 'isExpanded', type:'boolean', defaultValue: true}

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


