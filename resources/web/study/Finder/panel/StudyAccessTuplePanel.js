Ext4.define('LABKEY.study.panel.StudyAccessTuplePanel', {
    extend: 'Ext.panel.Panel',
    studyAccessPanels : [],
    border: false,
    initComponent: function(){
        var me = this;
        this.studyAccessPanels = [];
        if (this.mode != "view") {
            this.dockedItems = [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                style: 'background-color: transparent;',
                items: [
                    {
                        text: 'Add...',
                        handler: function (btn)
                        {
                            me.add(me.getStudyAccessPanel(me.getStudyAccessStore(-1)));
                            me.doLayout();
                        }
                    }
                ]
            }];
        }

        this.callParent();

        if (this.studyaccesslist && this.studyaccesslist.length > 0) {
            for (var i = 0; i < this.studyaccesslist.length; i++) {
                this.studyAccessPanels.push(this.getStudyAccessPanel(me.getStudyAccessStore(i)));
            }
        }
        else {
            this.studyAccessPanels.push(this.getStudyAccessPanel(me.getStudyAccessStore(-1)));
        }

        Ext4.each(this.studyAccessPanels, function(panel){
            this.add(panel);
        }, this);
        this.doLayout();
    },
    getStudyAccessStore: function(i) {
        return {
            model   : 'LABKEY.study.data.StudyAccess',
            data    : i >= 0 ? this.studyaccesslist[i] : {},
            autoLoad: true,
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        }
    },
    getStudyAccessPanel : function(studyAccessStore) {
        var formItems = [], me = this;
        var buttonId = Ext4.id(), panelId = Ext4.id();
        if (this.mode != "view") {
            var deleteButton = {
                id: buttonId,
                xtype: 'label',
                cls: 'studyaccessdeletebutton',
                html: '<span class="fa fa-lg fa-times" style="color:red;"></span>',
                listeners: {
                    click: {
                        element: 'el', //bind to the underlying el property on the panel
                        fn: function (a, button)
                        {
                            Ext4.getCmp(panelId).destroy();
                            if (Ext4.ComponentQuery.query("#cubedetailsformpanel")[0].getForm().isValid()) {
                                var submitBtn = Ext4.ComponentQuery.query("#detailsSubmitBtn")[0];
                                if (submitBtn && submitBtn.isDisabled) {
                                    submitBtn.enable();
                                }
                            }
                        }
                    }
                }
            };
            formItems.push(deleteButton);
        }

        var _id = Ext4.id();
        var form = Ext4.create('LABKEY.study.panel.StudyAccessForm', {
            id: _id,
            mode: this.mode,
            store : studyAccessStore
        });
        formItems.push(form);

        var studyAccessPanel = new Ext4.Panel({
            id: panelId,
            cls: 'studyaccesspanel',
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            bodyStyle: 'padding: 4px 0;',
            items: [deleteButton, form],
            scope: this
        });
        return studyAccessPanel;
    }
});

Ext4.define('LABKEY.study.panel.StudyAccessForm', {
    extend: 'Ext.form.Panel',
    itemId: 'studyaccessform',
    defaultFieldLabelWidth: 120,
    smallFieldWidth: 350,
    border: false,
    cls: 'studyaccessform',
    mediumLargeFieldWidth: 750,
    initComponent: function(){
        this.callParent();
        this.add(this.getFormFields());
    },getFormFields: function()
    {
        var items = [];
        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : false,
                    name            : 'visibility',
                    store           : Ext4.create('Ext.data.Store', {
                                            fields: ['value'],
                                            data : [
                                                {"value":"Operational"},
                                                {"value":"Public"}
                                            ]
                    }),
                    fieldLabel      : 'Visibility *',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'value',
                    displayField    : 'value',
                    editable        : false,
                    width           : this.value,
                    value           : this.store.data['visibility'],
                    isStudyAccess   : true
                });
        items.push(
                {
                    xtype           : 'combo',
                    disabled        : this.mode == "view",
                    disabledCls     : 'labkey-combo-disabled',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : false,
                    name            : 'studyContainer',
                    store           : {
                        model   : 'LABKEY.study.data.Container',
                        autoLoad: true
                    },
                    fieldLabel      : 'Study Container *',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'EntityId',
                    displayField    : 'Path',
                    editable        : false,
                    width           : this.mediumLargeFieldWidth,
                    value           : this.store.data['studyContainer'],
                    isStudyAccess   : true
                }
        );
        items.push(
                {
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : true,
                    fieldLabel      : 'Display Name',
                    name            : 'displayName',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
                    value           : this.store.data['displayName'],
                    isStudyAccess   : true
                });
        return items;
    }
});
