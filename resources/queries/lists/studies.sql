SELECT 
    sa.StudyId,
    sa.StudyContainer,
    sc.condition,
    sas.Assay,
    sph.Phase,
    sag.AgeGroup,
    sta.TherapeuticArea,
    sp.shortName,
    sp.Title,
    sp.StudyType,
    sp.Description,
    sp.Investigator
FROM StudyAccess sa
           LEFT JOIN StudyProperties sp ON sa.StudyId = sp.StudyId
           LEFT JOIN (SELECT StudyId, group_concat(Assay) AS Assay FROM StudyAssay GROUP BY StudyId) sas on sa.StudyId = sas.StudyId
           LEFT JOIN (SELECT StudyId, group_concat(Condition) As Condition FROM StudyCondition GROUP BY StudyId) sc on sa.StudyId = sc.StudyId
           LEFT JOIN (SELECT StudyId, group_concat(AgeGroup) AS AgeGroup FROM StudyAgeGroup GROUP BY StudyId) sag on sa.StudyId = sag.StudyId
           LEFT JOIN (SELECT StudyId, group_concat(Phase) AS Phase FROM StudyPhase GROUP BY StudyId) sph on sa.StudyId = sph.StudyId
           LEFT JOIN (SELECT StudyId, group_concat(TherapeuticArea) AS TherapeuticArea FROM StudyTherapeuticArea GROUP BY StudyId) sta on sa.StudyId = sta.StudyId