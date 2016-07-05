package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

/**
 * Created by susanh on 2/5/16.
 */
public class StudyPropertiesQueryUpdatePage extends LabKeyPage
{
    public StudyPropertiesQueryUpdatePage(BaseWebDriverTest test)
    {
        super(test);
    }

    public void setStudyContainers()
    {
        log("Setting up study container links");
        String projectName = getCurrentProject();
        clickAndWait(Locator.linkWithText("StudyAccess"));
        DataRegionTable table = new DataRegionTable("query", _test);
        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            String name = table.getDataAsText(i, "StudyId");
            Boolean isPublic = "Public".equalsIgnoreCase(table.getDataAsText(i, "Visibility"));
            clickAndWait(table.updateLink(i));
            selectOptionByText(Locators.studyContainerSelect, "/" + projectName + "/DataFinderTest" + (isPublic ? "Public" : "Operational") + name);
            clickButton("Submit");
        }
    }

    public void setStudyContainer(String studyId, String containerName, Boolean navigateToList)
    {
        log("Setting up study container link for studyId " + studyId);
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("StudyAccess"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        int rowIndex = table.getRow("StudyId", studyId);
        clickAndWait(table.updateLink(rowIndex));
        selectOptionByText(Locators.studyContainerSelect, containerName);
        clickButton("Submit");
    }

    private void unlinkStudy(String studyShortName)
    {
        clickAndWait(Locator.linkWithText("StudyProperties"));
        DataRegionTable table = new DataRegionTable("query", _test);
        int row = table.getRow("Short Name", studyShortName);

        clickAndWait(table.updateLink(row));

        selectOptionByText(Locators.studyContainerSelect, "");

        clickButton("Submit");
    }

    private static class Locators
    {
        public static final Locator.XPathLocator studyContainerSelect = Locator.tagWithName("select", "quf_StudyContainer");
    }

}
