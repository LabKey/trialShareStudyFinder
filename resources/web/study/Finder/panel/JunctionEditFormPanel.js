/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.JunctionEditFormPanel', {
    extend: 'LABKEY.ext4.FormPanel',

    cls: 'labkey-data-finder-junction-editor',

    defaultFieldWidth: 350,
    defaultFieldLabelWidth: 200,
    mode: "view",

    initComponent : function() {

        this.manageDataUrl = LABKEY.ActionURL.buildURL('trialShare', 'manageData.view', null, {objectName : this.objectName, 'query.viewName': 'manageData'});

        if (this.mode == "view")
            this.dockedItems = [];
        else
            this.dockedItems = [this.getToolBar()];
        this.callParent();
    },

    getToolBar : function()
    {
        if (!this.toolbar)
        {
            this.toolBar = {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                style: 'background-color: transparent;'
            };
            this.toolBar.items = [
                {
                    text: 'Submit',
                    formBind: true,
                    successURL: LABKEY.ActionURL.getParameter('returnUrl') || this.manageDataUrl,
                    handler: function (btn)
                    {
                        var panel = btn.up('form');
                        panel.doSubmit(btn);
                    }
                },
                {
                    text: 'Cancel',
                    returnUrl: LABKEY.ActionURL.getParameter('returnUrl') || this.manageDataUrl,
                    handler: function (btn, key)
                    {
                        window.location = btn.returnUrl;
                    }
                }
            ];
        }
        return this.toolBar;
    },

    shouldShowInInsertView: function(metadata) {
        return this.isJoinTableField(metadata.name) || LABKEY.ext4.Util.shouldShowInUpdateView(metadata);
    },

    shouldShowInDisplayView: function(metadata) {
        var record = this.store.getAt(0); // there will only be a single item in the store when in view mode
        if (record)
        {
            var field = record.get(metadata.name);
            return field !== undefined && field !== ""
        }
        return false;
    },

    shouldShowInView: function(metadata) {
        if (this.mode == "update" || this.mode == "insert")
            return this.shouldShowInInsertView(metadata);
        else
            return this.shouldShowInDisplayView(metadata);
    },

    configureJoinFields : function(store) {
        var fields = LABKEY.ext4.Util.getStoreFields(this.store);
        for (var i = 0; i < this.joinTableFields.length; i++)
        {
            var field = fields.get(this.joinTableFields[i]);
            if (field)
            {
                field.editable = true;
                field.facetingBehaviorType = "AUTOMATIC";
                field.multiSelect = true;
                field.convert = function(v, record)
                {
                    console.log(v, record);
                    if (Ext4.isArray(v))
                        return v;
                    else if (Ext4.isString(v))
                        return v.split(",");
                }
            }
        }
    },

    isJoinTableField : function(fieldName)
    {
        return this.joinTableFields.indexOf(fieldName) >= 0;
    },

    /** Override **/
    configureForm: function(store){
        this.configureJoinFields(store);
        var toAdd = [];
        toAdd.push({
            tag: 'div',
            itemId: 'messageEl',
            html:'Items marked with * are required',
            border: false,
            cls: 'labkey-data-finder-editor-message'
        });

        LABKEY.ext4.Util.getStoreFields(store).each(function(field){
            var config = {
                queryName: store.queryName,
                schemaName: store.schemaName
            };

            if (this.metadataDefaults) {
                Ext4.Object.merge(config, this.metadataDefaults);
            }
            if (this.metadata && this.metadata[field.name]) {
                Ext4.Object.merge(config, this.metadata[field.name]);
            }

            if (this.shouldShowInView(field)){

                var fieldEditor = LABKEY.ext4.Util.getFormEditorConfig(field, config);
                fieldEditor.cls = 'labkey-field-editor';
                fieldEditor.labelCls ='labkey-field-editor-label';
                if (fieldEditor.isRequired && this.mode != "view")
                    fieldEditor.fieldLabel = fieldEditor.fieldLabel + " *";
                if (!fieldEditor.width)
                    fieldEditor.width = this.defaultFieldWidth;
                if (!fieldEditor.labelWidth)
                    fieldEditor.labelWidth = this.defaultFieldLabelWidth;

                if (field.inputType == 'textarea' && fieldEditor.xtype == 'textarea' && !fieldEditor.height){
                    Ext4.apply(fieldEditor, {width: this.defaultFieldWidth, height: 100});
                }

                if (field.inputType == "checkbox" && field.jsonType == "boolean")
                {
                    fieldEditor.inputValue = true;
                }

                if (fieldEditor.xtype == 'combo' || fieldEditor.xtype == 'labkey-combo'){
                    fieldEditor.store.containerFilter = fieldEditor.containerFilter;
                    fieldEditor.multiSelect = field.multiSelect;
                    fieldEditor.store.autoLoad = true;
                    fieldEditor.delimiter = '; ';
                }

                if (field.isAutoIncrement){
                    fieldEditor.xtype = 'displayfield';
                }

                if (this.mode == "view")
                    fieldEditor.xtype = 'displayfield';

                if (!field.compositeField)
                    toAdd.push(fieldEditor);
                else
                    console.warn("Composite field encountered", field);
            }
        }, this);

        return toAdd;
    },


    /** Override **/
    doSubmit: function(btn){
        btn.setDisabled(true);

        // force record to refresh based on most recent form values.  this happens reliably in modern browsers,
        // but IE8 sometimes wont apply changes when the cursor is still on a field
        var plugin = this.getPlugin('labkey-databind');
        plugin.updateRecordFromForm();

        if (!this.store.getNewRecords().length && !this.store.getUpdatedRecords().length && !this.store.getRemovedRecords().length){
            Ext4.Msg.alert('No changes', 'There are no changes. Nothing to do.');
            btn.setDisabled(false);
            return;
        }

        function onSuccess(response, options){
            this.mun(this.store, onError);
            btn.setDisabled(false);
            
            if (!this.supressSuccessAlert) {
                Ext4.Msg.alert("Success", "Your upload was successful!", function(){
                    window.location = btn.successURL || LABKEY.ActionURL.buildURL('query', 'executeQuery', null, {schemaName: this.store.schemaName, 'query.queryName': this.store.queryName})
                }, this);
            }
        }

        function onError(response, options){
            this.mun(this.store, onSuccess);
            btn.setDisabled(false);

            obj = Ext4.JSON.decode(response.responseText);
            for (var i = 0; i < obj.errors.length; i++)
            {
                var field = this.getForm().findField(obj.errors[i].field);
                if (field)
                    field.markInvalid([obj.errors[i].message]);
                else
                    console.log("Unable to find field for invalidation", obj.errors[i]);
            }
            Ext4.Msg.alert("Error", "There were problems submitting your data. Please check the form for errors.");
        }

        Ext4.Ajax.request({
            url: LABKEY.ActionURL.buildURL('trialShare', this.mode + this.objectName + ".api"),
            method: 'POST',
            jsonData: this.getFieldValues(),
            success: onSuccess,
            failure: onError,
            scope: this
        });
        
    },

    getFieldValues : function()
    {
        var fieldValues = {};
        var metadata = this.metadata;
        // getValues returns the empty string for fields that are empty, which is not what we want,
        // so we'll walk through the fields ourselves.
        this.getForm().getFields().each(function(item)
        {
            var value = item.value;
            if (value)
            {
                if (metadata[item.dataIndex] && metadata[item.dataIndex].stripNewLines)
                     value = value.replace(/\n/g, " ");
                fieldValues[item.name] = value;
            }
        });
        return fieldValues;
    }
});