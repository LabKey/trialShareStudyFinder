package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

import java.util.Set;

/**
 * Created by susanh on 2/5/16.
 */
public class StudyPropertiesQueryUpdatePage extends LabKeyPage
{
    public StudyPropertiesQueryUpdatePage(BaseWebDriverTest test)
    {
        super(test);
    }

    public void setStudyContainers(Set<String> loadedStudies, String publicStudyName, String operationalStudyName)
    {
        _test.log("Setting up study container links");
        _test.goToProjectHome();
        clickAndWait(Locator.linkWithText("StudyContainer"));
        DataRegionTable table = new DataRegionTable("query", _test);
        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            String name = table.getDataAsText(i, "Short Name");
            Boolean isPublic = Boolean.valueOf(table.getDataAsText(i, "Is Public"));
            clickAndWait(table.updateLink(i));
            if (loadedStudies.contains("DataFinderTestOperational" + name))
                selectOptionByText(Locators.studyContainerSelect, "DataFinderTestOperational" + name);
            else if (loadedStudies.contains("DataFinderTestPublic" + name))
                selectOptionByText(Locators.studyContainerSelect, "DataFinderTestPublic" + name);
            else if (isPublic)
                selectOptionByText(Locators.studyContainerSelect, publicStudyName);
            else
                selectOptionByText(Locators.studyContainerSelect, operationalStudyName);
            clickButton("Submit");
        }
    }

    private void unlinkStudy(String studyShortName)
    {
        _test.goToProjectHome();
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
