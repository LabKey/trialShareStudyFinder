Ext4.define('LABKEY.study.data.Publication', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields : [
        {name: 'id', type:'int'},
        {name: 'studyId'},
        {name: 'pubMedId'}, // PubMed Id (for abstracts)
        {name: 'pmcId'},    // PubMed Central reference number (for full-text papers)
        {name: 'doi'},
        {name: 'author'},
        {name: 'authorAbbrev'},
        {name: 'issue'},
        {name: 'journal'},
        {name: 'pages'},
        {name: 'title'},
        {name: 'year'},
        {name: 'citation'},
        {name: 'status'},
        {name: 'url'},
        {name: 'dataUrl'},
        {name: 'isSelected', type: 'boolean', defaultValue: true}
    ]

});