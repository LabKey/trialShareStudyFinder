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
        {name: 'subsetLevelName'}
    ]
});