package org.labkey.trialshare.data;

/**
 * Created by susanh on 12/8/15.
 */
public class StudyFacetMember
{
    private String name;
    private String uniqueName;
    private Integer count;
    private Float percent;

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
}
