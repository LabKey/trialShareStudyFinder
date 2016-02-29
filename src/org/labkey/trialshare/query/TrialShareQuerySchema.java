package org.labkey.trialshare.query;

import org.labkey.api.data.Container;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;

/**
 * Created by susanh on 2/23/16.
 */
public class TrialShareQuerySchema
{
    public static final String STUDY_TABLE = "studyProperties";
    public static final String STUDY_ACCESS_TABLE = "studyAccess";
    public static final String PUBLICATION_TABLE = "manuscriptsAndAbstracts";
    public static final String STUDY_ASSAY_TABLE = "studyAssay";

    // study visibility values
    public static final String OPERATIONAL_VISIBILITY = "Operational";
    public static final String PUBLIC_VISIBILITY = "Public";

    // publication status values
    public static final String IN_PROGRESS_STATUS = "In Progress";
    public static final String COMPLETED_STATUS = "Complete";

    public static QuerySchema getSchema(User user, Container container)
    {
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        return coreSchema.getSchema("lists");
    }
}
