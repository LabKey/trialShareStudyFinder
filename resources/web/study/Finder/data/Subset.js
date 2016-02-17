/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.Subset', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id'},
        {name: 'name'},
        {name: 'isDefault', type:'boolean'}
    ]
});