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

    initComponent : function() {

        this.returnUrl = LABKEY.ActionURL.buildURL('trialShare', 'manageData.view', null, {objectName : this.objectName, 'query.viewName': 'manageData'});

        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            style: 'background-color: transparent;',
            items: [
                LABKEY.ext4.FORMBUTTONS.getButton('SUBMIT'),
                LABKEY.ext4.FORMBUTTONS.getButton('CANCEL', {returnURL : this.returnUrl})
            ]
        }];
        this.callParent();
    },

    shouldShowInInsertView: function(metadata) {
        return this.isJoinTableField(metadata.name) || LABKEY.ext4.Util.shouldShowInUpdateView(metadata);
    },

    shouldShowInUpdateView: function(metadata) {
        return this.shouldShowInInsertView(metadata);
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
            }
        }
    },

    isJoinTableField : function(fieldName)
    {
        return this.joinTableFields.indexOf(fieldName) >= 0;
    },

    uncapitalize : function(name)
    {
        return name && name[0].toLowerCase() + name.slice(1)
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

            if (this.shouldShowInUpdateView(field)){

                var fieldEditor = LABKEY.ext4.Util.getFormEditorConfig(field, config);
                if (fieldEditor.isRequired)
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
                    fieldEditor.delimiter = '; '
                }

                if (field.isAutoIncrement){
                    fieldEditor.xtype = 'displayfield';
                }

                fieldEditor.name = this.uncapitalize(fieldEditor.name); // this is necessary to match the bean properties, somehow...
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
                this.getForm().findField(obj.errors[i].field).markInvalid([obj.errors[i].message]);
            }
        }

        Ext4.Ajax.request({
            url: LABKEY.ActionURL.buildURL('trialShare', 'insert' + this.objectName + ".api"),
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
        // getValues returns the empty string for fields that are empty, which is not what we want.
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
        //
        // // We have to convert these to something acceptable as an arrayList
        // var values = this.getValues();
        // for (var i = 0; i < this.joinTableFields.length; i++)
        // {
        //     var value = values[this.uncapitalize(this.joinTableFields[i])];
        //     if (!Ext4.isArray(value))
        //         values[this.uncapitalize(this.joinTableFields[i])] = null;
        // }
        //
        // return values;

    }
});