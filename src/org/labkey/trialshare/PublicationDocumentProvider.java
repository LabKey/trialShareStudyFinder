/*
 * Copyright (c) 2015 LabKey Corporation
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

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.QueryService;
import org.labkey.api.search.SearchService;
import org.labkey.api.security.User;
import org.labkey.api.services.ServiceRegistry;
import org.labkey.api.util.Path;
import org.labkey.api.util.StringUtilsLabKey;
import org.labkey.api.view.ActionURL;
import org.labkey.api.webdav.SimpleDocumentResource;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicationDocumentProvider implements SearchService.DocumentProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(PublicationDocumentProvider.class);

    public static Container getDocumentContainer()
    {
        Module trialShareModule = ModuleLoader.getInstance().getModule(TrialShareModule.NAME);
        return ((TrialShareModule) trialShareModule).getCubeContainer(null);
    }

    public static void reindex()
    {
        PublicationDocumentProvider dp = new PublicationDocumentProvider();
        SearchService ss = ServiceRegistry.get(SearchService.class);
        ss.deleteResourcesForPrefix("trialShare:publication:");
        dp.enumerateDocuments(ss.defaultTask(), getDocumentContainer(), null);
    }


    @Override
    public void enumerateDocuments(SearchService.IndexTask task, @NotNull Container c, Date since)
    {

        QuerySchema listSchema = TrialShareQuerySchema.getSchema(User.getSearchUser(), c);

        String sql =
                "SELECT  " +
                    "pub.Key as PublicationId, " +
                    "pub.Title,  " +
                    "pub.Author,  " +
                    "pub.Citation, " +
                    "pub.DOI,  " +
                    "pub.PMID,  " +
                    "pub.PMCID,  " +
                    "pub.PublicationType,  " +
                    "pub.Year,  " +
                    "pub.Journal,  " +
                    "pub.Status,  " +
                    "pub.SubmissionStatus, " +
                    "pub.Study as PrimaryStudy,  " +
                    "pub.StudyId as PrimaryStudyId,  " +
                    "pub.AbstractText,  " +
                    "pub.Keywords,  " +
                    "pub.PermissionsContainer,  " +
                    "pub.ManuscriptContainer,  " +
                    "pc.Condition,  " +
                    "ps.ShortName as StudyShortName,  " +
                    "ps.StudyId,  " +
                    "pta.TherapeuticArea  " +
                "FROM ManuscriptsAndAbstracts pub  " +
                    "   LEFT JOIN (SELECT PublicationId, group_concat(Condition) AS Condition FROM PublicationCondition GROUP BY PublicationId) pc on pub.Key = pc.PublicationId  " +
                    "   LEFT JOIN (SELECT PublicationId, ShortName, group_concat(StudyId) AS StudyId FROM PublicationStudy GROUP BY ShortName, PublicationId) ps on pub.Key = ps.PublicationId  " +
                    "   LEFT JOIN (SELECT PublicationId, group_concat(TherapeuticArea) AS TherapeuticArea FROM PublicationTherapeuticArea GROUP BY PublicationId) pta on pub.Key = pta.PublicationId  " +
                "WHERE pub.Show = true ";

        try (ResultSet results = QueryService.get().select(listSchema,sql))
        {
            while (results.next())
            {

                StringBuilder body = new StringBuilder();

                for (String field : new String[]{"AbstractText", "Citation", "Keywords"})
                {
                    if (results.getString(field) != null)
                        body.append(results.getString(field).replaceAll(",", " ")).append(" ");
                }
                Map<String, Object> properties = new HashMap<>();

                StringBuilder keywords = new StringBuilder();
                // See #26028: identifiers that have punctuation in them (e.g., DOI) are not indexed well as identifiers, so we use keywords instead
                for (String field : new String[]{"Author", "Year", "Status", "PrimaryStudy", "Title", "SubmissionStatus", "PublicationType", "Journal", "TherapeuticArea", "StudyShortName", "Condition", "DOI"})
                {
                    if (results.getString(field) != null)
                        keywords.append(results.getString(field)).append(" ");
                }
                StringBuilder identifiers = new StringBuilder();
                for (String field : new String[]{"PMID", "PMCID", "StudyId", "PrimaryStudyId"})
                {
                    if (results.getString(field) != null)
                        identifiers.append(results.getString(field)).append(" " );
                }
                properties.put(SearchService.PROPERTY.indentifiersMed.toString(), identifiers.toString());
                properties.put(SearchService.PROPERTY.keywordsMed.toString(), keywords.toString());
                properties.put(SearchService.PROPERTY.title.toString(), results.getString("Title"));
                properties.put(SearchService.PROPERTY.categories.toString(), TrialShareModule.searchCategoryPublication.getName());

                String containerId;
                if (results.getString("PermissionsContainer") != null)
                    containerId = results.getString("PermissionsContainer");
                else if (results.getString("ManuscriptContainer") != null)
                    containerId = results.getString("ManuscriptContainer");
                else
                    containerId = ContainerManager.getHomeContainer().getId();

                ActionURL url = new ActionURL(TrialShareController.PublicationDetailsAction.class, c).addParameter("id", results.getString("PublicationId"));
                url.setExtraPath(containerId);

                SimpleDocumentResource resource = new SimpleDocumentResource (
                                Path.parse("/" + results.getString("PublicationId")),
                                "trialShare:publication:" + containerId + ":" + results.getString("PublicationId"),
                                containerId,
                                "text/html",
                                body.toString().getBytes(StringUtilsLabKey.DEFAULT_CHARSET),
                                url,
                                properties
                        );
                task.addResource(resource, SearchService.PRIORITY.item);
            }
        }
        catch (SQLException e)
        {
           _logger.error("Problem executing query for publication indexing", e);
        }
    }

    @Override
    public void indexDeleted() throws SQLException
    {
        // we currently do not support the LastIndexed setting
    }
}
