Ext4.define('LABKEY.study.data.AgeGroup', {
    extend: 'Ext.data.Model',

    idProperty: 'ageGroup',

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'lists',
            queryName   : 'ageGroup'
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },
    
    fields: [
        {name: 'ageGroup'}
    ]
});