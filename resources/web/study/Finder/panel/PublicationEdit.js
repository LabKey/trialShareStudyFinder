/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.PublicationEdit', {
    extend: 'LABKEY.ext4.FormPanel',
    // extend: 'Ext.form.Panel',

    alias: 'widget.labkey-data-finder-publication-editor',

    itemId : 'labkey-data-finder-publication-editor',

    cls: 'labkey-data-finder-publication-editor',

    defaultFieldWidth: 800,
    defaultFieldLabelWidth: 200,
    joinTableFields : ["StudiesList"],

    shouldShowInInsertView: function(metadata) {
        return this.isJoinTableField || this.callParent();
    },

    shouldShowInUpdateView: function(metadata) {
        return this.shouldShowInInsertView(metadata);
    },

    configureJoinFields : function(store) {
        var fields = LABKEY.ext4.Util.getStoreFields(this.store);
        for (var i = 0; i < this.joinTableFields.length; i++)
        {
            var field = fields.get(this.joinTableFields[i]);
            field.editable = true;
            field.facetingBehaviorType = "AUTOMATIC";
            field.multiSelect = true;
            field.isJoinField = true;
        }
    },

    isJoinTableField : function(fieldName)
    {
        return this.joinTableFields.indexOf(fieldName) >= 0;
    },

    configureForm: function(store){
        this.configureJoinFields(store);
        var toAdd = [];
        var compositeFields = {};
        var fields = LABKEY.ext4.Util.getStoreFields(this.store);
        
        LABKEY.ext4.Util.getStoreFields(store).each(function(c){
            var config = {
                queryName: store.queryName,
                schemaName: store.schemaName
            };

            if (this.metadataDefaults) {
                Ext4.Object.merge(config, this.metadataDefaults);
            }
            if (this.metadata && this.metadata[c.name]) {
                Ext4.Object.merge(config, this.metadata[c.name]);
            }

            if (this.shouldShowInUpdateView(c)){
                var theField = fields.get(c.name);
                var fieldEditor = LABKEY.ext4.Util.getFormEditorConfig(fields.get(c.name), config);

                if (!fieldEditor.width)
                    fieldEditor.width = this.defaultFieldWidth;
                if (!fieldEditor.labelWidth)
                    fieldEditor.labelWidth = this.defaultFieldLabelWidth;

                if (c.inputType == 'textarea' && fieldEditor.xtype == 'textarea' && !c.height){
                    Ext4.apply(fieldEditor, {width: this.defaultFieldWidth, height: 100});

                }

                if (fieldEditor.xtype == 'combo' || fieldEditor.xtype == 'labkey-combo'){
                    fieldEditor.store.containerFilter = fieldEditor.containerFilter;
                    fieldEditor.multiSelect = theField.multiSelect;
                    fieldEditor.store.autoLoad = true;
                }

                if (c.isAutoIncrement){
                    fieldEditor.xtype = 'displayfield';
                }

                if (!c.compositeField)
                    toAdd.push(fieldEditor);
                else {
                    fieldEditor.fieldLabel = undefined;
                    if(!compositeFields[c.compositeField]){
                        compositeFields[c.compositeField] = {
                            xtype: 'panel',
                            autoHeight: true,
                            layout: 'hbox',
                            border: false,
                            fieldLabel: c.compositeField,
                            defaults: {
                                border: false,
                                margins: '0px 4px 0px 0px '
                            },
                            width: this.defaultFieldWidth,
                            items: [fieldEditor]
                        };
                        toAdd.push(compositeFields[c.compositeField]);

                        if(compositeFields[c.compositeField].msgTarget == 'below'){
                            //create a div to hold error messages
                            compositeFields[c.compositeField].msgTargetId = Ext4.id();
                            toAdd.push({
                                tag: 'div',
                                fieldLabel: null,
                                border: false,
                                id: compositeFields[c.compositeField].msgTargetId
                            });
                        }
                        else {
                            fieldEditor.msgTarget = 'qtip';
                        }
                    }
                    else {
                        compositeFields[c.compositeField].items.push(fieldEditor);
                    }
                }
            }
        }, this);

        //distribute width for compositeFields
        for (var i in compositeFields){
            var compositeField = compositeFields[i];
            var toResize = [];
            //this leaves a 2px buffer between each field
            var availableWidth = this.defaultFieldWidth - 4*(compositeFields[i].items.length-1);
            for (var j=0;j<compositeFields[i].items.length;j++){
                var field = compositeFields[i].items[j];
                // if the field isn't using the default width, we assume it was deliberately customized
                if(field.width && field.width!=this.defaultFieldWidth){
                    availableWidth = availableWidth - field.width;
                }
                else {
                    toResize.push(field)
                }
            }

            if(toResize.length){
                var newWidth = availableWidth/toResize.length;
                for (j=0;j<toResize.length;j++){
                    toResize[j].width = newWidth;
                }
            }
        }

        return toAdd;
    },


    /** Override **/
    doSubmit: function(btn){
        btn.setDisabled(true);

        // force record to refresh based on most recent form values.  this happens reliably in modern browsers,
        // but IE8 sometimes wont apply changes when the cursor is still on a field
        var plugin = this.getPlugin('labkey-databind');
        plugin.updateRecordFromForm();

        if(!this.store.getNewRecords().length && !this.store.getUpdatedRecords().length && !this.store.getRemovedRecords().length){
            Ext4.Msg.alert('No changes', 'There are no changes. Nothing to do.');
            window.location = btn.successURL || LABKEY.ActionURL.buildURL('query', 'executeQuery', null, {schemaName: this.store.schemaName, 'query.queryName': this.store.queryName})
            return;
        }

        function onSuccess(store){
            this.mun(this.store, onError);
            btn.setDisabled(false);

            if (!this.supressSuccessAlert) {
                Ext4.Msg.alert("Success", "Your upload was successful!", function(){
                    window.location = btn.successURL || LABKEY.ActionURL.buildURL('query', 'executeQuery', null, {schemaName: this.store.schemaName, 'query.queryName': this.store.queryName})
                }, this);
            }
        }

        function onError(store, msg, error){
            this.mun(this.store, onSuccess);
            btn.setDisabled(false);
        }

        this.mon(this.store, 'write', onSuccess, this, {single: true});
        this.mon(this.store, 'exception', onError, this, {single: true});

        this.store.sync();
    }
});