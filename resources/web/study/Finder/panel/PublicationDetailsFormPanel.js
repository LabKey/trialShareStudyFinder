/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.PublicationDetailsFormPanel', {
    extend: 'LABKEY.study.panel.CubeObjectDetailsFormPanel',

    publicationId: null,

    dataModuleName: 'TrialShare',
    cubeContainerPath: 'TrialShare',
    stripNewLinesFields: ['Keywords','Author','Citation'],
    getFormFields: function()
    {
        var items = [];
        items.push(
                {
                    xtype : 'hidden',
                    name: 'Key'
                });
        items.push(
                {
                    xtype           : 'checkbox',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Show on Dashboard',
                    name            : 'Show',
                    labelWidth      : this.defaultFieldLabelWidth
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
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Publication Type *',
                    name            : 'PublicationType',
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
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Status *',
                    name            : 'Status',
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
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Submission Status',
                    name            : 'SubmissionStatus',
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
                    xtype           : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Author',
                    name            : 'Author',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 50
                }
        );
        items.push(
                {
                    xtype           : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Citation',
                    name            : 'Citation',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 50
                }
        );
        items.push(
                {
                    xtype           : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    // spinUpEnabled   : false,
                    // spinDownEnabled : false,
                    minValue        : 1440, // can't have published anything before the printing press was created
                    maxValue        : new Date().getFullYear() + 9,
                    fieldLabel      : 'Year',
                    name            : 'Year',
                    labelWidth      : this.defaultFieldLabelWidth
                }
        );

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Journal',
                    name            : 'Journal',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumLargeFieldWidth
                });

        items.push(
                {
                    xtype           : 'htmleditor',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Abstract',
                    name            : 'AbstractText',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth,
                    height          : 150
                }
        );

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'DOI',
                    name            : 'doi',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });


        items.push(
                {
                    xtype           : 'numberfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    spinUpEnabled   : false,
                    spinDownEnabled : false,
                    fieldLabel      : 'PMID',
                    name            : 'pmid',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'PMCID',
                    name            : 'pmcid',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth
                });

        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'ManuscriptContainer',
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
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'PermissionsContainer',
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
                }
        );

        items.push(
                {
                xtype : 'combo',
                multiSelect : true,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                name        : 'TherapeuticAreas',
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
                width      : 500
                }
        );

        items.push(
                {
                    xtype           : 'textarea',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    stripNewLines   : true,
                    fieldLabel      : 'Keywords',
                    name            : 'Keywords',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.mediumFieldWidth,
                    height          : 50
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    multiSelect     : true,
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    name            : 'StudyIds',
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
                        autoLoad    : true
                    },
                    fieldLabel      : 'Studies',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'studyId',
                    displayField    : 'shortName',
                    editable        : false,
                    delimiter       : this.multiSelectDelimiter,
                    width           : this.mediumFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    multiSelect     : true,
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
                    delimiter       : this.multiSelectDelimiter,
                    width           : this.mediumLargeFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'combo',
                    multiSelect     : true,
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
                    delimiter       : this.multiSelectDelimiter,
                    width           : this.mediumFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Link 1',
                    name            : 'Link1',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description 1',
                    name            : 'Description1',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                }
        );

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Link 2',
                    name            : 'Link2',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description 2',
                    name            : 'Description2',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Link 3',
                    name            : 'Link3',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });

        items.push(
                {
                    xtype           : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    fieldLabel      : 'Description 3',
                    name            : 'Description3',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.largeFieldWidth
                });
        return items;
    }
});