/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.popup;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.util.ListBoxValues;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Dependent
public class ActivityDataIOEditor implements ActivityDataIOEditorView.Presenter {

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Assignments data.
     */
    public interface GetDataCallback {
        void getData(String assignmentData);
    }
    GetDataCallback callback = null;

    @Inject
    ActivityDataIOEditorView view;

    private List<String> dataTypes = new ArrayList<String>();

    private List<String> dataTypeDisplayNames = new ArrayList<String>();

    private AssignmentData assignmentData;

    @PostConstruct
    public void init(){
        view.init(this);
    }

    public void setCallback(GetDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void handleSaveClick() {

        if ( callback != null ) {
            AssignmentData data = new AssignmentData( view.getInputAssignmentData(),
                    view.getOutputAssignmentData(), dataTypes, dataTypeDisplayNames );
            String sData = Marshalling.toJSON(data);
            callback.getData( sData );
        }
        view.hideView();
    }

    @Override
    public void handleCancelClick() {
        view.hideView();
    }

    public void setDataTypes(List<String> dataTypes, List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        view.setPossibleInputAssignmentsDataTypes(dataTypes, dataTypeDisplayNames);
        view.setPossibleOutputAssignmentsDataTypes(dataTypes, dataTypeDisplayNames);
    }

    public void setAssignmentData(AssignmentData assignmentData) {
        this.assignmentData = assignmentData;
    }

    public void configureDialog(String taskName, boolean hasInputVars, boolean isSingleInputVar, boolean hasOutputVars, boolean isSingleOutputVar) {
        if (taskName != null && !taskName.isEmpty()) {
            view.setCustomViewTitle(taskName);
        }
        else {
            view.setDefaultViewTitle();
        }

        view.setInputAssignmentsVisibility(hasInputVars);
        view.setOutputAssignmentsVisibility(hasOutputVars);
        view.setIsInputAssignmentSingleVar(isSingleInputVar);
        view.setIsOutputAssignmentSingleVar(isSingleOutputVar);
    }

    public void setDisallowedPropertyNames(List<String> disallowedPropertyNames) {
        Set<String> propertyNames = new HashSet<String>();
        if (disallowedPropertyNames != null) {
            for (String name : disallowedPropertyNames) {
                propertyNames.add(name.toLowerCase());
            }
        }
        view.setInputAssignmentsDisallowedNames(propertyNames);
    }

    public void setProcessVariables(List<String> processVariables) {
        view.setInputAssignmentsProcessVariables(processVariables);
        view.setOutputAssignmentsProcessVariables(processVariables);
    }

    public void setInputAssignmentRows(List<AssignmentRow> inputAssignmentRows){
        view.setInputAssignmentRows(inputAssignmentRows);
    }

    public void setOutputAssignmentRows(List<AssignmentRow> outputAssignmentRows){
        view.setOutputAssignmentRows(outputAssignmentRows);
    }

    public void show() {
        view.showView();
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                if (assignmentData != null) {
                    return assignmentData.getDataTypeDisplayNameForUserString(userValue);
                }
                else {
                    return null;
                }
            }
        };
    }

    @Override
    public ListBoxValues.ValueTester processVarTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                return null;
            }
        };
    }
}