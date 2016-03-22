/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
// TODO get rid of this
Ext4.define('LABKEY.study.store.Studies', {
    extend: 'LABKEY.study.store.CubeObjects',
    storeId: 'Study',
    model: 'LABKEY.study.data.Study',
    autoLoad: false,

    sorters: [{
        property: 'shortName',
        direction: 'ASC'
    }]
});