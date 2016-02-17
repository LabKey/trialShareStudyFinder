/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.FinderObjectSelection", {
    extend: "Ext.Container",
    bubbleEvents: ["finderObjectChanged"],
    cls: 'labkey-finder-object-selection',
    initComponent: function ()
    {
        this.items = [];
        for (var i = 0; i < this.cubeConfigs.length; i++)
        {
            var name = this.cubeConfigs[i].objectName;
            this.items.push(Ext4.create("Ext.button.Button", {
                        text: this.cubeConfigs[i].objectNamePlural,
                        cls: 'labkey-text-link',
                        objectName: this.cubeConfigs[i].objectName,
                        scope: this,
                        handler : function (button)
                        {
                            this.fireEvent("finderObjectChanged", button.objectName);
                        }
                    })
            );
        }
        this.callParent();
    }
});