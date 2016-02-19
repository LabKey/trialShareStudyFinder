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
package org.labkey.test.components.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.components.Component;
import org.labkey.test.components.ComponentElements;
import org.labkey.test.selenium.LazyWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public abstract class StudySummaryPanel extends Component
{
    protected BaseWebDriverTest _test;
    protected WebElement _panel;

    public StudySummaryPanel(BaseWebDriverTest test)
    {
        _test = test;
        _test.waitForElement(Locators.self);
        _panel = Locators.self.waitForElement(test.getDriver(), BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
        elements().accession.isDisplayed();
    }

    @Override
    public WebElement getComponentElement()
    {
        return _panel;
    }

    public String getAccession()
    {
        return elements().accession.getText();
    }

    public String getShortName()
    {
        return elements().shortName.getText();
    }

    public String getTitle()
    {
        return elements().title.getText();
    }

    public String getPI()
    {
        return elements().PI.getText();
    }

    public String getOrganization()
    {
        return elements().organization.getText();
    }

    public List<Paper> getPapers()
    {
        List<WebElement> paperEls = Locator.css(".labkey-study-papers > p").findElements(_panel);
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

    protected class Elements extends ComponentElements
    {
        @Override
        protected SearchContext getContext()
        {
            return _panel;
        }

        WebElement accession = new LazyWebElement(Locator.css(".labkey-study-accession"), this);
        WebElement shortName = new LazyWebElement(Locator.css(".labkey-study-short-name"), this);
        WebElement title = new LazyWebElement(Locator.css(".labkey-study-title"), this);
        WebElement PI = new LazyWebElement(Locator.css(".labkey-study-pi"), this);
        WebElement organization = new LazyWebElement(Locator.css(".labkey-study-organization"), this);
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
