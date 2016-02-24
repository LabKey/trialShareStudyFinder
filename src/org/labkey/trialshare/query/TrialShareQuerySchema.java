package org.labkey.trialshare.query;

/**
 * Created by susanh on 2/23/16.
 */
public class TrialShareQuerySchema
{
    public static final String NAME = "lists";
    public static final String STUDY_TABLE = "studyProperties";
    public static final String STUDY_CONTAINER_TABLE = "studyContainer";
    public static final String PUBLICATION_TABLE = "manuscriptsAndAbstracts";

    // study visibility values
    public static final String OPERATIONAL_VISIBILITY = "Operational";
    public static final String PUBLIC_VISIBILITY = "Public";

    // publication status values
    public static final String IN_PROGRESS_STATUS = "In Progress";
    public static final String COMPLETED_STATUS = "Complete";
}
