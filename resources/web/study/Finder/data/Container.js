/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.Container', {
    extend: 'Ext.data.Model',

    idProperty: 'entityId',

    containerFilter: "AllFolders",

    proxy : {
        type : 'ajax',
        url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api'),
        extraParams : {
            schemaName  : 'core',
            queryName   : 'containers',
            containerFilter : 'AllFolders',
            'query.columns' : "EntityId,DisplayName,Path",
            'query.queryName': "Containers",
            'query.sort': "DisplayName",
            limit : -1
        },
        reader : {
            type : 'json',
            root : 'rows'
        }
    },

    fields: [
        {name: 'EntityId'},
        {name: 'DisplayName'},
        {name: 'Path'}
    ]
});