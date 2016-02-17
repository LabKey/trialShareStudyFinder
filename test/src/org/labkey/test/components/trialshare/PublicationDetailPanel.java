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
import org.labkey.test.util.LogMethod;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PublicationDetailPanel extends Component
{
    protected BaseWebDriverTest _test;
    protected WebElement _panel;

    public PublicationDetailPanel(BaseWebDriverTest test)
    {
        _test = test;
        _test.waitForElement(Locators.self);
        _panel = Locators.self.waitForElement(test.getDriver(), BaseWebDriverTest.WAIT_FOR_JAVASCRIPT);
    }

    @Override
    public WebElement getComponentElement()
    {
        return _panel;
    }

    public String getAuthor()
    {
        return getTextOfElement(Locators.author);
    }

    public String getTitle()
    {
        return getTextOfElement(Locators.title);
    }

    public String getCitation()
    {
        return getTextOfElement(Locators.citation);
    }

    public String getPMID()
    {
        return getTextOfElement(Locators.PMID);
    }
    public void clickPMID() { _test.click(Locators.PMID); }

    public String getPMCID()
    {
        return getTextOfElement(Locators.PMCID);
    }
    public void clickPMCID() { _test.click(Locators.PMCID); }

    public String getDOI()
    {
        return getTextOfElement(Locators.DOI);
    }
    public void clickDOI() { _test.click(Locators.DOI); }

    public String getStudyShortName()
    {
        return getTextOfElement(Locators.studyShortName);
    }

    public String getDetailPageText()
    {
        return getTextOfElement(Locators.pubDetailPanel);
    }

    @LogMethod(quiet = true)
    private String getTextOfElement(Locator el)
    {
        try
        {
            return _test.getText(el);
        }
        catch (org.openqa.selenium.NoSuchElementException nse)
        {
            _test.log("Element not found returning an empty string.");
            return "";
        }
    }

    public void closeWindow()
    {
        _test.click(Locators.closeButton);
        _test.shortWait().until(ExpectedConditions.invisibilityOfElementLocated(Locators.pubDetailPanel.toBy()));
    }

    private static class Locators
    {
        private static Locator self = Locator.css(".labkey-publication-detail");
        private static Locator.CssLocator pubDetailPanel = Locator.css(".labkey-publication-detail");
        private static Locator.CssLocator author = pubDetailPanel.append(Locator.css(".labkey-publication-author"));
        private static Locator.CssLocator title = pubDetailPanel.append(Locator.css(".labkey-publication-title"));
        private static Locator.CssLocator citation = pubDetailPanel.append(Locator.css(".labkey-publication-citation"));
        private static Locator.XPathLocator PMID = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')][contains(text(), 'PMID')]//a");
        private static Locator.XPathLocator PMCID = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')][contains(text(), 'PMCID')]//a");
        private static Locator.XPathLocator DOI = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')][contains(text(), 'DOI')]//a");
        private static Locator.CssLocator studyShortName = Locator.css(".labkey-study-short-name");
        private static Locator.CssLocator closeButton = Locator.css(".x4-tool-close");
    }

}
