SELECT
        pub.Key as PublicationId,
        pub.Title,
        pub.Author,
        pub.Citation,
        pub.DOI,
        pub.PMID,
        pub.PMCID,
        pub.PublicationType,
        pub.Year,
        pub.Journal,
        pub.Status,
        pub.SubmissionStatus,
        pub.Study as PrimaryStudy,
        pub.StudyId as PrimaryStudyId,
        pub.AbstractText,
        pub.Keywords,
        pub.PermissionsContainer,
        pub.ManuscriptContainer,
        pc.Condition,
        ps.StudyId,
        pta.TherapeuticArea
FROM ManuscriptsAndAbstracts pub
LEFT JOIN (SELECT PublicationId, group_concat(Condition) AS Condition FROM PublicationCondition GROUP BY PublicationId) pc on pub.Key = pc.PublicationId
LEFT JOIN (SELECT PublicationId, group_concat(StudyId) AS StudyId FROM PublicationStudy GROUP BY PublicationId) ps on pub.Key = ps.PublicationId
LEFT JOIN (SELECT PublicationId, group_concat(TherapeuticArea) AS TherapeuticArea FROM PublicationTherapeuticArea GROUP BY PublicationId) pta on pub.Key = pta.PublicationId