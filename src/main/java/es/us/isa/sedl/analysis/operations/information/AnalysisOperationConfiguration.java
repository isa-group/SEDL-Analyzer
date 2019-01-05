package es.us.isa.sedl.analysis.operations.information;

import java.util.ArrayList;
import java.util.List;


import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.analysis.datasetspecification.Filter;
import es.us.isa.sedl.core.analysis.datasetspecification.GroupingProjection;
import es.us.isa.sedl.core.analysis.datasetspecification.Projection;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;

public class AnalysisOperationConfiguration extends AbstractAnalysisOperation<Object, Object>{
    private String function;
    private List<Filter> filters;
    private List<Projection> proj;
    private List<GroupingProjection> grouping;
    private List<List<String>> variables;
    private List<List<Double>> resultsOperation; //resultados asociados a unas variables
    private List<List<String>> dataset;
    private List<String> functionParam;
    private List<ValidationError<ControlledExperiment>> errors;
    public void setErrors(List<ValidationError<ControlledExperiment>> errors) {
        this.errors = errors;
    }
    public AnalysisOperationConfiguration(){
    	super("","");
        filters = new ArrayList<Filter>();
        proj = new ArrayList<Projection>();
        grouping = new ArrayList<GroupingProjection>();
        variables = new ArrayList<List<String>>();
        resultsOperation = new ArrayList<List<Double>>();
        dataset = new ArrayList<List<String>>();
        functionParam = new ArrayList<String>();
        errors = new ArrayList<ValidationError<ControlledExperiment>>();
    }
    public String getFunction() {
        return function;
    }
    public void setFunction(String function) {
        this.function = function;
    }
    public List<Filter> getFilters() {
        return filters;
    }
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
    public List<Projection> getProj() {
        return proj;
    }
    public void setProj(List<Projection> proj) {
        this.proj = proj;
    }
    public List<GroupingProjection> getGrouping() {
        return grouping;
    }
    public void setGrouping(List<GroupingProjection> grouping) {
        this.grouping = grouping;
    }
    public List<List<String>> getVariables() {
        return variables;
    }
    public void setVariables(List<List<String>> variables) {
        this.variables = variables;
    }
    public List<List<Double>> getResultsOperation() {
        return resultsOperation;
    }
    public void setResultsOperation(List<List<Double>> resultsOpperation) {
        this.resultsOperation = resultsOpperation;
    }
    public List<List<String>> getDataset() {
        return dataset;
    }
    public void setDataset(List<List<String>> dataset) {
        this.dataset = dataset;
    }
    public List<String> getFunctionParam() {
        return functionParam;
    }
    public void setFunctionParam(List<String> functionParam) {
        this.functionParam = functionParam;
    }
    public List<ValidationError<ControlledExperiment>> getErrors() {
        return errors;
    }
    public String toString() {
        return "Operation [function=" + function + ", filters=" + filters
                + ", proj=" + proj + ", grouping=" + grouping
                + ", variables=" + variables + ", resultsOperation="
                + resultsOperation + ", dataset=" + dataset
                + ", functionParam=" + functionParam + "]";
    }
	@Override
	public Object apply(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
