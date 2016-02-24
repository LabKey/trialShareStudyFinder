/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.StudyContainer', {
    extend: 'Ext.data.Model',

    idProperty : 'key',

    fields: [
        {name: 'key'},
        {name: 'displayName'},
        {name: 'studyId'},
        {name: 'studyContainerPath'},
        {name: 'visibility'}
    ]

});