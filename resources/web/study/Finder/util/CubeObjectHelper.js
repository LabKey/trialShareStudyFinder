Ext4.define('LABKEY.study.util.CubeObjectHelper', {
    singleton: true,

    getModelProxy: function(queryName, cubeContainerPath, schemaName)
    {
        var modelProxy = {
            type : 'ajax',
            url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api', cubeContainerPath),
            extraParams : {
                schemaName  : schemaName ? schemaName : 'lists',
                queryName   : queryName
            },
            reader : {
                type : 'json',
                root : 'rows'
            }
        };
        return modelProxy;
    }

});