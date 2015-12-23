Ext4.define('LABKEY.study.data.StudyCard', {
    extend: 'Ext.data.Model',

    idProperty : 'studyId',

    fields: [
        {name: 'studyId'},
        {name: 'title'},
        {name: 'url'},
        {name: 'shortName'},
        {name: 'iconUrl'},
        {name: 'investigator'},
        {name: 'hasManuscript', type: 'boolean'},
        {name: 'isLoaded', type: 'boolean'}, // TODO isHighlighted
        {name: 'availability'},
        {name: 'isPublic', type: 'boolean'},
        {name: 'manuscriptCount', type: 'int'},
        {name: 'participantCount', type: 'int'},
        {name: 'isSelected', type: 'boolean'}
    ]
});