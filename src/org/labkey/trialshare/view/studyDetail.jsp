<%
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
%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.labkey.api.data.Container" %>
<%@ page import="org.labkey.api.util.Pair" %>
<%@ page import="org.labkey.api.view.ActionURL" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.ViewContext" %>
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="org.labkey.trialshare.data.StudyBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPersonnelBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPublicationBean" %>
<%@ page import="java.net.URL" %>
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
    JspView<TrialShareController.StudyDetailBean> me = (JspView) HttpView.currentView();

    ViewContext context = HttpView.currentContext();
    Container c = context.getContainer();
    TrialShareController.StudyDetailBean studyDetail = me.getModelBean();
    StudyBean study = studyDetail.getStudy();
    String descriptionHTML = study.getDescription(c, context.getUser());
    if (StringUtils.isEmpty(descriptionHTML))
        descriptionHTML = h(study.getBriefDescription());

    String studyUrl = study.getUrl(c, context.getUser());

    String publicationsSectionTitle = "Manuscripts and Abstracts";
    Map<String, String> linkProps = new HashMap<>();
    linkProps.put("target", "_blank");
%>

<div id="studyDetails" class="labkey-study-details">
<h2 class="labkey-study-accession"><% if (null!=studyUrl) {%><a style="color:#fff" href="<%=h(studyUrl)%>"><%}%><%=h(study.getStudyId())%><% if (null!=studyUrl) {%></a><%}%></h2>
<h2 class="labkey-study-short-name"><% if (null!=study.getShortName()) {%><a style="color:#fff" href="<%=h(study.getShortName())%>"><%}%><%=h(study.getShortName())%><% if (null!=study.getShortName()) {%></a><%}%></h2>
<div id="labkey-study-details-content">
<% if (null != study.getIconUrl()) {%><img src="<%=h(study.getIconUrl())%>"/><%}%>
<h3 class="study-title"><%=h(study.getTitle())%></h3>
    <div><%

        if (null != study.getPersonnel())
        {
            for (StudyPersonnelBean p : study.getPersonnel())
            {
                if ("Principal Investigator".equals(p.getRole_in_study()))
                {
                    %><div>
                        <span class="labkey-study-pi"><%=h(p.getHonorific())%> <%=h(p.getFirst_name())%> <%=h(p.getLast_name())%></span>
                        <span class="labkey-study-organization" style="float: right"><%=h(p.getOrganization())%></span>
                    </div><%
                }
            }
        }
        %><%
        if (studyDetail.getDetailType() == TrialShareController.DetailType.study)
        {
        %><div class="labkey-study-description"><%=text(descriptionHTML)%></div>
        <%
        }
        %>
        <div class="labkey-study-papers"><%
        if (null != study.getPublications() && study.getPublications().size() > 0)
        {
            %><span class="labkey-study-publication-header"><%=h(publicationsSectionTitle)%></span><%
            for (StudyPublicationBean pub : study.getPublications())
            {
                if (pub.getTitle() != null)
                {
                %><p><span class="labkey-publication-journal"><%=h(pub.getJournal())%></span> <span class="labkey-publication-year"><%=h(pub.getYear())%></span><br/><%
                %><span class="labkey-publication-title"><%=h(pub.getTitle())%></span><%
                    if (!StringUtils.isEmpty(pub.getCitation()))
                    {
                        %><br/><span class="labkey-publication-citation"><%=h(pub.getCitation())%></span><%
                    }
                %><%
                    if (!StringUtils.isEmpty(pub.getAuthor()))
                    {
                        %><br/><span class="labkey-publication-author"><%=h(pub.getAuthor())%></span><%
                    }
                %><%
                    if (!StringUtils.isEmpty(pub.getPubmedId()))
                    {
                        %><br/><%=textLink("PubMed","http://www.ncbi.nlm.nih.gov/pubmed/?term=" + pub.getPubmedId(), null, null, linkProps)%><%
                    }
                    for (Pair<String, String> urlData : pub.getUrls())
                    {
                        if (urlData != null && !StringUtils.isEmpty(urlData.second))
                        {
                        %><br/><%=textLink(h(urlData.second), urlData.first, null, null, linkProps)%><%
                        }
                    }
                %></p><%
                }
            }
        }
        %></div>
    </div>

    <% if (null != studyUrl) { %>
        <%= textLink("View study " + study.getShortName(), studyUrl, null, null, linkProps)%><br>
    <% } %>
    <%
        if (null != study.getExternalUrl())
        {
            String text = study.getExternalUrlDescription();
            if (StringUtils.isEmpty(text))
            {
                URL url = new URL(study.getExternalUrl());
                text = "View study at " + url.getHost();
            }
    %>
        <%= textLink(text, study.getExternalUrl(), null, null, linkProps)%><br>
    <% } %>
</div>
</div>


