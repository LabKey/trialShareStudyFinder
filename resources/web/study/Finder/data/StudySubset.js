Ext4.define('LABKEY.study.data.StudySubset', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'default', type:'boolean'}
    ]
});