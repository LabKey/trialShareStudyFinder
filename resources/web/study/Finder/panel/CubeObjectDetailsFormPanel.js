/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.panel.CubeObjectDetailsFormPanel', {
    extend: 'Ext.form.Panel',

    cls: 'labkey-data-finder-editor',

    bodyPadding: 5,

    itemId: 'cubedetailsformpanel',

    fieldClsName: 'labkey-field-editor',
    fieldLabelClsName : 'labkey-field-editor-label',

    smallFieldWidth:  350,
    mediumFieldWidth: 500,
    mediumLargeFieldWidth: 800,
    largeFieldWidth:  1000,

    defaultFieldLabelWidth: 200,
    mode: "view",
    multiSelectDelimiter: '; ',

    cubeObject : null,

    stripNewLinesFields: [],
    

    initComponent : function()
    {

        this.manageDataUrl = LABKEY.ActionURL.buildURL('trialShare', 'manageData.view', null, {objectName : this.objectName, 'query.viewName': 'manageData'});

        if (this.mode != "view")
        {
            this.dockedItems = [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                style: 'background-color: transparent;',
                items: [
                    {
                        text: 'Submit',
                        itemId: 'detailsSubmitBtn',
                        formBind: true,
                        successURL: this.nextStepUrl || LABKEY.ActionURL.getParameter('returnUrl') ||  this.manageDataUrl,
                        handler: function (btn)
                        {
                            var panel = btn.up('form');
                            panel.doSubmit(btn);
                        }
                    },
                    {
                        text: 'Cancel',
                        returnUrl: this.manageDataUrl || LABKEY.ActionURL.getParameter('returnUrl'),
                        handler: function (btn, key)
                        {
                            window.location = btn.returnUrl;
                        }
                    }
                ]
            }];
        }

        this.callParent();
        this.add({
            tag: 'div',
            itemId: 'messageEl',
            html:'Items marked with * are required',
            border: false,
            cls: 'labkey-data-finder-editor-message'
        });
        this.add(this.getFormFields());
        if (this.cubeObject)
        {
            this.bindFormFields();
        }
    },


    // override this to set up the form for the cube object
    getFormFields: function()
    {
        return [];
    },

    bindFormFields : function()
    {
        if (this.cubeObject)
        {
            for (var fieldName in this.cubeObject)
            {
                if (this.cubeObject.hasOwnProperty(fieldName))
                {
                    var field = this.getForm().findField(fieldName);
                    if (field)
                        field.setValue(this.cubeObject[fieldName]);
                }
            }
        }
    },

    doSubmit: function(btn){
        btn.setDisabled(true);

        function onSuccess(response, options){
            btn.setDisabled(false);

            var msg = "Your " + this.objectName.toLowerCase() + " was saved.";
            if (this.mode == "insert" && this.objectName == "Study")
            {
                msg += " Enter study access data to make the study visible in the finder.";
            }
            Ext4.Msg.alert("Success", msg, function(){
                window.location = btn.successURL;
            }, this);
        }

        function onError(response, options){
            btn.setDisabled(false);

            obj = Ext4.JSON.decode(response.responseText);
            if (obj.errors[0].field == "form")
            {
                Ext4.Msg.alert("Error", "There were problems submitting your data. " + obj.errors[0].message);
            }
            else
            {
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
        // so we'll walk through the fields ourselves.  We have to strip out newlines sometimes anyway.
        this.getForm().getFields().each(function(item)
        {
            var value = item.value;
            if (value && !item.isStudyAccess)
            {
                if (this.stripNewLinesFields.indexOf(item.name) >= 0)
                     value = value.replace(/\n/g, " ");
                fieldValues[item.name] = value;
            }
        }, this);
        return fieldValues;
    }
});