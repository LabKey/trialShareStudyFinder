Ext4.define('LABKEY.study.data.Facet', {
    extend: 'Ext.data.Model',

    idProperty : 'name',

    fields: [
        {name: 'name'},
        {name: 'pluralName'}, // not currently used
        {name: 'members'},
        {name: 'selectedMembers'},
        {name: 'filterOptions'},
        {name: 'currentFilterType'},
        {name: 'currentFilterCaption'},
        {name: 'allMemberCount', type:'int', defaultValue: 0}, // set but not currently used
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


