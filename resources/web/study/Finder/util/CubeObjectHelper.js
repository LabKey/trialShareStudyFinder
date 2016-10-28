/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
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