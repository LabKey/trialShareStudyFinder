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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by susanh on 12/8/15.
 */
public class StudyFacetBean
{
    private String name;
    private String pluralName;
    private String hierarchyName;
    private String levelName;
    private String allMemberName;
    private FacetFilter.Type defaultFilterType;
    private List<FacetFilter> filterOptions;
    private Integer ordinal;
    private Boolean displayFacet = true;
    private List<String> defaultSelectedUniqueNames;

    public StudyFacetBean() {}

    public StudyFacetBean(String name, String pluralName, String hierarchyName, String levelName, String allMembersName, FacetFilter.Type defaultFilterType, Integer ordinal) {
        this.name = name;
        this.pluralName = pluralName;
        this.hierarchyName = hierarchyName;
        this.levelName = levelName;
        this.allMemberName = allMembersName;
        this.defaultFilterType = defaultFilterType;
        this.ordinal = ordinal;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAllMemberName()
    {
        return allMemberName;
    }

    public void setAllMemberName(String allMemberName)
    {
        this.allMemberName = allMemberName;
    }

    public List<FacetFilter> getFilterOptions()
    {
        return filterOptions;
    }

    public void setFilterOptions(List<FacetFilter> filterOptions)
    {
        this.filterOptions = filterOptions;
    }

    public FacetFilter.Type getDefaultFilterType()
    {
        return defaultFilterType;
    }

    public void setDefaultFilterType(FacetFilter.Type defaultFilterType)
    {
        this.defaultFilterType = defaultFilterType;
    }

    public String getHierarchyName()
    {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName)
    {
        this.hierarchyName = hierarchyName;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public String getPluralName()
    {
        return pluralName;
    }

    public void setPluralName(String pluralName)
    {
        this.pluralName = pluralName;
    }

    public Integer getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal)
    {
        this.ordinal = ordinal;
    }

    public Boolean getDisplayFacet()
    {
        return displayFacet;
    }

    public void setDisplayFacet(Boolean displayFacet)
    {
        this.displayFacet = displayFacet;
    }

    public List<String> getDefaultSelectedUniqueNames()
    {
        return defaultSelectedUniqueNames;
    }

    public void setDefaultSelectedUniqueNames(List<String> defaultSelectedUniqueNames)
    {
        this.defaultSelectedUniqueNames = defaultSelectedUniqueNames;
    }
}
