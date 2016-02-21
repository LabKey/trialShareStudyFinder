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
package org.labkey.trialshare.data;

import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;

/**
 * Created by susanh on 12/14/15.
 */
public class FacetFilter
{
    public enum Type { OR, AND };

    private Type _type;
    private String _caption;
    private Boolean _default;

    public String getCaption()
    {
        return _caption;
    }

    public void setCaption(String caption)
    {
        _caption = caption;
    }

    public Type getType()
    {
        return _type;
    }

    public void setType(Type type)
    {
        _type = type;
    }

    public Boolean geDefault()
    {
        return _default;
    }

    public void setDefault(Boolean aDefault)
    {
        _default = aDefault;
    }
}
