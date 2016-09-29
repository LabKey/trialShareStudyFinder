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
    },
    getFormFields: function()
    {
        var me = this;
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
                    store           : {
                        model       : 'LABKEY.study.data.Visibility',
                        autoLoad    : true,
                        proxy       : LABKEY.study.util.CubeObjectHelper.getModelProxy('visibility', me.cubeContainerPath)
                    },
                    fieldLabel      : 'Visibility *',
                    labelWidth      : this.defaultFieldLabelWidth,
                    valueField      : 'Visibility',
                    displayField    : 'Visibility',
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
