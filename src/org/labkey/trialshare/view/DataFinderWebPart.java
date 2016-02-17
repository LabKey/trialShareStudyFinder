/*
 * Copyright (c) 2015-2016 LabKey Corporation
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
package org.labkey.trialshare.view;

import org.labkey.api.data.Container;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.JspView;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
import org.labkey.trialshare.TrialShareModule;

public class DataFinderWebPart extends JspView
{
    boolean isAutoResize = false;

    public boolean isAutoResize()
    {
        return isAutoResize;
    }

    public void setIsAutoResize(boolean isAutoResize)
    {
        this.isAutoResize = isAutoResize;
    }

    public DataFinderWebPart(Container c)
    {
        super("/org/labkey/trialshare/view/dataFinder.jsp");
        this.setModelBean(TrialShareController.getFinderBean());

        setTitle("Data Finder");
        setTitleHref(new ActionURL(TrialShareController.DataFinderAction.class, c));
    }
    public DataFinderWebPart(ViewContext v)
    {
        this(v.getContainer());
    }


    @Override
    public void setIsOnlyWebPartOnPage(boolean b)
    {
        setIsAutoResize(b);
    }
}
