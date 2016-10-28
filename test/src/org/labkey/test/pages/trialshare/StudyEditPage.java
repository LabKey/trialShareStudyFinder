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
package org.labkey.test.pages.trialshare;

import org.labkey.test.Locator;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by susanh on 6/30/16.
 */
public class StudyEditPage extends CubeObjectEditPage
{
    public static final String SHORT_NAME = "shortName";
    public static final String STUDY_ID = "studyId";
    public static final String TITLE = "title";
    public static final String PARTICIPANT_COUNT = "participantCount";
    public static final String STUDY_TYPE ="studyType";
    public static final String ICON_URL = "iconUrl";
    public static final String EXTERNAL_URL = "externalURL";
    public static final String EXTERNAL_URL_DESCRIPTION = "externalUrlDescription";
    public static final String DESCRIPTION = "description";
    public static final String INVESTIGATOR = "investigator";
    public static final String AGE_GROUPS = "ageGroups";
    public static final String PHASES = "phases";
    public static final String CONDITIONS = "conditions";
    public static final String THERAPEUTIC_AREAS = "therapeuticAreas";
    public static final String VISIBILITY = "Visibility *:";
    public static final String STUDY_CONTAINER = "Study Container *:";
    public static final String DISPLAY_NAME = "Display Name:";

    private static final Map<String, String> DROPDOWN_FIELD_NAMES = new HashMap<>();
    static
    {
        DROPDOWN_FIELD_NAMES.put(STUDY_TYPE, "Study Type:");
    }

    private static final Map<String, String> MULTI_SELECT_FIELD_NAMES = new HashMap<>();
    static
    {
        MULTI_SELECT_FIELD_NAMES.put(AGE_GROUPS, "Age Groups:");
        MULTI_SELECT_FIELD_NAMES.put(PHASES, "Phases:");
        MULTI_SELECT_FIELD_NAMES.put(CONDITIONS, "Conditions:");
        MULTI_SELECT_FIELD_NAMES.put(THERAPEUTIC_AREAS, "Therapeutic Areas:");
    }

    private static final Set<String> FIELD_NAMES = new HashSet<>();
    static
    {
        FIELD_NAMES.add(SHORT_NAME);
        FIELD_NAMES.add(STUDY_ID);
        FIELD_NAMES.add(TITLE);
        FIELD_NAMES.add(PARTICIPANT_COUNT);
        FIELD_NAMES.add(STUDY_TYPE);
        FIELD_NAMES.add(ICON_URL);
        FIELD_NAMES.add(EXTERNAL_URL);
        FIELD_NAMES.add(EXTERNAL_URL_DESCRIPTION);
        FIELD_NAMES.add(DESCRIPTION);
        FIELD_NAMES.add(INVESTIGATOR);
        FIELD_NAMES.add(AGE_GROUPS);
        FIELD_NAMES.add(PHASES);
        FIELD_NAMES.add(CONDITIONS);
        FIELD_NAMES.add(THERAPEUTIC_AREAS);
    }

    public StudyEditPage(WebDriver driver)
    {
        super(driver);
    }

    @Override
    public Map<String, String> getDropdownFieldNames()
    {
        return DROPDOWN_FIELD_NAMES;
    }

    @Override
    public Map<String, String> getMultiSelectFieldNames()
    {
        return MULTI_SELECT_FIELD_NAMES;
    }

    @Override
    public Set<String> getFieldNames()
    {
        return FIELD_NAMES;
    }

    public Locator.XPathLocator getStudyAccessPanelLocator(int panelIndex)
    {
        Locator.XPathLocator panelLoc = new Locator.XPathLocator("");
        panelLoc = panelLoc.append(Locator.tagWithClass("div", "studyaccesspanelindex" + panelIndex));
        return panelLoc;
    }

    public Locator.XPathLocator getStudyAccessPanelFieldLocator(int panelIndex)
    {
        return getStudyAccessPanelLocator(panelIndex).append(Locator.tagWithClass("table", "x4-form-item"));
    }

    public Locator.XPathLocator getStudyAccessVisibility(int panelIndex)
    {
        return getStudyAccessPanelFieldLocator(panelIndex).withDescendant(Locator.tag("label").withText("Visibility *:"));
    }

    public Locator.XPathLocator getStudyAccessStudyContainer(int panelIndex)
    {
        return getStudyAccessPanelFieldLocator(panelIndex).withDescendant(Locator.tag("label").withText("Study Container *:"));
    }

    public Locator.XPathLocator getStudyAccessDisplayName(int panelIndex)
    {
        return getStudyAccessPanelLocator(panelIndex).append(Locator.tagWithName("input", "displayName"));
    }

    public Locator.XPathLocator getStudyAccessPanelRemoveBtn(int panelIndex)
    {
        return getStudyAccessPanelLocator(panelIndex).append(Locator.tagWithClass("span", "fa-times"));
    }

    public void setStudyAccessVisibility(int panelIndex, String value)
    {
        Locator.XPathLocator comboLocator = getStudyAccessVisibility(panelIndex);
        _ext4Helper.selectComboBoxItem(comboLocator, value);
    }

    public void setStudyAccessStudyContainer(int panelIndex, String value)
    {
        Locator.XPathLocator comboLocator = getStudyAccessStudyContainer(panelIndex);
        _ext4Helper.selectComboBoxItem(comboLocator, value);
    }

    public void setStudyAccessDisplayName(int panelIndex, String value)
    {
        Locator fieldLocator = getStudyAccessDisplayName(panelIndex);
        setFormElement(fieldLocator, value);
    }

    public String getStudyAccessDisplayNameValue(int panelIndex)
    {
        Locator fieldLocator = getStudyAccessDisplayName(panelIndex);
        return getFormElement(fieldLocator);
    }

    public void setStudyAccessFormValues(int panelIndex, String visibility, String studyContainer, String displayName)
    {
        // wait for combo to load
        sleep(1000);
        setStudyAccessVisibility(panelIndex, visibility);
        if (displayName != null)
            setStudyAccessDisplayName(panelIndex, displayName);
        // wait for combo to load
        sleep(1000);
        setStudyAccessStudyContainer(panelIndex, studyContainer);
    }

    public void removeStudyAccessPanel(int panelIndex)
    {
        click(getStudyAccessPanelRemoveBtn(panelIndex));
    }

    public void addStudyAccessPanel(int panelIndex)
    {
        click(Locator.linkWithText("Add..."));
        waitForElement(getStudyAccessPanelLocator(panelIndex));
        // wait for combo to load
        sleep(1000);
    }

    public void setStudyAccessFormValues(int i, Map<String, Object> newFields)
    {
        setStudyAccessFormValues(i, (String) newFields.get(VISIBILITY), (String) newFields.get(STUDY_CONTAINER), (String) newFields.get(DISPLAY_NAME));
    }

}
