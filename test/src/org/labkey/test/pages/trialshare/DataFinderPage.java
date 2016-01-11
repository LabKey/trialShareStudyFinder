package org.labkey.test.pages.trialshare;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.SystemUtils;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.Component;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFinderPage extends LabKeyPage
{
    private static final String CONTROLLER = "trialshare";
    private static final String ACTION = "dataFinder";
    private static final String COUNT_SIGNAL = "dataFinderCountsUpdated";
    private static final String GROUP_UPDATED_SIGNAL = "participantGroupUpdated";

    public DataFinderPage(BaseWebDriverTest test)
    {
        super(test);
    }

    @Override
    protected void waitForPage()
    {
        _test.waitForElement(LabKeyPage.Locators.pageSignal(COUNT_SIGNAL));
    }

    protected void waitForGroupUpdate()
    {
        _test.waitForElement(LabKeyPage.Locators.pageSignal(GROUP_UPDATED_SIGNAL));
    }

    public static DataFinderPage goDirectlyToPage(BaseWebDriverTest test, String containerPath)
    {
        test.beginAt(WebTestHelper.buildURL(CONTROLLER, containerPath, ACTION));
        return new DataFinderPage(test);
    }

    public void selectStudySubset(String text)
    {
        // FIXME isn't there a general method for asking "is this combo list item already selected"?
        _ext4Helper.openComboList(Locators.studySubsetCombo);
        if (!_test.isElementPresent(Ext4Helper.Locators.comboListItemSelected().withText(text)))
        {
            _test.doAndWaitForPageSignal(() ->_ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT), COUNT_SIGNAL);

        }
        else // FIXME you should be able to just close the combo box at this point, but the close method assumes you've chosen something from teh lis
        {
            _ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT);
        }
//        Locator.XPathLocator listItem = Ext4Helper.Locators.comboListItemSelected().withText(text);
//        WebElement element = listItem.waitForElement(_test.getDriver(), BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
//        boolean elementAlreadySelected = element.getAttribute("class").contains("selected");
//
//        if (!elementAlreadySelected)
//        {
//            _test.doAndWaitForPageSignal(() -> _ext4Helper.selectComboBoxItem(Locators.studySubsetCombo, text), COUNT_SIGNAL);
//        }
//        String selectedText = _test.getSelectedOptionText(Locators.studySubsetChooser);
//        if (!selectedText.equals(text))
//        {
//            _test.doAndWaitForPageSignal(() -> _test.selectOptionByText(Locators.studySubsetChooser, text), COUNT_SIGNAL);
//        }

    }

    @LogMethod
    public void studySearch(@LoggedParam final String search)
    {
        _test.doAndWaitForPageSignal(() -> _test.setFormElement(Locators.studySearchInput, search), COUNT_SIGNAL);
    }

    @LogMethod(quiet = true)
    public void clearSearch()
    {
        if (_test.isElementPresent(Locators.studySearchInput) && !_test.getFormElement(Locators.studySearchInput).isEmpty())
            studySearch(" ");
    }

    public void saveGroup(String name)
    {
        _test.setFormElement(Locators.groupLabelInput, name);
        _test.clickButtonContainingText("Save", BaseWebDriverTest.WAIT_FOR_EXT_MASK_TO_DISSAPEAR);
        waitForGroupUpdate();
    }

    public String getGroupLabel()
    {
        return Locators.groupLabel.findElement(_test.getDriver()).getText().trim();
    }

    public GroupMenu getMenu(Locator locator)
    {
        return new GroupMenu(locator.findElement(_test.getDriver()));
    }

    public boolean menuIsDisabled(Locator.CssLocator locator)
    {
        return _test.isElementPresent(locator.append(" .labkey-disabled-text-link"));
    }

    public void openMenu(Locator locator)
    {
        locator.findElement(_test.getDriver()).click();
    }

    public Map<Dimension, Integer> getSummaryCounts()
    {
        WebElement summaryElement = Locators.summaryArea.findElement(_test.getDriver());
        DimensionPanel summary = new DimensionPanel(summaryElement);

        Map<Dimension, Integer> countMap = new HashMap<>();
        for (String value : summary.getValues())
        {
            String[] parts = value.split("\n");
            Dimension dimension = Dimension.fromString(parts[0]);
            Integer count = Integer.parseInt(parts[1].trim());
            countMap.put(dimension, count);
        }
        return countMap;
    }

    public List<StudyCard> getStudyCards()
    {
        List<WebElement> studyCardEls = Locators.studyCard.findElements(_test.getDriver());
        List<StudyCard> studyCards = new ArrayList<>();

        for (WebElement el : studyCardEls)
        {
            studyCards.add(new StudyCard(el));
        }

        return studyCards;
    }

    public List<DimensionMember> getSelectedMembers()
    {
        List<DimensionMember> members = new ArrayList<>();
        for (WebElement el : Locators.selection.findElements(_test.getDriver()))
        {
            members.add(new DimensionMember(el));
        }
        return members;
    }

    public Map<Dimension, List<String>> getSelectionValues()
    {
        Map<Dimension, List<String>> selectionValues = new HashMap<>();

        for (DimensionMember member : getSelectedMembers())
        {
            List<String> values = selectionValues.get(member.getDimension());
            if (values == null)
            {
                values = new ArrayList<>();
                selectionValues.put(member.getDimension(), values);
            }
            values.add(member.getName());
        }

        return selectionValues;
    }

//    public DimensionPanel getFacetsGrid()
//    {
//        return new DimensionPanel(Locators.facetPanel.findElement(_test.getDriver()));
//    }

    public Map<Dimension, DimensionPanel> getAllDimensionPanels()
    {
        return getDimensionPanels(Locators.facetHeader);
    }

    public Map<Dimension, DimensionPanel> getDimensionPanels(Locator locator)
    {
        List<WebElement> dimensionPanelEls = locator.findElements(_test.getDriver());
        Map<Dimension, DimensionPanel> dimensionPanels = new HashMap<>();

        for (WebElement el : dimensionPanelEls)
        {
            DimensionPanel panel = new DimensionPanel(el);
            dimensionPanels.put(panel.getDimension(), panel);
        }

        return dimensionPanels;
    }

    public void clearAllFilters()
    {
        if (_test.isElementPresent(Locators.activeClearAll))
        {
            final WebElement clearAll = Locators.activeClearAll.findElement(_test.getDriver());
            if (clearAll.isDisplayed())
            {
                _test.doAndWaitForPageSignal(clearAll::click, COUNT_SIGNAL);
            }
        }
    }

    public void dismissTour()
    {
        _test.shortWait().until(new Predicate<WebDriver>()
        {
            @Override
            public boolean apply(WebDriver webDriver)
            {
                try
                {
                    return (Boolean) _test.executeScript("" +
                            "if (window.hopscotch)" +
                            "  return !hopscotch.endTour().isActive;" +
                            "else" +
                            "  return true;");
                }
                catch (Exception recheck)
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
        public static final Locator.CssLocator studyFinder = Locator.css(".labkey-data-finder-outer");
        public static final Locator.CssLocator studySearchInput = studyFinder.append(Locator.css("#searchTerms"));
        public static final Locator.XPathLocator studySubsetCombo = Ext4Helper.Locators.formItemWithInputNamed("studySubsetSelect");
        public static final Locator.CssLocator studyCard = studyFinder.append(Locator.css(".labkey-study-card"));
        public static final Locator.CssLocator selectionPanel = studyFinder.append(Locator.css(".labkey-facet-selection-panel"));
        public static final Locator.CssLocator facetPanel = selectionPanel.append(Locator.css(" .labkey-study-facets"));
        public static final Locator.CssLocator facetHeader = Locator.css(".x4-grid-group-title");
//        public static final Locator.CssLocator facet = facetPanel.append(" .labkey-facet-header");
        public static final Locator.CssLocator summaryArea = selectionPanel.append(Locator.css("#summaryArea"));
        public static final Locator.CssLocator selection = facetPanel.append(Locator.css(" .selected-member"));
        public static final Locator.CssLocator activeClearAll = Locator.css(".labkey-clear-all.active");
        public static final Locator.CssLocator groupLabel = Locator.css(".labkey-group-label");
        public static final Locator.NameLocator groupLabelInput = Locator.name("groupLabel");
        public static final Locator.CssLocator saveMenu = Locator.css("#saveMenu");
        public static final Locator.CssLocator loadMenu = Locator.css("#loadMenu");
        public static final Locator.IdLocator manageMenu = Locator.id("manageMenu");

    }

    public enum Dimension
    {
        STUDIES("studies"),
        SUBJECTS("subjects"),
        THERAPEUTIC_AREA("therapeutic area"),
        STUDY_TYPE("study type"),
        ASSAY("assay"),
        AGE_GROUP("age group"),
        PHASE("phase"),
        CONDITION("condition");

        private String caption;

        Dimension(String caption)
        {
            this.caption = caption;
        }

        public String getCaption()
        {
            return caption;
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
            _test.log("Choosing menu option " + optionText);
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

    public class DimensionPanel extends Component
    {
        private WebElement panel;
        private Elements elements;
        private Dimension dimension;

        private DimensionPanel(WebElement panel)
        {
            this.panel = panel;
            elements = new Elements();
        }

        public Dimension getDimension()
        {
            if (dimension == null)
            {
                dimension = Dimension.fromString(findElement(elements.dimension).getText());
            }

            return dimension;
        }

        @Override
        public WebElement getComponentElement()
        {
            return panel;
        }

        public List<String> getValues()
        {
            displayDimension();
            return _test.getTexts(findElements(elements.member));
        }

        public List<String> getEmptyValues()
        {
            displayDimension();
            return _test.getTexts(findElements(elements.emptyMemberName));
        }

        public List<String> getNonEmptyValues()
        {
            displayDimension();
            return _test.getTexts(findElements(elements.nonEmptyMemberName));
        }

        public List<String> getSelectedValues()
        {
            displayDimension();
            return _test.getTexts(findElements(elements.selectedMemberName));
        }

        public void displayDimension()
        {
            if (!isDisplayed())
            {
                WebElement caption = findElement(elements.facetCaption);
                caption.click();
            }
        }

        public boolean isDisplayed()
        {
            return !this.panel.getAttribute("class").contains("collapsed");
        }

        public String selectFirstIntersectingMeasure()
        {
            displayDimension();

            WebElement el = findElement(elements.nonEmptyNonSelectedMemberName);
            String value = el.getText();

            addToSelection(el);

            waitForSelection(value);
            return value;
        }

        public Map<String, Integer> getMemberCounts()
        {
            displayDimension();
            Map<String, Integer> countMap = new HashMap<>();
            List<WebElement> members = findElements(elements.member);
            for (WebElement member : members)
            {
                String[] parts = member.getText().split("\n");
                String name = parts[0].trim();
                Integer count = Integer.valueOf(parts[1].trim());
                countMap.put(name, count);
            }

            return countMap;
        }

        public void selectMember(String memberName)
        {
            displayDimension();
            select(findElement(elements.memberName.withText(memberName)));
            waitForSelection(memberName);
        }

        public void deselectMember(String memberName)
        {
            WebElement member = findElement(elements.member.containing(memberName));
            WebElement check = member.findElement(By.cssSelector(elements.selectedMemberCheck.getLocatorString()));
            select(check);
        }

        public void addToSelection(String value)
        {
            displayDimension();
            addToSelection(findElement(elements.member.withText(value)));
            waitForSelection(value);
        }

        public void clearFilters()
        {
            select(findElement(elements.clearFilters));
        }

        private void select(final WebElement value)
        {
            _test.doAndWaitForPageSignal(value::click, COUNT_SIGNAL);
        }

        private void addToSelection(final WebElement value)
        {
            _test.doAndWaitForPageSignal(() -> controlClick(value), COUNT_SIGNAL);
        }

        private void controlClick(WebElement el)
        {
            Keys multiSelectKey;
            if (SystemUtils.IS_OS_MAC)
                multiSelectKey = Keys.COMMAND;
            else
                multiSelectKey = Keys.CONTROL;

            Actions builder = new Actions(_test.getDriver());
            builder.keyDown(multiSelectKey).build().perform();
            el.click();
            builder.keyUp(multiSelectKey).build().perform();
        }

        private void waitForSelection(String value)
        {
            elements.selectedMemberName.containing(value).waitForElement(panel, BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
        }

        private class Elements
        {
            public Locator.CssLocator facetCaption = Locator.css(".labkey-facet-caption");
            public Locator.CssLocator dimension = Locator.css(".labkey-facet-caption > span");
            public Locator.CssLocator member = Locator.css(".labkey-facet-member");
            public Locator.CssLocator memberName = Locator.css(".labkey-facet-member-name");
            public Locator.CssLocator selectedMemberCheck = Locator.css(".labkey-facet-member .labkey-facet-member-indicator.selected");
            public Locator.CssLocator emptyMemberName = Locator.css(".labkey-empty-member .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyMemberName = Locator.css(".labkey-facet-member:not(.labkey-empty-member) .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyNonSelectedMemberName = Locator.css(".x4-grid-row:not(.x4-grid-row-selected) .labkey-facet-member:not(.labkey-empty-member) .labkey-member-name");
            public Locator.CssLocator selectedMemberName = Locator.css(".x4-grid-row-selected .labkey-member-name");
            public Locator.CssLocator clearFilters = Locator.css(".labkey-clear-filter");
        }
    }

    public class StudyCard
    {
        WebElement card;
        Elements elements;
        String title;
        String accession;
        String pi;

        private StudyCard(WebElement card)
        {
            this.card = card;
            elements = new Elements();
        }

        public WebElement getCardElement()
        {
            return card;
        }

        public StudySummaryWindow viewSummary()
        {
            elements.viewStudyLink.findElement(card).click();
            return new StudySummaryWindow(_test);
        }

        public void clickGoToStudy()
        {
            _test.clickAndWait(elements.goToStudyLink.findElement(card));
        }

        public String getAccession()
        {
            return elements.accession.findElement(card).getText();
        }

        public String getShortName()
        {
            return elements.shortName.findElement(card).getText();
        }

        public String getPI()
        {
            return elements.PI.findElement(card).getText();
        }

        public String getTitle()
        {
            return elements.title.findElement(card).getText();
        }

        private class Elements
        {
            public Locator viewStudyLink = Locator.linkWithText("view summary");
            public Locator goToStudyLink = Locator.linkWithText("go to study");
            public Locator accession = Locator.css(".labkey-study-card-accession");
            public Locator shortName = Locator.css(".labkey-study-card-short-name");
            public Locator PI = Locator.css(".labkey-study-card-pi");
            public Locator title = Locator.css(".labkey-study-card-title");
        }
    }

    public class DimensionMember extends Component
    {
        private final Elements elements;
        private final WebElement memberElement;
        private Dimension dimension;

        private DimensionMember(final WebElement memberElement)
        {
            this.memberElement = memberElement;
            elements = new Elements();
            parseMemberId();
        }

        private void parseMemberId()
        {
            String id = this.memberElement.getAttribute("id");
            String[] parts = id.split("_");
            this.dimension = Dimension.fromString(parts[1]);
        }

        @Override
        public WebElement getComponentElement()
        {
            return memberElement;
        }

        public Dimension getDimension()
        {
            return dimension;
        }

        public String getName()
        {
            return elements.memberName.findElement(memberElement).getText();
        }

        public Integer getCount()
        {
            return Integer.parseInt(elements.memberCount.findElement(memberElement).getText());
        }

        private class Elements
        {
            public Locator memberName = Locator.css(".member-name");
            public Locator memberCount = Locator.css(".member-count");
        }
    }
}
