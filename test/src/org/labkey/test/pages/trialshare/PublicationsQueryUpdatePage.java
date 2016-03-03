package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

/**
 * Created by susanh on 2/5/16.
 */
public class PublicationsQueryUpdatePage extends LabKeyPage
{
    public PublicationsQueryUpdatePage(BaseWebDriverTest test)
    {
        super(test);
    }

    public void setPermissionsContainer(String publicStudyName, String operationalStudyName)
    {
        _test.log("Setting up permissions container");
        _test.goToProjectHome();
        String projectName = _test.getCurrentProject();
        clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        DataRegionTable table = new DataRegionTable("query", _test);
        int rowIndex = table.getRow("Status", "In Progress");

        clickAndWait(table.updateLink(rowIndex));
        selectOptionByText(Locators.permissionsContainerSelect,operationalStudyName );
        clickButton("Submit");
    }

    private static class Locators
    {
        public static final Locator.XPathLocator manuscriptContainerSelect = Locator.tagWithName("select", "quf_ManuscriptContainer");
        public static final Locator.XPathLocator permissionsContainerSelect = Locator.tagWithName("select", "quf_PermissionsContainer");
    }

}
