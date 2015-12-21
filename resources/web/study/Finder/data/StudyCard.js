Ext4.define('LABKEY.study.data.StudyCard', {
    extend: 'Ext.data.Model',

    idProperty : 'studyId',

    fields: [
        {name: 'studyId'},
        {name: 'title'},
        {name: 'url'},
        {name: 'brand'}, // TODO bring this from the Java side
        {name: 'investigator'},
        {name: 'hasManuscript', type: 'boolean'},
        {name: 'isLoaded', type: 'boolean'}, // TODO isHighlighted
        {name: 'availability'}
    ]
});