/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.StudyDetailsFormPanel', {
    extend: 'LABKEY.study.panel.CubeObjectDetailsFormPanel',

    stripNewLinesFields : ['ExternalUrlDescription'],

    study : null,

    initComponent: function()
    {
        if (this.mode == "insert")
        {
            this.nextStepUrl = LABKEY.ActionURL.buildURL("list", "grid", LABKEY.ActionURL.getContainer(),
                    {
                        listId: this.accessListId,
                        returnUrl: window.location
                    });
        }
        this.callParent();
    },

    getFormFields: function()
    {
        var items = [];
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Short Name *',
                    name            : 'shortName',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Study Id *',
                    name            : 'studyId',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Title *',
                    name            : 'title',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    minValue        : 0,
                    fieldLabel      : 'Participant Count',
                    name            : 'participantCount',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                }
        );
        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    fieldLabel      : 'Study Type',
                    name            : 'studyType',
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
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Icon Url',
                    name            : 'iconUrl',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'External Url',
                    name            : 'externalURL',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'htmleditor',
                    disabled        : this.mode == "view",
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'External Url Description',
                    name            : 'externalUrlDescription',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 100
                });
        items.push(
                {
                    xtype           : 'htmleditor',
                    disabled        : this.mode == "view",
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description',
                    name            : 'description',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 200
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Investigator',
                    name            : 'investigator',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    fieldLabel      : 'Age Groups',
                    name            : 'ageGroups',
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
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    fieldLabel      : 'Phases',
                    name            : 'phases',
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
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'conditions',
                    store : {
                        model   : 'LABKEY.study.data.Condition',
                        autoLoad: true
                    },
                    fieldLabel      : 'Conditions',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'Condition',
                    displayField    : 'Condition',
                    editable        : false,
                    width           : this.mediumLargeFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    multiSelect     : true,
                    delimiter       : this.multiSelectDelimiter,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'therapeuticAreas',
                    store : {
                        model   : 'LABKEY.study.data.TherapeuticArea',
                        autoLoad: true
                    },
                    fieldLabel      : 'Therapeutic Areas',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'TherapeuticArea',
                    displayField    : 'TherapeuticArea',
                    editable        : false,
                    width           : this.mediumFieldWidth
                }
        );

        if (this.mode != "insert")
        {
            items.push(
                    {
                        xtype : 'displayfield',
                        hideLabel: true,
                        width: 175,
                        value: LABKEY.Utils.textLink({
                            href: LABKEY.ActionURL.buildURL("list", "grid", LABKEY.ActionURL.getContainer(),
                                    {
                                        listId: this.accessListId,
                                        'query.StudyId/ShortName~eq' : this.cubeObject.shortName,
                                        returnUrl: window.location}),
                            text: this.mode + ' Study Access Data'})
                    }
            );
        }
        
        return items;
    }
});