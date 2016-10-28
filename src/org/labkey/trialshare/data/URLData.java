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
package org.labkey.trialshare.data;

/**
 * Created by susanh on 2/23/16.
 */
public class URLData
{
    Integer _index;
    String _link;
    String _linkText;
    String _title;

    public Integer getIndex()
    {
        return _index;
    }

    public void setIndex(Integer index)
    {
        _index = index;
    }

    public String getLink()
    {
        return _link;
    }

    public void setLink(String link)
    {
        _link = link;
    }

    public String getLinkText()
    {
        return _linkText;
    }

    public void setLinkText(String linkText)
    {
        _linkText = linkText;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String title)
    {
        _title = title;
    }
}
