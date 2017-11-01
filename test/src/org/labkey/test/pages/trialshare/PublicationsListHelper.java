/*
 * Copyright (c) 2016-2017 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

public class PublicationsListHelper extends LabKeyPage
{
    public PublicationsListHelper(BaseWebDriverTest test)
    {
        super(test);
    }

    public void setPermissionsContainers(String publicStudyName, String operationalStudyName)
    {
        log("Setting up permissions container for publications");
        clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        DataRegionTable table = new DataRegionTable("query", getDriver());

        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            table.clickEditRow(i);
            String status = Locators.statusValue.findElement(getDriver()).getAttribute("value");
            if ("Complete".equalsIgnoreCase(status))
                selectOptionByText(Locators.permissionsContainerSelect,publicStudyName );
            else
                selectOptionByText(Locators.permissionsContainerSelect,operationalStudyName );
            clickButton("Submit");
        }
    }

    public void setPermissionsContainer(String title, String containerName, Boolean navigateToList)
    {
        log("Setting up permission container for publication with title: '" + title + "'");
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        }
        DataRegionTable table = new DataRegionTable("query", getDriver());

        int rowIndex = table.getRowIndex("Title", title);
        table.clickEditRow(rowIndex);
        selectOptionByText(Locators.permissionsContainerSelect, containerName);
        clickButton("Submit");
    }

    public void setManuscriptContainer(String title, String containerName, Boolean navigateToList)
    {
        log("Setting up manuscript container for publication with title: '" + title + "'");
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        }
        DataRegionTable table = new DataRegionTable("query", getDriver());

        int rowIndex = table.getRowIndex("Title", title);
        table.clickEditRow(rowIndex);
        selectOptionByText(Locators.manuscriptContainerSelect, containerName);
        clickButton("Submit");
    }

    public int getPublicationCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        }
        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.setFilter("Title", "Equals", title);
        return table.getDataRowCount();
    }

    public int getPublicationTherapeuticAreaCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("PublicationTherapeuticArea"));
        }
        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.setFilter("PublicationId", "Equals", title);
        return table.getDataRowCount();
    }


    public int getPublicationStudyCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("PublicationStudy"));
        }
        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.setFilter("PublicationId", "Equals", title);
        return table.getDataRowCount();
    }

    private static class Locators
    {
        static final Locator.XPathLocator manuscriptContainerSelect = Locator.tagWithName("select", "quf_ManuscriptContainer");
        static final Locator.XPathLocator statusValue = Locator.input("quf_Status");
        static final Locator.XPathLocator permissionsContainerSelect = Locator.tagWithName("select", "quf_PermissionsContainer");
    }

}
