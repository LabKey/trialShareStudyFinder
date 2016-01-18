Ext4.define("LABKEY.study.panel.StudyPanelHeader", {

    extend: 'Ext.Container',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    showHelpLinks: true,

    searchMessage: "",

    dataModuleName: 'study',

    bubbleEvents: [
        "studySubsetChanged",
        "searchTermsChanged"
    ],


    initComponent: function() {

        this.studySubsets = Ext4.create('LABKEY.study.store.StudySubsets', {
            dataModuleName : this.dataModuleName,
            objectName: this.objectName
        });
        this.items = [];
        var searchBox = this.getSearchBox();
        if (searchBox)
            this.items.push(searchBox);
        if (this.getStudySubsetMenu())
            this.items.push(this.getStudySubsetMenu());
        if (this.showHelpLinks)
            this.items.push(this.getHelpLinks());

        this.studySubsets.on(
                'load', function(store) {
                    this.getStudySubsetMenu().setValue(store.defaultValue);
                    if (store.count() > 1)
                        this.getStudySubsetMenu().show();
                },
                this
        );

        this.callParent();
    },

    getSearchBox : function() {
        if (!this.searchBox && this.showSearch) {
            this.searchBox = Ext4.create('Ext.form.field.Text', {
                emptyText:'Studies',
                cls: 'labkey-search-box',
                fieldLabel: '<i class="fa fa-search"></i>',
                labelWidth: "10px",
                labelSeparator: '',
                disabled: !this.showSearch,
                hidden: !this.showSearch,
                fieldCls: 'labkey-search-box',
                id: 'searchTerms',
                listeners: {
                    scope: this,
                    'change': function(field,newValue,oldValue,eOpts) {
                        this.onSearchTermsChanged(newValue)
                    }
                }
            })
        }
        return this.searchBox;

    },

    getStudySubsetMenu: function() {
        if (!this.studySubsetMenu) {
            this.studySubsetMenu = Ext4.create('Ext.form.ComboBox', {
                store: this.studySubsets,
                name : 'studySubsetSelect',
                queryMode: 'local',
                valueField: 'id',
                displayField: 'name',
                hidden: true,
                value: this.studySubsets.defaultValue,
                cls: 'labkey-study-search',
                multiSelect: false,
                listeners: {
                    scope: this,
                    'select': function(field, newValue, oldValue, eOpts) {
                        this.onSubsetChanged(newValue[0])
                    },
                    'render': function(eOpts) {
                        if (this.studySubsets.defaultValue)
                            this.onSubsetChanged(this.studySubsets.defaultValue)
                    }
                }
            })
        }
        return this.studySubsetMenu;
    },

    getHelpLinks: function() {
        if (!this.helpLinks) {
            this.helpLinks = Ext4.create("Ext.button.Button", {
                text: 'quick help',
                cls: 'labkey-text-link labkey-finder-help',
                componentCls: 'labkey-finder-help',
                scope: this,
                handler: function() {
                    this.startTutorial();
                }
            });
        }
        return this.helpLinks;
    },

    onSearchTermsChanged: function(value) {
        console.log("Search terms changed to ", value);
        this.fireEvent("searchTermsChanged", value);
    },

    onSubsetChanged: function(value) {
        this.fireEvent("studySubsetChanged", value.data.id);
    },

    startTutorial: function() {
        LABKEY.help.Tour.show("LABKEY.tour.dataFinder");
        return false;
    }

});