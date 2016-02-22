<%
/*
 * Copyright (c) 2016 LabKey Corporation
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
%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.labkey.api.data.Container" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.ViewContext" %>
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="org.labkey.trialshare.data.StudyBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPublicationBean" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.Map" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();

        resources.add(ClientDependency.fromPath("study/Finder/dataFinder.css"));
        resources.add(ClientDependency.fromPath("study/Finder/trialShare.css"));

        return resources;
    }
%>
<%
    JspView<StudyPublicationBean> me = (JspView) HttpView.currentView();

    ViewContext context = HttpView.currentContext();
    Container c = context.getContainer();
    StudyPublicationBean publication = me.getModelBean();

    Map<String, String> linkProps = new HashMap<>();
    linkProps.put("target", "_blank");
%>

<div id="publicationDetails" class="labkey-publication-details">
<div id="labkey-publication-details-content">
    <h3 class="labkey-publication-title"><%=h(publication.getTitle())%></h3>
    <div>
        <%
        if (!StringUtils.isEmpty(publication.getAuthor()))
        {
        %><div class="labkey-publication-author"><%=h(publication.getAuthor())%></div><%
        }
        %>
        <%
        if (!StringUtils.isEmpty(publication.getCitation()))
        {
        %><div class="labkey-publication-citation"><%=h(publication.getCitation())%></div><%
        }
        %>

        <div class="labkey-publication-identifiers">
        <%
            if (!StringUtils.isEmpty(publication.getPmid()))
            {
        %><span class="labkey-publication-identifier">PMID: <%=textLink(publication.getPmid(),"http://www.ncbi.nlm.nih.gov/pubmed/?term=" + publication.getPmid(), null, null, linkProps)%></span><%
            }
        %>
        <%
            if (!StringUtils.isEmpty(publication.getPmcid()))
            {
        %><span class="labkey-publication-identifier">PMCID: <%=textLink(publication.getPmcid(),"http://www.ncbi.nlm.nih.gov/pmc/articles/" + publication.getPmcid(), null, null, linkProps)%></span><%
            }
        %>
        <%
            if (!StringUtils.isEmpty(publication.getDoi()))
            {
        %><span class="labkey-publication-identifier">DOI: <%=textLink(publication.getDoi(), "http://dx.doi.org/" + publication.getDoi(), null, null, linkProps)%></span><%
            }
        %>
        </div>
        <%

            for (StudyPublicationBean.URLData urlData : publication.getUrls())
            {
                if (urlData != null && !StringUtils.isEmpty(urlData.getLinkText()))
                {
                %><br/><%=textLink(h(urlData.getLinkText()), urlData.getLink(), null, null, linkProps)%><%
                }
            }
        %>
        <%
            if (!publication.getStudies().isEmpty())
            {
        %>
        <div>
            <span class="labkey-publication-detail-label">Studies</span>
        <%
                for (StudyBean study : publication.getStudies())
                {
                    if (study.getUrl(getContainer(), getUser()) != null)
                    {
        %><span class="labkey-study-short-name"><a href="<%=h(study.getUrl())%>"%><%=h(study.getShortName())%></a></span>
        <%
                    }
                    else
                    {
        %><span class="labkey-study-short-name"><%=h(study.getShortName())%></span>
        <%
                    }
                }
            }
        %>
        </div>
        <%
            if (!StringUtils.isEmpty(publication.getAbstractText()))
            {
        %><div class="labkey-publication-detail-label">Abstract</div>
        <div class="labkey-publication-abstract"><%=h(publication.getAbstractText())%></div><%
        }
        %>
    </div>
</div>
</div>


