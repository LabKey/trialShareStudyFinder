/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
// Consider: Use "cube.js" app metadata overlay paradigm
Ext4.define('LABKEY.study.data.CubeConfig', {
    extend: 'Ext.data.Model',
    idProperty : 'objectName',

    fields: [
        {name: 'objectName'},
        {name: 'objectNamePlural'},
        {name: 'cubeName'},
        {name: 'dataModuleName'},
        {name: 'configId'},
        {name: 'schemaName'},
        {name: 'showSearch'},
        {name: 'filterByLevel'},
        {name: 'countDistinctLevel'},
        {name: 'filterByFacetUniqueName'},
        {name: 'showParticipantFilters'},
        {name: 'isDefault'},
        {name: 'subsetLevelName'},
        {name: 'searchCategory'},
        {name: 'searchScope'},
        {name: 'cubeContainerPath'},
        {name: 'hasContainerFilter', type: 'boolean', defaultValue: false},
        {name: 'countField'}
    ]
});