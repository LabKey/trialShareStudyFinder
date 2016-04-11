/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.FacetMember', {
    extend: 'Ext.data.Model',

    idProperty : 'uniqueName',

    fields: [
        {name: 'name'},
        {name: 'uniqueName'},
        {name: 'count'},
        {name: 'percent'},
        {name: 'unfilteredPercent'},
        {name: 'facet'},
        {name: 'facetName'},
        {name: 'level'},
        {name: 'unfilteredCount', type: 'int', defaultValue: 0}
    ]

});
