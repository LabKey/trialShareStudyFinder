Ext4.define('LABKEY.study.data.Status', {
    extend: 'Ext.data.Model',

    idProperty: 'status',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'status'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'status'}
    ]
});