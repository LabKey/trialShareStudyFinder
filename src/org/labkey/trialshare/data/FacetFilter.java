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
}
