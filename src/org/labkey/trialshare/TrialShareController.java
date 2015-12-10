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

import org.labkey.api.action.ApiAction;
import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;
import org.labkey.api.action.ReturnUrlForm;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.gwt.client.util.StringUtils;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.view.HtmlView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.api.view.NotFoundException;
import org.labkey.api.view.VBox;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyFacetBean;
import org.labkey.trialshare.data.StudyFacetMember;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Marshal(Marshaller.Jackson)
public class TrialShareController extends SpringActionController
{
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(TrialShareController.class);
    public static final String NAME = "trialshare";

    public TrialShareController()
    {
        setActionResolver(_actionResolver);
    }

    @RequiresPermission(ReadPermission.class)
    public class BeginAction extends SimpleViewAction
    {
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }

        @Override
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            return new JspView("/org/labkey/trialshare/view/dataFinder.jsp");
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class GetStudiesAction extends ApiAction<StudiesForm>
    {

        @Override
        public Object execute(StudiesForm form, BindException errors) throws Exception
        {
            List<StudyBean> studies = new ArrayList<StudyBean>();
            StudyBean study = new StudyBean();

            studies.add(getStudy("ABC123"));
            studies.add(getStudy("XY1YVA"));

            return success(studies);
        }
    }

    public static class StudiesForm {

    }

    private StudyBean getStudy(String accession) {
        StudyBean study = new StudyBean();
        if (accession.equals("ABC123")) {
            study.setAccession("ABC123");
            study.setInvestigator("Some One");
            study.setTitle("The title of this study is very descriptive");
            study.setIsLoaded(true);
            study.setUrl("/labkey/project/ITN%20TrialShare/ABC123/begin.view?");
        } else if (accession.equals("XY1YVA")) {
            study.setAccession("XY1YVA");
            study.setInvestigator("Some One Else");
            study.setTitle("The title of this study is even more descriptive");
            study.setIsLoaded(false);
            study.setDescription("Immunosuppression Withdrawal for Pediatric Living-donor Liver Transplant Recipients\n" +
                    "\n" +
                    "Protocol Chair: Sandy Feng, MD, PhD\n" +
                    "\n" +
                    "This is a prospective multicenter, open-label, single-arm trial in which 20 pediatric recipients of parental living-donor liver allografts will undergo gradual withdrawal of immunosuppression with the goal of complete withdrawal. Patients on stable immunosuppression regimens with good organ function and no evidence of acute or chronic rejection or other forms of allograft dysfunction will be enrolled. Participants will undergo gradual withdrawal of immunosuppression and will be followed for a minimum of 4 years after completion of immunosuppression withdrawal. Immunologic and genetic profiles will be collected at multiple time points and compared between tolerant and nontolerant participants.\n" +
                    "\n" +
                    "Cohort\n" +
                    "Description\n" +
                    "Immunosuppression Withdrawal\tPediatric recipients of parental living-donor liver allografts\n" +
                    "ClinTrials.gov #: NCT00320606");
        }
        return study;
    }

    @RequiresPermission(ReadPermission.class)
    public class GetStudyFacetsAction extends ApiAction
    {

        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            List<StudyFacetBean> facets = new ArrayList<StudyFacetBean>();
            StudyFacetBean facet = new StudyFacetBean();
            facet.setName("Facet1");
            facet.setCaption("Facet 1");

            List<StudyFacetMember> members = new ArrayList<StudyFacetMember>();
            StudyFacetMember member = new StudyFacetMember();
            member.setName("Member 1.1");
            member.setCount(10000);
            member.setUniqueName("Member11");
            members.add(member);


            member = new StudyFacetMember();
            member.setName("Member 1.2");
            members.add(member);
            member.setCount(0);
            member.setUniqueName("Member12");
            facet.setMembers(members);


            facets.add(facet);

            StudyFacetBean facet2 = new StudyFacetBean();
            facet2.setName("Facet2");
            facet2.setCaption("Facet 2");

            members = new ArrayList<StudyFacetMember>();
            member = new StudyFacetMember();
            member.setName("Member 2.1");
            member.setCount(12);
            member.setUniqueName("Member21");
            members.add(member);
            facet2.setMembers(members);

            facets.add(facet2);


            return success(facets);
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudyDetailAction extends SimpleViewAction<StudyIdForm>
    {
        StudyIdForm _form;
        StudyBean _study = new StudyBean();

        @Override
        public ModelAndView getView(StudyIdForm form, BindException errors) throws Exception
        {
            _form = form;

            String studyId = (null==form) ? null : form.getStudyId();
            if (StringUtils.isEmpty(studyId))
                throw new NotFoundException("study not specified");

//            _study.study = (new TableSelector(DbSchema.get("immport").getTable("study"))).getObject(studyId, StudyBean.class);
//            if (null == _study.study)
//                throw new NotFoundException("study not found: " + form.getStudy());
//            SimpleFilter filter = new SimpleFilter();
//            filter.addCondition(new FieldKey(null,"study_accession"),studyId);
//            _study.personnel = (new TableSelector(DbSchema.get("immport").getTable("study_personnel"),filter,null)).getArrayList(StudyPersonnelBean.class);
//            _study.pubmed = (new TableSelector(DbSchema.get("immport").getTable("study_pubmed"),filter,null)).getArrayList(StudyPubmedBean.class);

            _study = getStudy(studyId);
            VBox v = new VBox();
            if (null != _form.getReturnActionURL())
            {
                v.addView(new HtmlView(PageFlowUtil.textLink("back",_form.getReturnActionURL()) + "<br>"));
            }
            v.addView(new JspView<StudyBean>("/org/labkey/trialshare/view/studyDetail.jsp", _study));
            return v;
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    public static class StudyIdForm extends ReturnUrlForm
    {
        private String studyId;

        public StudyIdForm(){}

        public String getStudyId()
        {
            return studyId;
        }

        public void setStudyId(String studyId)
        {
            this.studyId = studyId;
        }
    }


}