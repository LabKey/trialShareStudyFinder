/*
 * Copyright (c) 2016-2019 LabKey Corporation
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StudySummaryWindow extends StudySummaryPanel
{
    private WebElement _window;

    public StudySummaryWindow(BaseWebDriverTest test)
    {
        super(test);
        _window = Locator.css("div.labkey-study-detail").findElement(test.getDriver());
    }

    public void closeWindow()
    {
        elements().closeButton.findElement(_window).click();
        _test.shortWait().until(ExpectedConditions.stalenessOf(_window));
    }

    @Override
    public Elements elements()
    {
        return new Elements();
    }

    private class Elements extends StudySummaryPanel.Elements
    {
        Locator.CssLocator closeButton = Locator.css(".x4-tool-close");
    }
}
