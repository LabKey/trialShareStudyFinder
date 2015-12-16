Ext4.define('LABKEY.study.data.StudyCard', {
    extend: 'Ext.data.Model',

    idProperty : 'studyId',

    fields: [
        {name: 'studyId'},
        {name: 'title'},
        {name: 'url'},
        {name: 'investigator'},
        {name: 'hasManuscript', type: 'boolean'},
        {name: 'isLoaded', type: 'boolean'},
        {name: 'availability'}
    ]
});