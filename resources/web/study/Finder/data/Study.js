/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.Study', {
    extend: 'Ext.data.Model',

    idProperty : 'studyId',

    proxy : {
        type: "ajax",
        //url: set before calling "load".
        reader: {
            type: 'json',
            root: 'data'
        }
    },

    fields: [
        {name: 'studyId'},
        {name: 'title'},
        {name: 'url'},
        {name: 'externalUrl'},
        {name: 'externalUrlDescription'},
        {name: 'shortName'},
        {name: 'iconUrl'},
        {name: 'investigator'},
        {name: 'visibility'},
        {name: 'isPublic', type: 'boolean'},
        {name: 'abstractCount', type: 'int'},
        {name: 'manuscriptCount', type: 'int'},
        {name: 'participantCount', type: 'int'},
        {name: 'isSelected', type: 'boolean', defaultValue: true},
        {name: 'isSelectedBySearch', type: 'boolean', defaultValue: false},
        {name: 'isHighlighted', type: 'boolean', defaultValue: false},
        {name: 'isBorderHighlighted', type: 'boolean', defaultValue: false},
        {name: 'studyAccessList'}
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'LABKEY.study.data.StudyAccess',
            name: 'studyAccessList',
            associationKey: 'studyAccessList'
        }
    ]
});