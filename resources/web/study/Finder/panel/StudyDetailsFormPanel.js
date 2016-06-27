/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.StudyDetailsFormPanel', {
    extend: 'LABKEY.study.panel.CubeObjectDetailsFormPanel',

    stripNewLinesFields : ['ExternalUrlDescription'],

    getFormFields: function()
    {
        var items = [];
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Short Name *',
                    name            : 'ShortName',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Study Id *',
                    name            : 'StudyId',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Title *',
                    name            : 'Title',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    minValue        : 0,
                    fieldLabel      : 'Participant Count',
                    name            : 'ParticipantCount',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                }
        );
        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Study Type',
                    name            : 'StudyType',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    valueField      : 'StudyType',
                    displayField    : 'StudyType',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.StudyType',
                        autoLoad    :   true
                    }
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Icon Url',
                    name            : 'IconUrl',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'External Url',
                    name            : 'ExternalURL',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'External Url Description',
                    name            : 'ExternalUrlDescription',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 50
                });
        items.push(
                {
                    xtype           : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description',
                    name            : 'Description',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 100
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Investigator',
                    name            : 'Investigator',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Age Groups',
                    name            : 'AgeGroups',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumFieldWidth,
                    valueField      : 'AgeGroup',
                    displayField    : 'AgeGroup',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.AgeGroup',
                        autoLoad    :   true
                    }
                });
        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    fieldLabel      : 'Phases',
                    name            : 'Phases',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumFieldWidth,
                    valueField      : 'Phase',
                    displayField    : 'Phase',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.Phase',
                        autoLoad    :   true
                    }
                });
        items.push(
                {
                    xtype           : 'combo',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'Conditions',
                    store : {
                        model   : 'LABKEY.study.data.Condition',
                        autoLoad: true
                    },
                    fieldLabel      : 'Conditions',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'Condition',
                    displayField    : 'Condition',
                    editable        : false,
                    delimiter       : '; ',
                    width           : this.mediumLargeFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'TherapeuticAreas',
                    store : {
                        model   : 'LABKEY.study.data.TherapeuticArea',
                        autoLoad: true
                    },
                    fieldLabel      : 'Therapeutic Areas',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'TherapeuticArea',
                    displayField    : 'TherapeuticArea',
                    editable        : false,
                    delimiter       : '; ',
                    width           : this.mediumFieldWidth
                }
        );
        
        return items;
    }
});