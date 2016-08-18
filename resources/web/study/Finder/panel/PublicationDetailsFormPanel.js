/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

Ext4.define('LABKEY.study.panel.PublicationDetailsFormPanel', {
    extend: 'LABKEY.study.panel.CubeObjectDetailsFormPanel',
    
    dataModuleName: 'TrialShare',
    cubeContainerPath: 'TrialShare',
    stripNewLinesFields: ['Keywords','Author','Citation'],

    scope: this,

    constructor : function(config) {
        this.callParent([config]);

        this.addListener('dirtychange', function(cmp, isDirty){
            this.changeLink(isDirty);
        }, this);
    },

    listeners : {
        afterrender :
        {
            // reset wasDirty properties after render so dirtychange listener will work properly (should only trigger on user changes, not initial population)
            fn: function() {
                this.changeLink(false);
                var items = this.getForm().getFields().items,
                        i = 0,
                        len = items.length;
                for(; i < len; i++) {
                    var c = items[i];
                    if(c.mixins && c.mixins.field && typeof c.mixins.field['initValue'] == 'function' && c.wasDirty)
                    {
                        c.mixins.field.initValue.apply(c);
                        c.wasDirty = false;
                    }
                }
            }
        }
    },

    changeLink: function (isDirty)
    {
        var studyIds = this.getForm().getValues().studyIds;
        var linkText = 'Create/Find Manuscript on Workbench';

        if(!isDirty && studyIds && (studyIds.length !== 0) && LABKEY.ActionURL.getParameter('id'))
        {
            this.getForm().findField('workbenchUrl').setValue(
                    '<a href="' + LABKEY.contextPath + '/project/Studies/' + studyIds[0] + 'OPR/Study%20Data/begin.view?pageId=Manuscripts?publicationId='+ LABKEY.ActionURL.getParameter('id') + '">' + linkText + '</a>');
        }
        else
        {
            this.getForm().findField('workbenchUrl').setValue(linkText);
        }
    },
    
    getFormFields: function()
    {
        var items = [];

        items.push(
                {
                    xtype : 'hidden',
                    name: 'id'
                });

        items.push(
                {
                    xtype           : 'checkbox',
                    disabled        : this.mode == "view",
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Show on Dashboard',
                    name            : 'show',
                    labelWidth      : this.defaultFieldLabelWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    allowBlank      : false,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Title *',
                    name            : 'title',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    scope           : this
                });

        items.push(
                {
                    xtype           : 'combo',
                    allowBlank      : false,
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Publication Type *',
                    name            : 'publicationType',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    valueField      : 'PublicationType',
                    displayField    : 'PublicationType',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.PublicationType',
                        autoLoad    :   true
                    }
                });

        items.push(
                {
                    xtype           : 'combo',
                    allowBlank      : false,
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Status *',
                    name            : 'status',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    valueField      : 'Status',
                    displayField    : 'Status',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.Status',
                        autoLoad    :   true
                    }
                });

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Submission Status',
                    name            : 'submissionStatus',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    valueField      : 'Status',
                    displayField    : 'Status',
                    editable        : false,
                    store           : {
                        model       : 'LABKEY.study.data.SubmissionStatus',
                        autoLoad    :   true
                    }
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Author',
                    name            : 'author',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 50
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Citation',
                    name            : 'citation',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 50
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    minValue        : 1440, // can't have published anything before the printing press was created
                    maxValue        : new Date().getFullYear() + 9,
                    fieldLabel      : 'Year',
                    name            : 'year',
                    labelWidth      : this.defaultFieldLabelWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Journal/Meeting',
                    name            : 'journal',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumLargeFieldWidth
                });

        items.push(
                {
                    xtype           : 'htmleditor',
                    disabled        : this.mode == "view",
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Abstract',
                    name            : 'abstractText',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 150
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'DOI',
                    name            : 'DOI',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    spinUpEnabled   : false,
                    minValue        : 1,
                    spinDownEnabled : false,
                    fieldLabel      : 'PMID',
                    name            : 'PMID',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'PMCID',
                    name            : 'PMCID',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    regex           : /PMC\d+/
                });


        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Keywords',
                    name            : 'keywords',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumFieldWidth,
                    height          : 50
                });

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    multiSelect     : true,
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
                    delimiter       : this.multiSelectDelimiter,
                    width           : this.mediumFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    multiSelect     : true,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'studyIds',
                    store           : {
                        model   : 'LABKEY.study.data.Study',
                        proxy : {
                            type: "ajax",
                            url : LABKEY.ActionURL.buildURL(this.dataModuleName, "studies.api", this.cubeContainerPath),
                            reader: {
                                type: 'json',
                                root: 'data'
                            }
                        },
                        sorters : [
                            {
                                property: 'shortName',
                                direction: 'ASC'
                            }
                        ],
                        autoLoad    : true
                    },
                    fieldLabel      : 'Studies',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'studyId',
                    displayField    : 'shortName',
                    editable        : false,
                    delimiter       : this.multiSelectDelimiter,
                    width           : this.mediumFieldWidth,
                    scope           : this
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Document URL',
                    name            : 'link1',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Document Description',
                    name            : 'description1',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Link 2',
                    name            : 'link2',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description 2',
                    name            : 'description2',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Link 3',
                    name            : 'link3',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description 3',
                    name            : 'description3',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'manuscriptContainer',
                    store           : {
                        model   : 'LABKEY.study.data.Container',
                        autoLoad: true
                    },
                    fieldLabel      : 'Manuscript Container',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'EntityId',
                    displayField    : 'Path',
                    editable        : false,
                    width           : this.mediumLargeFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'permissionsContainer',
                    store           : {
                        model   : 'LABKEY.study.data.Container',
                        autoLoad: true
                    },
                    fieldLabel      : 'Permissions Container',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'EntityId',
                    displayField    : 'Path',
                    editable        : false,
                    width           : this.mediumLargeFieldWidth
                });

        return items;
    }
});