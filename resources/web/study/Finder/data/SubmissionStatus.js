Ext4.define('LABKEY.study.data.SubmissionStatus', {
    extend: 'Ext.data.Model',

    idProperty: 'status',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'submissionStatus'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'Status'}
    ]
});