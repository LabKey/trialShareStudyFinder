/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.CubeObjects', {
    extend: 'LABKEY.ext4.data.Store',
    //private
    setModel: function (model)
    {
        // NOTE: if the query lacks a PK, which can happen with queries that dont represent physical tables,
        // Ext adds a column to hold an Id.  In order to differentiate this from other fields we set defaults
        this.model.prototype.fields.each(function (field)
        {
            if (field.name == '_internalId')
            {
                Ext4.apply(field, {
                    hidden: true,
                    calculatedField: true,
                    shownInInsertView: false,
                    shownInUpdateView: false,
                    userEditable: false
                });
            }
            if (field.lookup && field.lookup.multiValued !== undefined)
            {
                Ext4.apply(field, {
                    editable: true,
                    facetingBehaviorType: "AUTOMATIC",
                    multiSelect : true
                })
            }
        });
        this.model = model;
        this.implicitModel = false;
    },
});