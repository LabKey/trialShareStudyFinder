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
<%@ page import="org.labkey.api.data.ContainerFilter" %>
<%@ page import="org.labkey.api.data.ContainerFilterable" %>
<%@ page import="org.labkey.api.data.ContainerManager" %>
<%@ page import="org.labkey.api.data.TableInfo" %>
<%@ page import="org.labkey.api.data.TableSelector" %>
<%@ page import="org.labkey.api.query.DefaultSchema" %>
<%@ page import="org.labkey.api.query.QuerySchema" %>
<%@ page import="org.labkey.api.view.ActionURL" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.ViewContext" %>
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="org.labkey.trialshare.data.StudyBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPublicationBean" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.Map" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();

        resources.add(ClientDependency.fromPath("dataFinder.css"));
        resources.add(ClientDependency.fromPath("trialShare.css"));

        return resources;
    }
%>
<%
    JspView<StudyBean> me = (JspView) HttpView.currentView();

    ViewContext context = HttpView.currentContext();
    Container c = context.getContainer();
    StudyBean study = me.getModelBean();

    ActionURL studyUrl = null;
    if (!c.isRoot())
    {
        String comma = "\n";
        Container p = c.getProject();
        QuerySchema s = DefaultSchema.get(context.getUser(), p).getSchema("study");
        TableInfo sp = s.getTable("StudyProperties");
        if (sp.supportsContainerFilter())
        {
            ContainerFilter cf = new ContainerFilter.AllInProject(context.getUser());
            ((ContainerFilterable) sp).setContainerFilter(cf);
        }
        Collection<Map<String, Object>> maps = new TableSelector(sp).getMapCollection();
        for (Map<String, Object> map : maps)
        {
            Container studyContainer = ContainerManager.getForId((String) map.get("container"));
            String studyAccession = (String)map.get("study_accession");
            String name = (String)map.get("Label");
            if (null == studyAccession && study.getStudyIdPrefix() != null && name.startsWith(study.getStudyIdPrefix()))
                studyAccession = name;
            if (null != studyContainer && StringUtils.equalsIgnoreCase(study.getStudyId(), studyAccession))
            {
                studyUrl = studyContainer.getStartURL(context.getUser());
                break;
            }
        }
    }

    String publicationsTitle = "Manuscripts and Abstracts";
    Map<String, String> linkProps = new HashMap<>();
    linkProps.put("target", "_blank");
%>

<div id="studyPublicationDetails" class="labkey-study-details">
<h2 class="labkey-study-accession"><% if (null!=studyUrl) {%><a style="color:#fff" href="<%=h(studyUrl)%>"><%}%><%=h(study.getStudyId())%><% if (null!=studyUrl) {%></a><%}%></h2>
<h2 class="labkey-study-short-name"><% if (null!=study.getShortName()) {%><a style="color:#fff" href="<%=h(study.getShortName())%>"><%}%><%=h(study.getShortName())%><% if (null!=study.getShortName()) {%></a><%}%></h2>
<div id="labkey-study-details-content">
<% if (null != study.getIconUrl()) {%><img src="<%=study.getIconUrl()%>"/><%}%>
<h3 class="study-title"><%=h(study.getTitle())%></h3>
    <div>
        <div class="labkey-study-papers"><%
        if (null != study.getPublications() && study.getPublications().size() > 0)
        {
            %><span class="labkey-study-manuscript-header"><%=h(publicationsTitle)%></span><%
            for (StudyPublicationBean pub : study.getPublications())
            {
                if (pub.getTitle() != null)
                {
                %><p><span style="font-size:80%;"><span class="labkey-manuscript-journal" style="text-decoration:underline;"><%=h(pub.getJournal())%></span> <span class="labkey-manuscript-year"><%=h(pub.getYear())%></span></span><br/><%
                %><span class="labkey-manuscript-title"><%=h(pub.getTitle())%></span><%
                    if (!StringUtils.isEmpty(pub.getPubmedId()))
                    {
                        %><br/><%=textLink("PubMed","http://www.ncbi.nlm.nih.gov/pubmed/?term=" + pub.getPubmedId(), null, null, linkProps)%><%
                    }
                %></p><%
                }
            }
        }
        %></div>
    </div>

    <% if (null != studyUrl) { %>
        <%= textLink("View study " + study.getStudyId(), studyUrl.toString(), null, null, linkProps)%><br>
    <% } %>
</div>
</div>


