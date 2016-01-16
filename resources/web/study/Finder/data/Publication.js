Ext4.define('LABKEY.study.data.Publication', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields : [
        {name: 'id', type:'int'},
        {name: 'studyId'},
        {name: 'pmid'}, // PubMed Id
        {name: 'pmcid'},    // PubMed Central reference number
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