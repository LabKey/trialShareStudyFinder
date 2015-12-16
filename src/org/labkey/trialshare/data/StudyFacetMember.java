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
    private Float percent;
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

    public Float getPercent()
    {
        return percent;
    }

    public void setPercent(Float percent)
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
