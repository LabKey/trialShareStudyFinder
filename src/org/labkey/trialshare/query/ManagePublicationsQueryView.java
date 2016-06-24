package org.labkey.trialshare.query;

import org.labkey.api.data.Container;
import org.labkey.api.data.TableInfo;
import org.labkey.api.query.QueryView;
import org.labkey.api.security.User;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
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

    protected TableInfo getTable(User user, Container container)
    {
        return new TrialShareQuerySchema(user, container).getPublicationsTableInfo();
    }

    @Override
    protected TrialShareController.ObjectName getCubeObjectName() { return TrialShareController.ObjectName.publication; }

    @Override
    protected String getKeyField() { return "Key"; }

    @Override
    protected Set<String> getDefaultColumns()
    {
        return _defaultColumns;
    }
}
