Ext4.define('LABKEY.study.data.Study', {
    extend: 'Ext.data.Model',

    idProperty : 'studyId',

    fields: [
        {name: 'studyId'},
        {name: 'title'},
        {name: 'url'},
        {name: 'externalUrl'},
        {name: 'externalUrlDescription'},
        {name: 'shortName'},
        {name: 'iconUrl'},
        {name: 'investigator'},
        {name: 'isPublic', type: 'boolean'},
        {name: 'abstractCount', type: 'int'},
        {name: 'manuscriptCount', type: 'int'},
        {name: 'participantCount', type: 'int'},
        {name: 'isSelected', type: 'boolean'}
    ]
});