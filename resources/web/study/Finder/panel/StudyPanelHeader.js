Ext4.define("LABKEY.study.panel.StudyPanelHeader", {

    extend: 'Ext.Container',

    layout: { type: 'hbox', align: 'stretch'},

    showHelpLinks: true,

    searchMessage: "",

    dataModuleName: 'study',

    bubbleEvents: [
        "studySubsetChanged",
        "searchTermsChanged"
    ],

    studySubsets : Ext4.create('LABKEY.study.store.StudySubsets'),

    initComponent: function() {
        this.items = [];
        this.items.push(this.getSearchBox());
        if (this.getStudySubsetMenu())
            this.items.push(this.getStudySubsetMenu());
        if (this.showHelpLinks)
            this.items.push(this.getHelpLinks());

        this.callParent();
    },

    getSearchBox : function() {
        if (!this.searchBox) {
            this.searchBox = Ext4.create('Ext.form.field.Text', {
                emptyText:'Studies',
                cls: 'labkey-search-box',
                fieldLabel: '<i class="fa fa-search"></i>',
                labelWidth: "10px",
                labelSeparator: '',
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
                value: this.studySubsets.defaultValue,
                cls: 'labkey-study-search',
                multiSelect: false,
                listeners: {
                    scope: this,
                    'select': function(field, newValue, oldValue, eOpts) {
                        this.onStudySubsetChanged(newValue[0])
                    },
                    'render': function(eOpts) {
                        if (this.studySubsets.defaultValue)
                            this.onStudySubsetChanged(this.studySubsets.defaultValue)
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
                cls: 'labkey-text-link',
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

    onStudySubsetChanged: function(value) {
        this.fireEvent("studySubsetChanged", value.data.id);
    },

    startTutorial: function() {
        LABKEY.help.Tour.show("LABKEY.tour.dataFinder");
        return false;
    }

});