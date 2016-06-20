/*
 * Copyright (c) 2015-2016 LabKey Corporation
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
package org.labkey.trialshare.data;

import org.apache.commons.lang3.StringUtils;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DbScope;
import org.labkey.api.reports.Report;
import org.labkey.api.reports.ReportService;
import org.labkey.api.reports.model.ViewCategory;
import org.labkey.api.reports.report.view.ReportUtil;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.URLHelper;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyPublicationBean
{
    private static final String KEY_FIELD = "Key";
    private static final String STUDY_ID_FIELD = "StudyId";
    private static final String TITLE_FIELD = "Title";
    private static final String AUTHOR_FIELD = "Author";
    private static final String DOI_FIELD = "DOI";
    private static final String PMID_FIELD = "PMID";
    private static final String PMCID_FIELD = "PMCID";
    private static final String ISSUE_NUMBER_FIELD = "IssueNo";
    private static final String PAGES_FIELD = "Pages";
    private static final String PUBLICATION_TYPE_FIELD = "PublicationType"; // TODO convert to lookup
    private static final String YEAR_FIELD = "Year";
    private static final String JOURNAL_FIELD = "Journal";
    private static final String STATUS_FIELD = "Status";
    private static final String CITATION_FIELD = "Citation";
    private static final String ABSTRACT_FIELD = "Abstract";
    private static final String DATA_URL_FIELD = "DataUrl";
    private static final String IS_HIGHLIGHTED_FIELD = "IsHighlighted";
    private static final String MANUSCRIPT_CONTAINER_FIELD = "ManuscriptContaienr";
    private static final String KEYWORDS_FIELD = "Keywords";
    private static final String PERMISSIONS_CONTAINER_FIELD = "PermissionsContainer";
    private static final String IS_SHOWN_FIELD = "Show";

    public static final String FIGURES_CATEGORY_TEXT = "Manuscript Figures";

    private static final int AUTHORS_PER_ABBREV = 3;
    private Map<String, Object> _primaryFields = new HashMap<>();

    private List<StudyBean> studies;
    private List<URLData> thumbnails;
    private List<URLData> urls = new ArrayList<>();
    private Map<String, String> studyIds = new HashMap<>();
    private List<String> conditions = new ArrayList<>();
    private List<String> therapeuticAreas = new ArrayList<>();



    public Integer getId()
    {
        return (Integer) _primaryFields.get(KEY_FIELD);
    }

    public void setId(Integer id)
    {
        _primaryFields.put(KEY_FIELD, id);
    }

    public void setKey(Integer id)
    {
        _primaryFields.put(KEY_FIELD, id);
    }

    public void set_Key(Integer id) { _primaryFields.put(KEY_FIELD, id); } // this is required because for SQLServer we alias "Key" as "_Key" in our query

    public String getStudyId()
    {
        return (String) _primaryFields.get(STUDY_ID_FIELD);
    }

    public void setStudyId(String studyId)
    {
        _primaryFields.put(STUDY_ID_FIELD, studyId);
    }

    public String getPmid()
    {
        return (String) _primaryFields.get(PMID_FIELD);
    }

    public void setPmid(String pmid)
    {
        _primaryFields.put(PMID_FIELD, pmid);
    }

    public String getPmcid()
    {
        return (String) _primaryFields.get(PMCID_FIELD);
    }

    public void setPmcid(String pmcid)
    {
        _primaryFields.put(PMCID_FIELD, pmcid);
    }

    public String getDoi()
    {
        return (String) _primaryFields.get(DOI_FIELD);
    }

    public void setDoi(String doi)
    {
        _primaryFields.put(DOI_FIELD, doi);
    }

    public String getAuthor()
    {
        return (String) _primaryFields.get(AUTHOR_FIELD);
    }

    public String getAuthorAbbrev()
    {
        if (getAuthor() == null)
            return null;
        String[] authors = getAuthor().split(",");
        StringBuilder authorAbbrev = new StringBuilder();
        int endVal = AUTHORS_PER_ABBREV;
        String suffix = ", et al.";
        if (authors.length <= AUTHORS_PER_ABBREV)
        {
            endVal = authors.length;
            suffix = "";
        }
        return String.join(", ", Arrays.copyOfRange(authors, 0, endVal)) + suffix;
    }

    public void setAuthor(String author)
    {
        _primaryFields.put(AUTHOR_FIELD, author);
    }

    public String getIssueNumber()
    {
        return (String) _primaryFields.get(ISSUE_NUMBER_FIELD);
    }

    public void setIssueNumber(String issue)
    {
        _primaryFields.put(ISSUE_NUMBER_FIELD, issue);
    }

    public String getJournal()
    {
        return (String) _primaryFields.get(JOURNAL_FIELD);
    }

    public void setJournal(String journal)
    {
        _primaryFields.put(JOURNAL_FIELD, journal);
    }

    public String getPages()
    {
        return (String) _primaryFields.get(PAGES_FIELD);
    }

    public void setPages(String pages)
    {
        _primaryFields.put(PAGES_FIELD, pages);
    }

    public String getTitle()
    {
        return (String) _primaryFields.get(TITLE_FIELD);
    }

    public void setTitle(String title)
    {
        _primaryFields.put(TITLE_FIELD, title);
    }

    public String getYear()
    {
       return (String) _primaryFields.get(YEAR_FIELD);
    }

    public void setYear(String year)
    {
        _primaryFields.put(YEAR_FIELD, year);
    }

    public String getCitation()
    {
        return (String) _primaryFields.get(CITATION_FIELD);
    }

    public void setCitation(String citation)
    {
        _primaryFields.put(CITATION_FIELD, citation);
    }

    public String getAbstractText()
    {
        return (String) _primaryFields.get(ABSTRACT_FIELD);
    }

    public void setAbstractText(String abstractText)
    {
        _primaryFields.put(ABSTRACT_FIELD, abstractText);
    }

    public void setDescription1(String description1)
    {
        setUrlText(0, description1);
    }


    public String getUrl() {
        for (URLData urlData : urls)
        {
            if (!StringUtils.isEmpty(urlData.getLink()))
                return urlData.getLink();
        }
        return null;
    }

    public List<URLData> getUrls()
    {
        return urls;
    }

    public void setUrls(List<URLData> urls)
    {
        this.urls = urls;
    }

    private void setUrlText(int index, String description)
    {
        if (description == null || description.equals("&nbsp;"))
            description = "";

        URLData urlData = getUrlData(index);
        if (urlData == null)
        {
            urlData = new URLData();
            urlData.setIndex(index);
            urls.add(urlData);
        }
        urlData.setLinkText(description);
    }

    private void setUrlLink(int index, String link)
    {
        if (link == null)
            return;

        URLData urlData = getUrlData(index);
        if (urlData == null)
        {
            urlData = new URLData();
            urlData.setIndex(index);
            urls.add(urlData);
        }
        urlData.setLink(link);
    }

    private URLData getUrlData(int index)
    {
        for (URLData url : urls)
        {
            if (url.getIndex() == index)
                return url;
        }
        return null;
    }

    public void setDescription2(String description2)
    {
        setUrlText(1, description2);
    }

    public void setDescription3(String description3)
    {
        setUrlText(2, description3);
    }


    public void setLink1(String link1)
    {
        setUrlLink(0, link1);
    }


    public void setLink2(String link2)
    {
        setUrlLink(1, link2);
    }


    public void setLink3(String link3)
    {
        setUrlLink(2, link3);
    }

    public String getStatus()
    {
        return (String) _primaryFields.get(STATUS_FIELD);
    }

    public void setStatus(String status)
    {
        _primaryFields.put(STATUS_FIELD, status);
    }

    public List<StudyBean> getStudies()
    {
        return studies;
    }

    public void setStudies(List<StudyBean> studies)
    {
        this.studies = studies;
    }

    public String getDataUrl()
    {
        return (String) _primaryFields.get(DATA_URL_FIELD);
    }

    public void setDataUrl(String dataUrl)
    {
        _primaryFields.put(DATA_URL_FIELD, dataUrl);
    }

    public Boolean getIsHighlighted()
    {
        return (Boolean) _primaryFields.get(IS_HIGHLIGHTED_FIELD);
    }

    public void setIsHighlighted(Boolean highlighted)
    {
        _primaryFields.put(IS_HIGHLIGHTED_FIELD, highlighted);
    }

    public String getPublicationType()
    {
        return (String) _primaryFields.get(PUBLICATION_TYPE_FIELD);
    }

    public void setPublicationType(String publicationType)
    {
        _primaryFields.put(PUBLICATION_TYPE_FIELD, publicationType);
    }

    public String getManuscriptContainer()
    {
        return (String) _primaryFields.get(MANUSCRIPT_CONTAINER_FIELD);
    }

    public void setManuscriptContainer(String manuscriptContainer)
    {
        _primaryFields.put(MANUSCRIPT_CONTAINER_FIELD, manuscriptContainer);
    }

    public List<URLData> getThumbnails()
    {
        return thumbnails;
    }

    public void setThumbnails(User user, ActionURL actionURL)
    {
        if (getManuscriptContainer() == null)
            return;
        thumbnails = new ArrayList<>();
        Container container = ContainerManager.getForId(getManuscriptContainer());
        if (container == null)
            return;
        ViewContext context = new ViewContext();
        context.setContainer(container);
        context.setUser(user);
        context.setActionURL(actionURL);

        List<ViewCategory> figureCategories = getFigureReportCategories(user, container);

        for (Report report : ReportService.get().getReports(user, container))
        {
            ViewCategory category = report.getDescriptor().getCategory();

            if (figureCategories.contains(category))
            {
                URLHelper urlHelper = ReportUtil.getThumbnailUrl(container, report);

                if (urlHelper != null)
                {
                    URLData urlData = new URLData();
                    urlData.setLinkText(urlHelper.toString());
                    urlData.setLink(report.getRunReportURL(context).toString());
                    urlData.setTitle(report.getDescriptor().getReportName());
                    thumbnails.add(urlData);
                }
            }
        }
    }

    public List<ViewCategory> getFigureReportCategories(User user, Container container)
    {
        List<ViewCategory> categories = new ArrayList<>();

        for (Report report : ReportService.get().getReports(user, container))
        {
            ViewCategory category = report.getDescriptor().getCategory();

            if (category != null && category.getLabel().contains(FIGURES_CATEGORY_TEXT))
            {
                addCategories(category, categories);
                break;
            }
        }
        return categories;
    }

    public void addCategories(ViewCategory category, List<ViewCategory> categories)
    {
        categories.add(category);
        for (ViewCategory subcategory : category.getSubcategories())
        {
            addCategories(subcategory, categories);
        }
    }

    public String getKeywords()
    {
        return (String) _primaryFields.get(KEYWORDS_FIELD);
    }

    public void setKeywords(String keywords)
    {
        _primaryFields.put(KEYWORDS_FIELD, keywords);
    }

    public String getPermissionsContainer()
    {
        return (String) _primaryFields.get(PERMISSIONS_CONTAINER_FIELD);
    }

    public void setPermissionsContainer(String permissionsContainer)
    {
        _primaryFields.put(PERMISSIONS_CONTAINER_FIELD, permissionsContainer);
    }

    public Boolean getShow()
    {
        return (Boolean) _primaryFields.get(IS_SHOWN_FIELD);
    }

    public void setShow(Boolean show)
    {
        _primaryFields.put(IS_SHOWN_FIELD, show);
    }

    public List<String> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<String> conditions)
    {
        this.conditions = conditions;
    }

    public Map<String, String> getStudyIds()
    {
        return studyIds;
    }

    public void setStudyIds(Map<String, String> studyIds)
    {
        this.studyIds = studyIds;
    }

    public List<String> getTherapeuticAreas()
    {
        return therapeuticAreas;
    }

    public void setTherapeuticAreas(List<String> therapeuticAreas)
    {
        this.therapeuticAreas = therapeuticAreas;
    }

    public Boolean inProgress()
    {
        return getStatus().equalsIgnoreCase(TrialShareQuerySchema.IN_PROGRESS_STATUS);
    }

    public String getCubeId()
    {
        return "[Publication].[" + getId() + "]";
    }

    public boolean hasPermission(User user)
    {
        Boolean inProgress = inProgress();
        if (getPermissionsContainer() == null)
            return !inProgress;
        else
        {
            Container permissionsContainer = ContainerManager.getForId(getPermissionsContainer());
            if (permissionsContainer == null)
                return !inProgress;
            else if (inProgress)
            {
                if (permissionsContainer.hasPermission(user, InsertPermission.class))
                    return true;
            }
            else if (permissionsContainer.hasPermission(user, ReadPermission.class))
                return true;
        }
        return false;
    }

    public Map<String, Object> getPrimaryFields()
    {
       return _primaryFields;
    }


    private void save(User user, Container container, BindException errors)
    {

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {

            transaction.commit();
        }
        catch (Exception e)
        {
            errors.reject("Error saving data", "Error saving publication data: " + e.getMessage());
        }
    }

}
