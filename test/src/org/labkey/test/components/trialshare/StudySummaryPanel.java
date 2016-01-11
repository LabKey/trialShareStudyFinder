package org.labkey.test.components.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public abstract class StudySummaryPanel
{
    protected BaseWebDriverTest _test;
    protected WebElement _panel;

    public StudySummaryPanel(BaseWebDriverTest test)
    {
        _test = test;
        _panel = Locators.self.waitForElement(test.getDriver(), BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
        elements().accession.waitForElement(_panel, BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
    }

    public String getAccession()
    {
        return elements().accession.findElement(_panel).getText();
    }

    public String getShortName()
    {
        return elements().shortName.findElement(_panel).getText();
    }

    public String getTitle()
    {
        return elements().title.findElement(_panel).getText();
    }

    public String getPI()
    {
        return elements().PI.findElement(_panel).getText();
    }

    public String getOrganization()
    {
        return elements().organization.findElement(_panel).getText();
    }

    public List<Paper> getPapers()
    {
        List<WebElement> paperEls = elements().paper.findElements(_panel);
        List<Paper> papers = new ArrayList<>();

        for (WebElement el : paperEls)
        {
            papers.add(new Paper(el));
        }

        return papers;
    }

    protected Elements elements()
    {
        return new Elements();
    }

    protected class Elements
    {
        Locator.CssLocator accession = Locator.css(".labkey-study-accession");
        Locator.CssLocator shortName = Locator.css(".labkey-study-short-name");
        Locator.CssLocator title = Locator.css(".labkey-study-title");
        Locator.CssLocator PI = Locator.css(".labkey-study-pi");
        Locator.CssLocator organization = Locator.css(".labkey-study-organization");
        Locator.CssLocator paper = Locator.css(".labkey-study-papers > p");
    }

    private static class Locators
    {
        private static Locator self = Locator.css(".labkey-study-details");
    }

    private class Paper
    {
        private WebElement paper;

        private Paper(WebElement el)
        {
            this.paper = el;
        }

        public String getJournal()
        {
            return Locator.css(".labkey-publication-journal").findElement(paper).getText();
        }

        public String getYear()
        {
            return Locator.css(".labkey-publication-year").findElement(paper).getText();
        }

        public String getTitle()
        {
            return Locator.css(".labkey-publication-title").findElement(paper).getText();
        }

        public WebElement getPubMedLink()
        {
            return Locator.linkWithText("PubMed").findElement(paper);
        }

        public String getCitation()
        {
            return Locator.css(".labkey-publication-citation").findElement(paper).getText();
        }

    }
}
