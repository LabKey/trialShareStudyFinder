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
        log("Setting up permissions container for publications");
        clickAndWait(Locator.linkWithText("ManuscriptsAndAbstracts"));
        DataRegionTable table = new DataRegionTable("query", _test);

        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            clickAndWait(table.updateLink(i));
            String status = Locators.statusValue.findElement(_test.getDriver()).getAttribute("value");
            if ("Complete".equalsIgnoreCase(status))
                selectOptionByText(Locators.permissionsContainerSelect,publicStudyName );
            else
                selectOptionByText(Locators.permissionsContainerSelect,operationalStudyName );
            clickButton("Submit");
        }
    }

    private static class Locators
    {
        public static final Locator.XPathLocator manuscriptContainerSelect = Locator.tagWithName("select", "quf_ManuscriptContainer");
        public static final Locator.XPathLocator statusValue = Locator.input("quf_Status");
        public static final Locator.XPathLocator permissionsContainerSelect = Locator.tagWithName("select", "quf_PermissionsContainer");
    }

}
