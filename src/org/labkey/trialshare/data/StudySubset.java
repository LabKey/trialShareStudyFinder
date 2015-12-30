package org.labkey.trialshare.data;

/**
 * Created by susanh on 12/10/15.
 */
public class StudySubset
{
    private String _id;
    private String _name;
    private Boolean _isDefault;

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

    public Boolean getIsDefault()
    {
        return _isDefault;
    }

    public void setIsDefault(Boolean isDefault)
    {
        _isDefault = isDefault;
    }
}
