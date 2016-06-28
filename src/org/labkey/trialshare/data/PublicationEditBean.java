package org.labkey.trialshare.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by susanh on 6/20/16.
 */
public class PublicationEditBean extends StudyPublicationBean
{
    private List<String> _studyIds = new ArrayList<>();
    private List<String> _conditions = new ArrayList<>();
    private List<String> _therapeuticAreas = new ArrayList<>();

    public PublicationEditBean() {}

    public PublicationEditBean(StudyPublicationBean base)
    {
        setPrimaryFields(base.getPrimaryFields());
    }

    public List<String> getConditions()
    {
        return _conditions;
    }

    public void setConditions(List<String> conditions)
    {
        this._conditions = conditions;
    }

    public List<String> getStudyIds()
    {
        return _studyIds;
    }

    public void setStudyIds(List<String> studyIds)
    {
        this._studyIds = studyIds;
    }

    public List<String> getTherapeuticAreas()
    {
        return _therapeuticAreas;
    }

    public void setTherapeuticAreas(List<String> therapeuticAreas)
    {
        this._therapeuticAreas = therapeuticAreas;
    }
}
