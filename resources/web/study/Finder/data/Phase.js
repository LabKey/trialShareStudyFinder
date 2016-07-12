Ext4.define('LABKEY.study.data.Phase', {
    extend: 'Ext.data.Model',

    idProperty: 'phase',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'Phase'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'phase'}
    ]
});