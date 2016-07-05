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

import com.google.common.base.Predicate;
import org.junit.Assert;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.components.Component;
import org.labkey.test.components.trialshare.PublicationPanel;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFinderPage extends LabKeyPage
{
    public static final String STUDY_COUNT_SIGNAL = "dataFinderStudyCountsUpdated";
    public static final String PUBLICATION_COUNT_SIGNAL = "dataFinderPublicationCountsUpdated";
    public static final String STUDY_FACET_SIGNAL = "dataFinderStudyFacetsReady";
    public static final String PUBLICATION_FACET_SIGNAL = "dataFinderPublicationFacetReady";
    private static final String GROUP_UPDATED_SIGNAL = "participantGroupUpdated";
    private static final String PUBLICATION_DETAILS_SIGNAL = "publicationDetailsLoaded";
    private boolean testingStudies = true;
    private Locator.CssLocator finderLocator = null;

    public DataFinderPage(BaseWebDriverTest test, boolean testingStudies)
    {
        super(test);
        this.testingStudies = testingStudies;
        if (testingStudies)
        {
            finderLocator = Locators.studyFinder;
        }
        else
        {
            finderLocator = Locators.pubFinder;
        }
    }

    public boolean canManageData()
    {
        return isElementPresent(Locators.manageData);
    }

    public void goToManageData()
    {
        Locators.manageData.findElement(this.getDriver()).click();
    }

    public String getCountSignal()
    {
        return (this.testingStudies ? STUDY_COUNT_SIGNAL : PUBLICATION_COUNT_SIGNAL);
    }

    public String getFacetReadySignal()
    {
        return (this.testingStudies ? STUDY_FACET_SIGNAL : PUBLICATION_FACET_SIGNAL);
    }

    @Override
    protected void waitForPage()
    {
        waitForElement(LabKeyPage.Locators.pageSignal(getCountSignal()));
    }

    protected void waitForGroupUpdate()
    {
        waitForElement(LabKeyPage.Locators.pageSignal(GROUP_UPDATED_SIGNAL));
    }


    public boolean hasStudySubsetCombo()
    {
        return isElementPresent(Locators.studySubsetCombo);
    }

    public void selectStudySubset(String text)
    {
        // FIXME isn't there a general method for asking "is this combo list item already selected"?
        _ext4Helper.openComboList(DataFinderPage.Locators.studySubsetCombo);
        if (!isElementPresent(Ext4Helper.Locators.comboListItemSelected().withText(text)))
        {
            doAndWaitForPageSignal(() ->_ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT), getCountSignal());

        }
        else // FIXME you should be able to just close the combo box at this point, but the close method assumes you've chosen something from teh lis
        {
            _ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT);
        }
    }

    @LogMethod
    public void search(@LoggedParam final String search)
    {
        doAndWaitForPageSignal(() -> setFormElement(Locators.getSearchInput(finderLocator), search), getCountSignal());
    }

    @LogMethod(quiet = true)
    public void clearSearch()
    {
        if (isElementPresent(Locators.getSearchInput(finderLocator)) && !getFormElement(Locators.getSearchInput(finderLocator)).isEmpty())
            search("");
    }

    public void saveGroup(String name)
    {
        setFormElement(DataFinderPage.Locators.groupLabelInput, name);
        clickButtonContainingText("Save", BaseWebDriverTest.WAIT_FOR_EXT_MASK_TO_DISSAPEAR);
        waitForGroupUpdate();
    }

    public String getGroupLabel()
    {
        return DataFinderPage.Locators.groupLabel.findElement(getDriver()).getText().trim();
    }

    public GroupMenu getMenu(Locator locator)
    {
        return new GroupMenu(locator.findElement(getDriver()));
    }

    public boolean menuIsDisabled(Locator.CssLocator locator)
    {
        return isElementPresent(locator.append(" .labkey-disabled-text-link"));
    }

    public void openMenu(Locator locator)
    {
        locator.findElement(getDriver()).click();
    }

    public void navigateToStudies()
    {
        selectDataFinderObject("Studies");
        assertElementVisible(DataFinderPage.Locators.studyFinder);
    }

    public void navigateToPublications()
    {
        selectDataFinderObject("Publications");
        assertElementVisible(DataFinderPage.Locators.pubFinder);
    }

    public void selectDataFinderObject(String text)
    {
        Locators.finderObjectTab(text).findElement(getDriver()).click();
    }

    public Map<Dimension, Integer> getSummaryCounts()
    {
        WebElement summaryElement;
        if (testingStudies)
            summaryElement = DataFinderPage.Locators.studySummaryArea.findElement(getDriver());
        else
            summaryElement = DataFinderPage.Locators.pubSummaryArea.findElement(getDriver());

        SummaryPanel summary = new SummaryPanel(summaryElement);

        Map<Dimension, Integer> countMap = new HashMap<>();
        for (String value : summary.getValues())
        {
            String[] parts = value.split("\n");
            Dimension dimension = Dimension.fromString(parts[0]);
            Integer count = Integer.parseInt(parts[1].trim().replace(",",""));
            countMap.put(dimension, count);
        }
        return countMap;
    }

    public List<DataCard> getDataCards()
    {
        List<WebElement> cardEls;
        List<DataCard> cards = new ArrayList<>();

        Locator locator = testingStudies? Locators.studyCard : Locators.pubCard;

        if (isElementPresent(locator))
        {
            scrollIntoView(locator);
            cardEls = locator.findElements(getDriver());
            for (WebElement el : cardEls)
            {
                cards.add(new DataCard(el));
            }
        }

        return cards;
    }

    public FacetGrid getFacetsGrid()
    {
        if(testingStudies)
            return new FacetGrid(DataFinderPage.Locators.studyFacetPanel.findElement(getDriver()));
        else
            return new FacetGrid(DataFinderPage.Locators.pubFacetPanel.findElement(getDriver()));
    }

    public boolean clearAllActive()
    {
        Locator.CssLocator clearAllLocator = DataFinderPage.Locators.getClearAll(finderLocator);
        scrollIntoView(clearAllLocator);
        Locator activeClearAllLocator = DataFinderPage.Locators.getActiveClearAll(clearAllLocator);
        return isElementPresent(activeClearAllLocator);
    }

    public void clearAllFilters()
    {
        Locator.CssLocator clearAllLocator = DataFinderPage.Locators.getClearAll(finderLocator);
        scrollIntoView(clearAllLocator);
        Locator activeClearAllLocator = DataFinderPage.Locators.getActiveClearAll(clearAllLocator);
        if (isElementPresent(activeClearAllLocator))
        {
            final WebElement clearAll = activeClearAllLocator.findElement(getDriver());
            if (clearAll.isDisplayed())
            {
                doAndWaitForPageSignal(clearAll::click, getCountSignal());
            }
        }
    }

    public void dismissTour()
    {
        shortWait().until(new Predicate<WebDriver>()
        {
            @Override
            public boolean apply(WebDriver webDriver)
            {
                try
                {
                    return (Boolean) executeScript("" +
                            "if (window.hopscotch)" +
                            "  return !hopscotch.endTour().isActive;" +
                            "else" +
                            "  return true;");
                }
                catch (WebDriverException recheck)
                {
                    return false;
                }
            }

            @Override
            public String toString()
            {
                return "tour to be dismissed.";
            }
        });
    }
    
    public static class Locators
    {

        public static final Locator.CssLocator cardDeck = Locator.css(".labkey-data-finder-card-deck-view");
        public static final Locator.CssLocator studyFinder = Locator.css(".labkey-study-finder-card");

        public static final Locator.XPathLocator finderObjectCombo = Ext4Helper.Locators.formItemWithInputNamed("configSelect");
        public static final Locator.XPathLocator studySubsetCombo = Ext4Helper.Locators.formItemWithInputNamed("subsetSelect");
        public static final Locator.CssLocator studyCard = studyFinder.append(Locator.css(".labkey-study-card"));
        public static final Locator.CssLocator studySelectionPanel = studyFinder.append(Locator.css(".labkey-facet-selection-panel"));
        public static final Locator.CssLocator studyFacetPanel = studySelectionPanel.append(Locator.css(" .labkey-study-facets"));
        public static final Locator.CssLocator studySummaryArea = studySelectionPanel.append(Locator.css("#summaryArea"));
        public static final Locator.CssLocator pubFinder = Locator.css(".labkey-publication-finder-card");
        public static final Locator.CssLocator pubSelectionPanel = pubFinder.append(Locator.css(".labkey-facet-selection-panel"));
        public static final Locator.CssLocator pubFacetPanel = pubSelectionPanel.append(Locator.css(" .labkey-publication-facets"));
        public static final Locator.CssLocator pubSummaryArea = pubSelectionPanel.append(Locator.css("#summaryArea"));
        public static final Locator.CssLocator pubCard = pubFinder.append(Locator.css(".labkey-publication-card"));
        public static final Locator.CssLocator pubCardBorderHighlight = pubFinder.append(Locator.css(".labkey-publication-highlight1"));
        public static final Locator.CssLocator pubCardBackgroundHighlight = pubFinder.append(Locator.css(".labkey-publication-highlight3"));
        public static final Locator.CssLocator groupLabel = Locator.css(".labkey-group-label");
        public static final Locator.NameLocator groupLabelInput = Locator.name("groupLabel");
        public static final Locator.CssLocator saveMenu = Locator.css("#saveMenu");
        public static final Locator.CssLocator loadMenu = Locator.css("#loadMenu");
        public static final Locator.IdLocator manageMenu = Locator.id("manageMenu");
        public static final Locator.CssLocator manageData = Locator.css(".labkey-publications-panel .labkey-finder-manage-data");

        public static final Locator.CssLocator getSearchInput(Locator.CssLocator locator)
        {
            return locator.append(" input.labkey-search-box");
        }

        public static Locator.CssLocator getClearAll(Locator.CssLocator locator)
        {
            return locator.append(" .labkey-clear-all");
        }

        public static Locator.CssLocator getActiveClearAll(Locator.CssLocator locator)
        {
            return locator.append(".active");
        }

        public static Locator.XPathLocator finderObjectTab(String objectName)
        {
            return Locator.tagWithText("span", objectName);
        }

    }

    public enum Dimension
    {
        // Common
        STUDIES("studies", null),

        // Study focused.
        SUBJECTS("subjects", null),
        VISIBILITY("visibility","Study.Visibility"),
        THERAPEUTIC_AREA("therapeutic area", "Study.Therapeutic Area"),
        STUDY_TYPE("study type", "Study.Study Type"),
        ASSAY("assay", "Study.Assay"),
        AGE_GROUP("age group", "Study.AgeGroup"),
        PHASE("phase", "Study.Phase"),
        CONDITION("condition", "Study.Condition"),

        // Publication focused.
        PUBLICATIONS("publications", null),
        STATUS("status", "Publication.Status"),
        COMPLETE("complete", "Complete"),
        IN_PROGRESS("in progress", "In Progress"),
        PUBLICATION_TYPE("publication type", "Publication.Publication Type"),
        PUB_THERAPEUTIC_AREA("therapeutic area", "Publication.Therapeutic Area"),
        YEAR("year", "Publication.Year"),
        PUBLICATION_JOURNAL("journal", "Publication.Journal"),
        PUB_STUDY("study", "Publication.Study"),
        PUB_ASSAY("assay", "Publication.Assay"),
        PUB_CONDITION("condition", "Publication.Condition");


        private String caption;
        private String hierarchyName;

        Dimension(String caption, String hierarchyName)
        {
            this.caption = caption;
            this.hierarchyName = hierarchyName;
        }

        public String getCaption()
        {
            return caption;
        }

        public String getHierarchyName()
        {
            return hierarchyName;
        }

        public static Dimension fromString(String value)
        {
            for (Dimension dimension : values())
            {
                if (value.toLowerCase().equals(dimension.getCaption()))
                    return dimension;
            }

            throw new IllegalArgumentException("No such dimension: " + value);
        }
    }


    public class GroupMenu extends Component
    {

        private final WebElement menu;
        private final Elements elements;

        private GroupMenu(WebElement menu)
        {
            this.menu = menu;
            elements = new Elements();
        }

        public void toggleMenu()
        {
            this.menu.click();
        }

        @Override
        public WebElement getComponentElement()
        {
            return menu;
        }

        public List<String> getActiveOptions()
        {
            return getOptions(elements.activeOption);
        }

        public List<String> getInactiveOptions()
        {
            return getOptions(elements.inactiveOption);
        }

        public void chooseOption(String optionText, boolean waitForUpdate)
        {
            log("Choosing menu option " + optionText);
            List<WebElement> activeOptions = findElements(elements.activeOption);
            for (WebElement option : activeOptions)
            {
                if (optionText.equals(option.getText().trim()))
                {
                    option.click();
                    if (waitForUpdate)
                        waitForGroupUpdate();
                    return;
                }
            }
        }

        private List<String> getOptions(Locator locator)
        {
            List<WebElement> options = findElements(locator);
            List<String> optionStrings = new ArrayList<String>();
            for (WebElement option : options)
            {
                optionStrings.add(option.getText().trim());
            }
            return optionStrings;
        }

        private class Elements
        {
            public Locator.CssLocator activeOption = Locator.css(".menu-item-link:not(.inactive)");
            public Locator.CssLocator inactiveOption = Locator.css(".menu-item-link.inactive");
        }
    }

    public class FacetGrid extends Component
    {

        private WebElement grid;
        private Locators locators;

        public FacetGrid(WebElement grid)
        {
            this.grid = grid;
            this.locators = new Locators();
        }

        @Override
        public WebElement getComponentElement()
        {
            return this.grid;
        }

        public boolean facetIsPresent(Dimension dimension)
        {
            return isElementPresent(locators.facetGroup(dimension));
        }

        public void toggleFacet(Dimension dimension, String name)
        {
            Locator.XPathLocator rowLocator = locators.facetMemberName(dimension, name);
            scrollIntoView(rowLocator);
            WebElement row = rowLocator.findElement(getDriver());

            doAndWaitForPageSignal(() -> row.click(), getCountSignal());
        }

        public void clearFilter(Dimension dimension)
        {
            WebElement clearEl = locators.facetClear(dimension).findElement(getDriver());
            scrollIntoView(clearEl);
            Assert.assertFalse("Attempting to clear filter that is not active", clearEl.getAttribute("class").contains("inactive"));
            clickAt(locators.facetClear(dimension), 3, 3, 0);
            waitForElement(Locator.xpath("//tr[contains(@data-recordid,'" + dimension.getHierarchyName() + "')]//span[contains(@class,'labkey-clear-filter')][contains(@class, 'inactive')]"));
        }

        public Map<Dimension, List<String>> getSelectedMembers()
        {
            Map<Dimension, List<String>> selections = new HashMap<>();
            for (Dimension dimension : Dimension.values())
            {
                if (dimension.getHierarchyName() != null)
                {
                    selections.put(dimension, getSelectedMembers(dimension));
                }
            }
            return selections;
        }

        public List<String> getSelectedMembers(Dimension dimension)
        {
            List<WebElement> selectedElements =  locators.facetMemberSelected(dimension).findElements(getDriver());
            List<String> selectedNames = new ArrayList<>();
            for (WebElement element: selectedElements)
            {
                selectedNames.add(locators.memberName.findElement(element).getText());
            }
            return selectedNames;
        }

        public Map<String, String> getMemberCounts(Dimension dimension)
        {
            List<WebElement> memberElements = locators.facetMembers(dimension).findElements(getDriver());
            Map<String, String> countMap = new HashMap<>();
            for (WebElement member : memberElements)
            {
                String name = locators.memberName.findElement(member).getText();
                String count = locators.memberCount.findElement(member).getText();
                countMap.put(name, count);
            }
            return countMap;

        }

        private class Locators
        {
            public Locator.XPathLocator facetMemberRow(Dimension dimension, String name)
            {
                return Locator.tagWithAttribute("tr", "data-recordid", "[" + dimension.getHierarchyName() + "].[" + name + "]");
            }

            public Locator.XPathLocator facetMemberName(Dimension dimension, String name)
            {
                return facetMemberRow(dimension, name).append("//span[contains(@class, 'labkey-facet-member-name')]");
            }

            public Locator.XPathLocator facetGroup(Dimension dimension)
            {
                return new Locator.XPathLocator("//tr[contains(@data-recordid,'" + dimension.getHierarchyName() + "')]");
            }

            public Locator.XPathLocator facetGroupTitle(Dimension dimension)
            {
                //tr[contains(@data-recordid,"Study.Therapeutic Area")]//div[@class="x4-grid-group-title"]
                return facetGroup(dimension).append("//div[@class='x4-grid-group-title']");
            }

            public Locator.XPathLocator facetClear(Dimension dimension)
            {
                return new Locator.XPathLocator("//tr[contains(@data-recordid,'" + dimension.getHierarchyName() + "')]//span[contains(@class,'labkey-clear-filter')]");
            }

            public Locator.XPathLocator facetMemberSelected(Dimension dimension)
            {
                //tr[contains(@data-recordid, 'Therapeutic Area')][contains(@class, 'x4-grid-row-selected')]
                return facetGroup(dimension).append("[contains(@class, 'x4-grid-row-selected')]");
            }

            public Locator.XPathLocator facetMembers(Dimension dimension)
            {

                return facetGroup(dimension).append("//span[contains(concat(' ', normalize-space(@class), ' '), ' labkey-facet-member ')]");
            }

            public Locator.CssLocator memberCount = Locator.css(".labkey-facet-member-count");
            public Locator.CssLocator memberName = Locator.css(".labkey-facet-member-name");
            public Locator.CssLocator emptyMemberName = Locator.css(".labkey-empty-member .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyMemberName = Locator.css(".labkey-facet-member:not(.labkey-empty-member) .labkey-facet-member-name");
            public Locator.CssLocator selectedMemberName = Locator.css(".x4-grid-row-selected .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyNonSelectedMemberName = Locator.css(".x4-grid-row:not(.x4-grid-row-selected) .labkey-facet-member:not(.labkey-empty-member) .labkey-member-name");
        }
    }

    public class SummaryPanel extends Component
    {
        private WebElement panel;
        private Elements elements;
        private Dimension dimension;

        private SummaryPanel(WebElement panel)
        {
            this.panel = panel;
            elements = new Elements();
        }

        @Override
        public WebElement getComponentElement()
        {
            return panel;
        }

        public List<String> getValues()
        {
            return getTexts(findElements(elements.member));
        }

        private class Elements
        {
            public Locator.CssLocator member = Locator.css(".labkey-facet-member");
        }
    }

    public class DataCard
    {
        WebElement card;
        Locators locators;
        String title;
        String accession;
        String pi;

        private DataCard(WebElement card)
        {
            this.card = card;
            locators = new Locators();
        }

        public WebElement getCardElement()
        {
            return card;
        }

        public StudySummaryWindow viewStudySummary()
        {
            clickAt(locators.viewStudyLink.findElement(card), 3, 3, 0);
            return new StudySummaryWindow(_test);
        }

        public void clickGoToStudy(String choice)
        {
            _ext4Helper.clickExt4MenuButton(false, locators.goToStudyLink, false, choice);
        }

        public void clickGoToStudy()
        {
            locators.goToStudyLink.findElement(card).click();
        }

        public String getStudyAccession()
        {
            return locators.studyAccession.findElement(card).getText();
        }

        public String getStudyShortName()
        {
            return locators.studyShortName.findElement(card).getText();
        }

        public String getStudyPI()
        {
            return locators.studyPI.findElement(card).getText();
        }

        public String getTitle()
        {
            if(testingStudies)
                return locators.studyTitle.findElement(card).getText();
            else
                return locators.pubTitle.findElement(card).getText();
        }

        public PublicationPanel viewDetail()
        {
            doAndWaitForPageSignal(() -> locators.pubMoreDetails.findElement(card).click(), PUBLICATION_DETAILS_SIGNAL);
            return new PublicationPanel(_test);
        }

        public void hideDetail()
        {
            clickAt(locators.pubLessDetails.findElement(card), 3, 3, 0);
        }

        public void clickViewDocument()
        {
            locators.pubViewDocument.findElement(card).click();
        }

        private class Locators
        {
            public Locator viewStudyLink = Locator.linkWithText("view summary");
            public Locator goToStudyLink = Locator.linkWithText("go to study");
            public Locator.XPathLocator goToStudyMenu = Locator.tagWithClass("div", "labkey-study-goto-menu");
            public Locator studyAccession = Locator.css(".labkey-study-card-accession");
            public Locator studyShortName = Locator.css(".labkey-study-card-short-name");
            public Locator studyPI = Locator.css(".labkey-study-card-pi");
            public Locator studyTitle = Locator.css(".labkey-study-card-title");
            public Locator pubTitle = Locator.css(".labkey-publication-title");
            public Locator pubViewDocument = Locator.linkWithText("view document");
            public Locator pubMoreDetails = Locator.tagWithClass("i", "fa-plus-square");
            public Locator pubLessDetails = Locator.tagWithClass("i", "fa-minus-square");
        }
    }

}
