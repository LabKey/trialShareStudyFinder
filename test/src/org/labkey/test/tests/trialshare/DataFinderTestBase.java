/*
 * Copyright (c) 2016 LabKey Corporation
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

package org.labkey.test.tests.trialshare;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.ModulePropertyValue;
import org.labkey.test.TestFileUtils;
import org.labkey.test.TestTimeoutException;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.util.APIContainerHelper;
import org.labkey.test.util.AbstractContainerHelper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.PortalHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Category({Git.class})
public abstract class DataFinderTestBase extends BaseWebDriverTest
{
    static final String MODULE_NAME = "TrialShare";
    static final String WEB_PART_NAME = "TrialShare Data Finder";
    static final String OPERATIONAL_STUDY_NAME = "DataFinderTestOperationalStudy";
    static final String PUBLIC_STUDY_NAME = "DataFinderTestPublicStudy";
    static final String EMAIL_EXTENSION = "@datafinder.test";
    static final String PUBLIC_READER_DISPLAY_NAME = "public_reader";
    static final String PUBLIC_READER = PUBLIC_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    static final String CASALE_READER_DISPLAY_NAME = "casale_reader";
    static final String CASALE_READER = CASALE_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    static final String WISPR_READER_DISPLAY_NAME = "wispr_reader";
    static final String WISPR_READER = WISPR_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    static final String CONTROLLER = "trialshare";
    static final String ACTION = "dataFinder";
    static File dataListArchive = TestFileUtils.getSampleData("DataFinder.lists.zip");
    static File lookupListArchive = TestFileUtils.getSampleData("Lookups.lists.zip");

    public enum CubeObjectType {

        study("StudyId", "Manage Studies", new String[]{"Study Id", "Short Name", "Title"}),
        publication("Title", "Manage Publications", new String[]{"Key", "Title", "Status", "Publication Type"});


        private String _keyField;
        private String _manageDataTitle;
        private List<String> _manageDataHeaders;

        CubeObjectType(String keyField, String manageDataTitle, String[] manageDataHeaders)
        {
            _keyField = keyField;
            _manageDataHeaders = Arrays.asList(manageDataHeaders);
            _manageDataTitle = manageDataTitle;
        }

        public String getKeyField()
        {
            return _keyField;
        }

        public List<String> getManageDataHeaders()
        {
            return _manageDataHeaders;
        }

        public String getManageDataTitle()
        {
            return _manageDataTitle;
        }
    }

    static final Map<String, Set<String>> studySubsets = new HashMap<>();
    static {

        Set<String> operationalSet = new HashSet<>();
        studySubsets.put("Operational", operationalSet);
        operationalSet.add("TILT");
        operationalSet.add("WISP-R");
        operationalSet.add("ACCEPTOR");
        operationalSet.add("FACTOR");
        operationalSet.add("DIAMOND");

        Set<String> publicSet = new HashSet<>();
        studySubsets.put("Public", publicSet);
        publicSet.add("DIAMOND");
        publicSet.add("Shapiro");
        publicSet.add("Casale");
        publicSet.add("Vincenti");
    };

    protected static Set<String> loadedStudies = new HashSet<>();
    static {
        loadedStudies.add("DataFinderTestPublicCasale");
        loadedStudies.add("DataFinderTestOperationalWISP-R");
    }

    @Override
    protected void doCleanup(boolean afterTest) throws TestTimeoutException
    {
        _containerHelper.deleteProject(getProjectName(), afterTest);
    }

    @BeforeClass
    public static void initTest()
    {
        DataFinderTestBase init = (DataFinderTestBase)getCurrentTest();

        init.setUpProject();
    }


    @Override
    protected BrowserType bestBrowser()
    {
        return BrowserType.CHROME;
    }

    @Override
    public List<String> getAssociatedModules()
    {
        return Collections.singletonList("TrialShare");
    }


    protected void setUpProject()
    {
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);

        containerHelper.createProject(getProjectName(), "Custom");
        containerHelper.enableModule(MODULE_NAME);
        goToProjectHome();
        importLists();
        createStudies();
        createUsers();

        List<ModulePropertyValue> propList = new ArrayList<>();
        // set the site-default value for this so it will work as expected from the Admin Console.
        propList.add(new ModulePropertyValue("TrialShare", "/", "DataFinderCubeContainer", getProjectName()));
        setModuleProperties(propList);

        reindexForSearch();

        goToProjectHome();
        new PortalHelper(this).addWebPart(WEB_PART_NAME);
    }

    protected abstract void importLists();

    protected abstract void createStudies();

    protected abstract void createUsers();

    @LogMethod
    protected void reindexForSearch()
    {
        log("Reindexing data for full-text search");
        goToAdminConsole();
        clickAndWait(Locator.linkWithText("Data Cube"));
        clickButton("Reindex", 0);
        clickButton("OK");
    }

    void createStudy(String studyName, Boolean operational)
    {
        log("creating study " + studyName);
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);
        File studyArchive = operational ? TestFileUtils.getSampleData(OPERATIONAL_STUDY_NAME + ".folder.zip") : TestFileUtils.getSampleData(PUBLIC_STUDY_NAME + ".folder.zip");
        containerHelper.createSubfolder(getProjectName(), studyName, "Study");
        importStudyFromZip(studyArchive, true, true);
    }

    void createStudy(String name)
    {
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);

        File studyArchive = TestFileUtils.getSampleData(name + ".folder.zip");
        containerHelper.createSubfolder(getProjectName(), name, "Study");
        importStudyFromZip(studyArchive, true, true);
    }

    @Before
    public void preTest()
    {
        goToProjectHome();
        DataFinderPage finder = new DataFinderPage(this, true);
        finder.clearSearch();
        try
        {
            finder.clearAllFilters();
        }
        catch (NoSuchElementException ignore) {}
        finder.dismissTour();
    }

    DataFinderPage goDirectlyToDataFinderPage(String containerPath, boolean testingStudies)
    {
        log("Going directly to data finder page");
        DataFinderPage finder = new DataFinderPage(this, testingStudies);
        doAndWaitForPageSignal(() -> beginAt(WebTestHelper.buildURL(CONTROLLER, containerPath, ACTION)), finder.getCountSignal(), longWait());
        sleep(1000);  // HACK!
        if (!testingStudies)
        {
            finder.navigateToPublications();
        }
        return finder;
    }

    void goDirectlyToManageDataPage(String containerPath, CubeObjectType objectType)
    {
        log("Going directly to manage data page for " + objectType.toString());
        Map<String, String> params = new HashMap<>();
        params.put("objectName", objectType.toString());
        params.put("query.viewName", "manageData");
        beginAt(WebTestHelper.buildURL(CONTROLLER, containerPath, "manageData", params));
    }
}