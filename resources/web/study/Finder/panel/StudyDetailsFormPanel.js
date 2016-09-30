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
        this.callParent();
    },

    getFormFields: function()
    {
        var me = this;
        var items = [];
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : false,
                    fieldLabel      : 'Short Name *',
                    name            : 'shortName',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    listeners: {
                        afterrender: function(field) {
                            field.focus(false, 500);
                        }
                    }
                });
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : false,
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
                    allowBlank      : false,
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
                        autoLoad    :   true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('studyType', me.cubeContainerPath)
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
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    enableFont      : false,
                    disabled        : this.mode == "view",
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'External Url Description',
                    name            : 'externalUrlDescription',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'htmleditor',
                    enableFont      : false,
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
                        autoLoad    :   true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('ageGroup', me.cubeContainerPath)
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
                        autoLoad    :   true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('Phase', me.cubeContainerPath)
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
                        model       : 'LABKEY.study.data.Condition',
                        autoLoad    : true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('condition', me.cubeContainerPath)
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
                        model       : 'LABKEY.study.data.TherapeuticArea',
                        autoLoad    : true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('therapeuticArea', me.cubeContainerPath)
                    },
                    fieldLabel      : 'Therapeutic Areas',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'TherapeuticArea',
                    displayField    : 'TherapeuticArea',
                    editable        : false,
                    width           : this.mediumFieldWidth
                }
        );


        items.push(
                Ext4.create('Ext4.Panel', {
                    layout: {
                        type: 'hbox',
                        align: 'top'
                    },
                    margin: '15 0 0 0',
                    border: false,
                    items: [{
                        xtype: 'label',
                        cls: 'studyaccesstuplelabel',
                        width: this.defaultFieldLabelWidth,
                        html: '<span>Study Access: </span>'
                    },
                        Ext4.create('LABKEY.study.panel.StudyAccessTuplePanel', {
                            studyaccesslist: this.studyaccesslist,
                            width: this.largeFieldWidth,
                            mode: this.mode,
                            cubeContainerPath: this.cubeContainerPath
                        })
                    ],
                    scope: this
                })
        );

        return items;
    },
    getFieldValues : function()
    {
        var studyFields = this.callParent();
        studyFields.studyAccessList = [];
        var formCmps = Ext4.ComponentQuery.query("#studyaccessform");
        Ext4.each(formCmps, function(formCmp){
            var studyAccessValues = {};
            formCmp.getForm().getFields().each(function(item)
            {
                var value = item.value;
                if (value && item.isStudyAccess)
                {
                    studyAccessValues[item.name] = value;
                }
            }, this);
            studyFields.studyAccessList.push(studyAccessValues);
        });

        return studyFields;
    }
});