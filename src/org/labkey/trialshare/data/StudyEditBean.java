package org.labkey.trialshare.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by susanh on 6/23/16.
 */
public class StudyEditBean extends StudyBean
{
    private List<String> _ageGroups = new ArrayList<>();
    private List<String> _phases = new ArrayList<>();
    private List<String> _conditions = new ArrayList<>();
    private List<String> _therapeuticAreas = new ArrayList<>();

    public List<String> getAgeGroups()
    {
        return _ageGroups;
    }

    public void setAgeGroups(List<String> ageGroups)
    {
        _ageGroups = ageGroups;
    }

    public List<String> getConditions()
    {
        return _conditions;
    }

    public void setConditions(List<String> conditions)
    {
        this._conditions = conditions;
    }

    public List<String> getTherapeuticAreas()
    {
        return _therapeuticAreas;
    }

    public void setTherapeuticAreas(List<String> therapeuticAreas)
    {
        this._therapeuticAreas = therapeuticAreas;
    }

    public List<String> getPhases()
    {
        return _phases;
    }

    public void setPhases(List<String> phases)
    {
        _phases = phases;
    }


}
