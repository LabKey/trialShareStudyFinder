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
package org.labkey.trialshare.data;

import org.apache.commons.lang3.StringUtils;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.reports.Report;
import org.labkey.api.reports.ReportService;
import org.labkey.api.reports.model.ViewCategory;
import org.labkey.api.reports.report.view.ReportUtil;
import org.labkey.api.security.User;
import org.labkey.api.util.URLHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyPublicationBean
{
    private static final int AUTHORS_PER_ABBREV = 3;

    // common fields
    private Integer id;
    private String studyId;
    private String pmid; // PubMed Id
    private String pmcid; // PubMed Central reference number
    private String doi;
    private String author;
    private String issue;
    private String journal;
    private String pages;
    private String title;
    private String year;
    private String abstractText;
    private String status;
    private String dataUrl;
    private List<StudyBean> studies;
    private Boolean isHighlighted = false;
    private String publicationType;
    private List<URLData> thumbnails;
    private String manuscriptContainer;

    public static class URLData
    {
        Integer _index;
        String _link;
        String _linkText;

        public Integer getIndex()
        {
            return _index;
        }

        public void setIndex(Integer index)
        {
            _index = index;
        }

        public String getLink()
        {
            return _link;
        }

        public void setLink(String link)
        {
            _link = link;
        }

        public String getLinkText()
        {
            return _linkText;
        }

        public void setLinkText(String linkText)
        {
            _linkText = linkText;
        }
    }

    private List<URLData> urls = new ArrayList<>();

    private String citation;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setKey(Integer id)
    {
        this.id = id;
    }

    public void set_Key(Integer id)
    {
        this.id = id;
    }

    public String getStudyId()
    {
        return studyId;
    }

    public void setStudyId(String studyId)
    {
        this.studyId = studyId;
    }

    public String getPmid()
    {
        return pmid;
    }

    public void setPmid(String pmid)
    {
        this.pmid = pmid;
    }

    public String getPmcid()
    {
        return pmcid;
    }

    public void setPmcid(String pmcid)
    {
        this.pmcid = pmcid;
    }

    public String getDoi()
    {
        return doi;
    }

    public void setDoi(String doi)
    {
        this.doi = doi;
    }

    public String getAuthor()
    {
        return author;
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
        this.author = author;
    }

    public String getIssue()
    {
        return issue;
    }

    public void setIssue(String issue)
    {
        this.issue = issue;
    }

    public String getJournal()
    {
        return journal;
    }

    public void setJournal(String journal)
    {
        this.journal = journal;
    }

    public String getPages()
    {
        return pages;
    }

    public void setPages(String pages)
    {
        this.pages = pages;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getCitation()
    {
        return citation;
    }

    public void setCitation(String citation)
    {
        this.citation = citation;
    }

    public String getAbstractText()
    {
        return abstractText;
    }

    public void setAbstractText(String abstractText)
    {
        this.abstractText = abstractText;
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
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
        return dataUrl;
    }

    public void setDataUrl(String dataUrl)
    {
        this.dataUrl = dataUrl;
    }

    public Boolean getIsHighlighted()
    {
        return isHighlighted;
    }

    public void setIsHighlighted(Boolean highlighted)
    {
        isHighlighted = highlighted;
    }

    public String getPublicationType()
    {
        return publicationType;
    }

    public void setPublicationType(String publicationType)
    {
        this.publicationType = publicationType;
    }

    public String getManuscriptContainer()
    {
        return manuscriptContainer;
    }

    public void setManuscriptContainer(String manuscriptContainer)
    {
        this.manuscriptContainer = manuscriptContainer;
    }

    public List<URLData> getThumbnails()
    {
        return thumbnails;
    }

    public void setThumbnails(User user)
    {
        if (getManuscriptContainer() == null)
            return;
        thumbnails = new ArrayList<>();
        Container container = ContainerManager.getForId(getManuscriptContainer());
        if (container == null)
            return;
        for (Report report : ReportService.get().getReports(user, container))
        {
            ViewCategory category = report.getDescriptor().getCategory();

            if (category != null && category.getLabel().contains("Manuscript Figures"))
            {
                URLHelper urlHelper = ReportUtil.getThumbnailUrl(container, report);
                if (urlHelper != null)
                {
                    URLData urlData = new URLData();
                    urlData.setLinkText(urlHelper.toString());
                    urlData.setLink(container.getStartURL(user).addParameter("pageId","study.DATA_ANALYSIS").toString());
                    thumbnails.add(urlData);
                }
            }
        }
    }
}
