Ext4.define('LABKEY.study.data.Visibility', {
    extend: 'Ext.data.Model',

    idProperty: 'visibility',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'visibility'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },

    fields: [
        {name: 'visibility'}
    ]
});