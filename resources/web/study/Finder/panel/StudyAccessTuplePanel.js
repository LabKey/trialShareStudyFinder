Ext4.define('LABKEY.study.panel.StudyAccessTuplePanel', {
    extend: 'Ext.panel.Panel',
    studyAccessPanels : [],
    border: false,
    initComponent: function(){
        var me = this;
        this.studyAccessPanels = [];
        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            style: 'background-color: transparent;',
            items: [
                {
                    text: 'Add...',
                    //formBind: true,
                    handler: function (btn)
                    {
                        me.add(me.getStudyAccessPanel(me.getStudyAccessStore(-1)));
                        me.doLayout();
                    }
                }
            ]
        }];

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
        var _id = Ext4.id();
        var form = Ext4.create('LABKEY.study.panel.StudyAccessForm', {
            id: _id,
            store : studyAccessStore,
            fieldLabel      : 'Container',
            labelWidth      : this.defaultFieldLabelWidth,
            editable        : false,
            width           : this.largeFieldWidth
        });

        var buttonId = Ext4.id();
        var panelId = Ext4.id();
        var deleteButton = {
            id: buttonId,
            xtype:'label',
            cls: 'studyaccessdeletebutton',
            html:'<span class="fa fa-lg fa-times" style="color:red;"></span>',
            listeners: {
                click: {
                    element: 'el', //bind to the underlying el property on the panel
                    fn: function(a, button){
                        Ext4.getCmp(panelId).destroy();
                    }
                }
            }
        };


        var studyAccessPanel = new Ext4.FormPanel({
            id: panelId,
            cls: 'studyaccesspanel',
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            bodyStyle: 'padding: 4px 0;',
            hideLabel: true,
            //border: false,
            frame: false,
            items: [deleteButton, form],
            scope: this
        });
        return studyAccessPanel;
    }
});

Ext4.define('LABKEY.study.panel.StudyAccessForm', {
    extend: 'Ext.form.Panel',
    widget: 'study.studyaccessform',
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
                    xtype           : this.mode == "view" ? 'displayfield' : 'textfield',
                    cls             : this.fieldClsName,
                    labelCls        : this.fieldLabelClsName,
                    allowBlank      : false,
                    fieldLabel      : 'Visiblility',
                    name            : 'visiblility',
                    labelWidth      : this.defaultFieldLabelWidth,
                    width           : this.smallFieldWidth,
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
                    name            : 'studyContainer',
                    store           : {
                        model   : 'LABKEY.study.data.Container',
                        autoLoad: true
                    },
                    fieldLabel      : 'Study Container',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'EntityId',
                    displayField    : 'Path',
                    editable        : false,
                    width           : this.mediumLargeFieldWidth,
                    value           : this.store.data['studyContainerPath'],
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
