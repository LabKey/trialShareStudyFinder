package org.labkey.trialshare.query;

import org.labkey.api.query.QueryView;
import org.labkey.api.view.ViewContext;
import org.springframework.validation.BindException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by susanh on 6/14/16.
 */
public class ManagePublicationsQueryView extends ManageCubeObjectQueryView
{
    private static final Set<String> _defaultColumns = new HashSet<>();
    static
    {
        _defaultColumns.add("title");
        _defaultColumns.add("show");
        _defaultColumns.add("key");
    }
    public ManagePublicationsQueryView(ViewContext context, BindException errors)
    {
        super(context, errors);

        setSettings(getSchema().getSettings(context, QueryView.DATAREGIONNAME_DEFAULT, TrialShareQuerySchema.PUBLICATION_TABLE));
    }

    @Override
    protected String getKeyField() { return "Key"; }

    @Override
    protected Set<String> getDefaultColumns()
    {
        return _defaultColumns;
    }
}
