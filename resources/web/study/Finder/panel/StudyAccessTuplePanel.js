Ext4.define('LABKEY.study.panel.StudyAccessTuplePanel', {
    extend: 'Ext.panel.Panel',
    studyAccessPanels : [],
    border: false,
    studyAccessPanelIndex: 0,
    initComponent: function(){
        var me = this;
        this.studyAccessPanels = [];
        if (this.mode != "view") {
            this.dockedItems = [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
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
        var panelId = Ext4.id();
        if (this.mode != "view") {
            var deleteButton = {
                xtype: 'label',
                cls: 'studyaccessdeletebutton',
                html: '<span class="fa fa-lg fa-times"></span>',
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

        var form = Ext4.create('LABKEY.study.panel.StudyAccessForm', {
            mode: this.mode,
            store : studyAccessStore
        });
        formItems.push(form);

        var studyAccessPanel = new Ext4.Panel({
            id: panelId,
            cls: 'studyaccesspanel studyaccesspanelindex' + this.studyAccessPanelIndex++, //add index cls for automated test
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            items: [deleteButton, form],
            scope: this
        });
        return studyAccessPanel;
    }
});

