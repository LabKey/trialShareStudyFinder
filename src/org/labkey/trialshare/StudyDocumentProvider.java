/*
 * Copyright (c) 2016-2017 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.trialshare;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.QueryService;
import org.labkey.api.search.SearchService;
import org.labkey.api.security.User;
import org.labkey.api.util.ConfigurationException;
import org.labkey.api.util.Path;
import org.labkey.api.view.ActionURL;
import org.labkey.api.webdav.SimpleDocumentResource;
import org.labkey.trialshare.query.TrialShareQuerySchema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudyDocumentProvider implements SearchService.DocumentProvider
{
    private static final Logger _logger = Logger.getLogger(StudyDocumentProvider.class);
    public static Container getDocumentContainer()
    {
        return TrialShareManager.get().getCubeContainer(null);
    }

    public static void reindex()
    {
        StudyDocumentProvider dp = new StudyDocumentProvider();
        SearchService ss = SearchService.get();
        ss.deleteResourcesForPrefix("trialShare:study:");
        dp.enumerateDocuments(ss.defaultTask(), getDocumentContainer(), null);
    }


    @Override
    public void enumerateDocuments(SearchService.IndexTask task, @NotNull Container c, Date since)
    {
        try
        {
            Container cubeContainer = TrialShareManager.get().getCubeContainer(c);
            if (c != cubeContainer) // nothing to enumerate if we aren't in the cubeContainer
                return;
        }
        catch (ConfigurationException e) // if the cubeContainer has not been configured, there's nothing to enumerate
        {
            return;
        }

        QuerySchema listSchema = TrialShareQuerySchema.getSchema(User.getSearchUser(), c);

        if (!listSchema.getTableNames().containsAll(TrialShareQuerySchema.getRequiredStudyLists()))
            return;

        String sql =
                "SELECT  "+
                        "sa.StudyId, "+
                        "sa.StudyContainer, "+
                        "sc.condition, "+
                        "sph.Phase,  "+
                        "sag.AgeGroup, "+
                        "sta.TherapeuticArea, "+
                        "sp.shortName, "+
                        "sp.Title, "+
                        "sp.StudyType, "+
                        "sp.Description, "+
                        "sp.Investigator "+
                "FROM StudyAccess sa  "+
                        "   LEFT JOIN StudyProperties sp ON sa.StudyId = sp.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(Condition) As Condition FROM StudyCondition GROUP BY StudyId) sc on sa.StudyId = sc.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(AgeGroup) AS AgeGroup FROM StudyAgeGroup GROUP BY StudyId) sag on sa.StudyId = sag.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(Phase) AS Phase FROM StudyPhase GROUP BY StudyId) sph on sa.StudyId = sph.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(TherapeuticArea) AS TherapeuticArea FROM StudyTherapeuticArea GROUP BY StudyId) sta on sa.StudyId = sta.StudyId ";

        try (ResultSet results = QueryService.get().select(listSchema,sql))
        {
            while (results.next())
            {

                StringBuilder body = new StringBuilder();

                for (String field : new String[]{"Description", "Title"})
                {
                    if (results.getString(field) != null)
                        body.append(results.getString(field).replaceAll(",", " ")).append(" " );
                }

                Map<String, Object> properties = new HashMap<>();

                StringBuilder keywords = new StringBuilder();
                for (String field : new String[]{"AgeGroup", "Condition", "Phase", "TherapeuticArea", "StudyType"})
                {
                    if (results.getString(field) != null)
                        keywords.append(results.getString(field)).append(" ");
                }

                StringBuilder identifiers = new StringBuilder();
                for (String field : new String[]{"shortName", "StudyId", "Investigator"})
                {
                    if (results.getString(field) != null)
                        identifiers.append(results.getString(field)).append(" ");
                }

                properties.put(SearchService.PROPERTY.identifiersMed.toString(), identifiers.toString());
                properties.put(SearchService.PROPERTY.keywordsMed.toString(), keywords.toString());
                properties.put(SearchService.PROPERTY.title.toString(), results.getString("Title"));
                properties.put(SearchService.PROPERTY.categories.toString(), TrialShareModule.searchCategoryStudy.getName());

                String containerId = results.getString("StudyContainer") == null ? c.getId() : results.getString("StudyContainer");

                ActionURL url = new ActionURL(TrialShareController.StudyDetailAction.class, c).addParameter("studyId", results.getString("StudyId")).addParameter("detailType", "study");
                url.setExtraPath(containerId);

                SimpleDocumentResource resource = new SimpleDocumentResource (
                                Path.parse("/" + results.getString("StudyId")),
                                "trialShare:study:" + containerId + ":" + results.getString("StudyId"),
                                containerId,
                                "text/html",
                                body.toString(),
                                url,
                                properties
                        );
                task.addResource(resource, SearchService.PRIORITY.item);
            }
        }
        catch (SQLException e)
        {
           _logger.error("Problem executing query for study indexing", e);
        }
    }

    @Override
    public void indexDeleted() throws SQLException
    {
        // we currently do not support the LastIndexed setting
    }
}
