Ext4.define('LABKEY.study.data.StudyType', {
    extend: 'Ext.data.Model',

    idProperty: 'studyType',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'studyType'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'studyType'}
    ]
});