package org.labkey.trialshare.data;

/**
 * Created by susanh on 12/10/15.
 */
public class StudySubset
{
    private String _id;
    private String _name;
    private Boolean _default;

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String id)
    {
        _id = id;
    }

    public Boolean getDefault()
    {
        return _default;
    }

    public void setDefault(Boolean aDefault)
    {
        _default = aDefault;
    }
}
