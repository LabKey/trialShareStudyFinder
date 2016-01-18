Ext4.define('LABKEY.study.data.Subset', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'isDefault', type:'boolean'}
    ]
});