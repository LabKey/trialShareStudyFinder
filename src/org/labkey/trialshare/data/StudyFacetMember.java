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

import java.util.List;
import java.util.Map;

/**
 * Created by susanh on 12/8/15.
 */
public class StudyFacetMember
{
    private String name;
    private String uniqueName;
    private Integer count;
    private Integer percent;
    private String facetName;
    private String facetUniqueName;
    private List<FacetFilter> filterOptions;

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getPercent()
    {
        return percent;
    }

    public void setPercent(Integer percent)
    {
        this.percent = percent;
    }

    public String getUniqueName()
    {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName)
    {
        this.uniqueName = uniqueName;
    }

    public String getFacetName()
    {
        return facetName;
    }

    public void setFacetName(String facetName)
    {
        this.facetName = facetName;
    }

    public String getFacetUniqueName()
    {
        return facetUniqueName;
    }

    public void setFacetUniqueName(String facetUniqueName)
    {
        this.facetUniqueName = facetUniqueName;
    }

    public List<FacetFilter> getFilterOptions()
    {
        return filterOptions;
    }

    public void setFilterOptions(List<FacetFilter> filterOptions)
    {
        this.filterOptions = filterOptions;
    }
}
