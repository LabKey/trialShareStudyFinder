Ext4.define('LABKEY.study.data.StudyCard', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'accession'},
        {name: 'title'},
        {name: 'url'},
        {name: 'investigator'},
        {name: 'isLoaded', type: 'boolean'}
    ]
});