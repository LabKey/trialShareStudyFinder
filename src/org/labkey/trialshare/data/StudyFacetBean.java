package org.labkey.trialshare.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by susanh on 12/8/15.
 */
public class StudyFacetBean
{
    private String name;
    private String caption;
    private String pluralName;
    private String hierarchyName;
    private String levelName;
    private String allMemberName;
    private String defaultFilterType;
    private List<StudyFacetMember> members;
    private List<String> filterOptions;

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    @JsonIgnore
    public List<StudyFacetMember> getMembers()
    {
        return members;
    }

    public void setMembers(List<StudyFacetMember> members)
    {
        this.members = members;
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

    public List<String> getFilterOptions()
    {
        return filterOptions;
    }

    public void setFilterOptions(List<String> filterOptions)
    {
        this.filterOptions = filterOptions;
    }

    public String getDefaultFilterType()
    {
        return defaultFilterType;
    }

    public void setDefaultFilterType(String defaultFilterType)
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
}
