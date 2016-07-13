Ext4.define('LABKEY.study.data.Condition', {
    extend: 'Ext.data.Model',

    idProperty: 'condition',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'condition'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'condition'}
    ]
});