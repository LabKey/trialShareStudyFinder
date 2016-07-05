package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

/**
 * Created by susanh on 2/5/16.
 */
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
        DataRegionTable table = new DataRegionTable("query", _test);

        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            clickAndWait(table.updateLink(i));
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
        DataRegionTable table = new DataRegionTable("query", _test);

        int rowIndex = table.getRow("Title", title);
        clickAndWait(table.updateLink(rowIndex));
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
        DataRegionTable table = new DataRegionTable("query", _test);

        int rowIndex = table.getRow("Title", title);
        clickAndWait(table.updateLink(rowIndex));
        selectOptionByText(Locators.manuscriptContainerSelect, containerName);
        clickButton("Submit");
    }

    public int getPublicationCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        table.setFilter("Title", "Equals", title);
        return table.getDataRowCount();
    }

    public int getPublicationConditionCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("PublicationCondition"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        table.setFilter("PublicationId", "Equals", title);
        return table.getDataRowCount();
    }

    public int getPublicationTherapeuticAreaCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("PublicationTherapeuticArea"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        table.setFilter("PublicationId", "Equals", title);
        return table.getDataRowCount();
    }


    public int getPublicationStudyCount(String title, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("PublicationStudy"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
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
