Ext4.define('LABKEY.study.data.TherapeuticArea', {
    extend: 'Ext.data.Model',

    idProperty: 'therapeuticArea',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'therapeuticArea'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'therapeuticArea'}
    ]
});