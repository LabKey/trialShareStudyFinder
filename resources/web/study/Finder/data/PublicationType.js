Ext4.define('LABKEY.study.data.PublicationType', {
    extend: 'Ext.data.Model',

    idProperty: 'PublicationType',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'publicationType'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'PublicationType'}
    ]
});