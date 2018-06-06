package es.us.isa.sedl.analysis.operations.information.computestats;

import java.util.ArrayList;
import java.util.List;


import es.us.isa.jdataset.DataSet;
import es.us.isa.sedl.core.BasicExperiment;
import es.us.isa.sedl.core.analysis.datasetspecification.DatasetSpecification;
import es.us.isa.sedl.core.analysis.datasetspecification.Projection;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;
import es.us.isa.sedl.core.util.Error.ERROR_SEVERITY;
import es.us.isa.sedl.runtime.analysis.AnalysisOperation;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;

public class StatisticalAnalysisOperation implements AnalysisOperation<DataSet, List<StatisticalAnalysisResult>> {

    private Statistic statistic;
    private List<StatisticalAnalysisResult> result;

    private DataSet dataset;
    private List<ValidationError> errors;
    private StatisticsComputingEngine statisticsComputingEngine;
    private StatisticalResultsRenderer<String> resultsRenderer;

    public StatisticalAnalysisOperation(StatisticsComputingEngine statisticsComputingEngine, StatisticalResultsRenderer<String> resultsRenderer) {        
        errors = new ArrayList<ValidationError>();
        this.statisticsComputingEngine = statisticsComputingEngine;
        this.resultsRenderer=resultsRenderer;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public String toString() {
        return "Operation [statisitc=(" + getStatistic() + "),dataset=(" + dataset + "), result=(" + getResults() + ")]";
    }

    /**
     * @return the statisticsComputingEngine
     */
    public StatisticsComputingEngine getStatisticsComputingEngine() {
        return statisticsComputingEngine;
    }

    /**
     * @param statisticsComputingEngine the statisticsComputingEngine to set
     */
    public void setStatisticsComputingEngine(StatisticsComputingEngine statisticsComputingEngine) {
        this.statisticsComputingEngine = statisticsComputingEngine;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<StatisticalAnalysisResult> apply(DataSet dataset){
        return apply(dataset,null);
    }
    
    public List<StatisticalAnalysisResult> apply(DataSet dataset,BasicExperiment experiment){
        
        if(this.statistic.getDatasetSpecification()!=null){
            validateDatasetSpecfication(dataset,this.statistic.getDatasetSpecification());
            this.dataset = this.statistic.getDatasetSpecification().apply(dataset,experiment);
        }
        try {
            if(this.errors.isEmpty())
                this.setResults(statisticsComputingEngine.compute(this));
        } catch (UnsupportedStatisticException ex) {
            this.result=null;
            this.errors.add(new ValidationError(this,ERROR_SEVERITY.ERROR,
                    "The statistics computing engine does not support statistic:"
                    + " '" + getStatistic() + "' (" + ex.getMessage() + ")"));
        }

        return result;
    }

    @Override
    public String getName() {
        return getStatistic().toString();
    }

    @Override
    public String getDescription() {
        return "Compute the following statistic on the dataset provided as input: " + getStatistic();
    }

    /**
     * @return the statistic
     */
    public Statistic getStatistic() {
        return statistic;
    }

    /**
     * @param statistic the statistic to set
     */
    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    /**
     * @return the result
     */
    public List<StatisticalAnalysisResult> getResults() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResults(List<StatisticalAnalysisResult> result) {
        this.result = result;
    }

    public void generateDataset(BasicExperiment experiment) {	        
        this.dataset = statistic.getDatasetSpecification().apply(dataset);
    }

    
    /*
    private void calculateTypeOnlyProjection(List<List<String>> dataset, BasicExperiment experiment) {
        List<String> params = new ArrayList<String>();
        String param = "Filter( ";
        for (Projection p : operation.getProj()) {
            List<String> projVar = p.getProjectedVariables();
            for (String var : projVar) {
                param = param + var + " ";
                String varName = var;
                int valueIndex = 2;
                int labelIndex = 1;
                if (operation.getVariables().get(0).contains(varName)) {
                    for (String aux : operation.getVariables().get(0)) {
                        if (aux.equals(varName)) {
                            labelIndex = operation.getVariables().get(0).indexOf(aux);
                        }
                        if (aux.equals(outcomes.get(0).getName())) {
                            valueIndex = operation.getVariables().get(0).indexOf(aux);
                        }
                    }
                } else {
                    ValidationError<BasicExperiment> vs = new ValidationError<BasicExperiment>(experiment, Error.ERROR_SEVERITY.ERROR, "[ERROR] Variable not found: " + varName);
                    operation.getErrors().add(vs);
                    System.out.println("[ERROR] Variable name: " + varName);
                }
                List<String> labels = new ArrayList<String>();
                List<Variable> expVariables = experiment.getDesign().getVariables().getVariable();
                for (Variable v : expVariables) {//replantearse refactorizar cuando funcione
                    if (v.getName().equals(varName)) {
                        for (es.us.isa.sedl.core.design.Level level : v.getDomain().getLevels()) {
                            labels.add(level.getValue().replaceAll("\"", ""));
                        }
                    }
                }
                for (String label : labels) {
                    List<String> newLine = new ArrayList<String>();
                    for (int i = 1; i < operation.getVariables().size(); i++) {
                        List<String> lineVar = operation.getVariables().get(i);
                        if (label.equals(lineVar.get(labelIndex))) {
                            newLine.add(lineVar.get(valueIndex));
                        }
                    }
                    dataset.add(newLine);
                }
                operation.setDataset(dataset);
            }
        }
        param = param + ")";
        params.add(param);
        operation.setFunctionParam(params);
    }

    private void calculateTypeOnlyFilter(List<List<String>> dataset, BasicExperiment experiment) {
        List<String> header = operation.getVariables().get(0);
        for (Filter filter : operation.getFilters()) {
            ValuationFilter vf = (ValuationFilter) filter;
            List<VariableValuation> listVV = vf.getVariableValuation();
            for (VariableValuation vv : listVV) {
                int indexValues = 2;
                if (header.contains(outcomes.get(0).getName())) {
                    indexValues = header.indexOf(outcomes.get(0).getName());
                } else {
                    ValidationError<BasicExperiment> vs = new ValidationError<BasicExperiment>(experiment, Error.ERROR_SEVERITY.ERROR,
                            "[ERROR] Outcome not found in dataset: " + outcomes.get(0).getName());
                    operation.getErrors().add(vs);
                    System.out.println("[ERROR] Outcome not found in dataset: " + outcomes.get(0).getName());
                }
                String lavelName = vv.getLevel().getValue().replaceAll("\"", "");
                //String varName = vv.getVariable();
                List<String> newLine = new ArrayList<String>();
                for (List<String> list : operation.getVariables()) {
                    if (list.contains(lavelName)) {
                        newLine.add(list.get(indexValues));
                    }
                }
                if (newLine.isEmpty()) {
                    ValidationError<BasicExperiment> vs = new ValidationError<BasicExperiment>(experiment, Error.ERROR_SEVERITY.ERROR, "[ERROR] Level not found: " + lavelName);
                    operation.getErrors().add(vs);
                    System.out.println("ERROR no existe lavel: " + lavelName);
                }
                dataset.add(newLine);
                operation.setDataset(dataset);
            }
        }
    }
    
    private void calculateTypeFilterAndGroup(List<List<String>> dataset, BasicExperiment experiment) {
        List<String> header = operation.getVariables().get(0);
        List<Filter> filters = operation.getFilters();
        List<GroupingProjection> groups = operation.getGrouping();
        int filterIndex = 1;
        int groupIndex = 1;
        int outcomeIndex = 2;
        String flabel = "";
        String gvarname = "";
        for (Filter f : filters) {
            ValuationFilter vf = (ValuationFilter) f;
            for (VariableValuation w : vf.getVariableValuation()) {
                if (header.contains(w.getVariable())) {
                    filterIndex = header.indexOf(w.getVariable());
                    flabel = w.getLevel().getValue();
                }
            }
        }
        for (GroupingProjection gp : groups) {
            String var = gp.getProjectedVariables().get(0);
            if (header.contains(var)) {
                groupIndex = header.indexOf(var);
                gvarname = var;
            }
        }
        List<String> glabels = new ArrayList<String>();
        List<Variable> expVariables = experiment.getDesign().getVariables().getVariable();
        for (Variable v : expVariables) {//replantearse refactorizar cuando funcione
            if (v.getName().equals(gvarname)) {
                for (es.us.isa.sedl.core.design.Level level : v.getDomain().getLevels()) {
                    glabels.add(level.getValue().replaceAll("\"", ""));
                }
            }
        }
        if (operation.getProj().isEmpty()) {
            outcomeIndex = header.indexOf(outcomes.get(0).getName());
        } else {
            outcomeIndex = header.indexOf(operation.getProj().get(0).getProjectedVariables().get(0));
        }
        for (String l : glabels) {
            List<String> newLine = new ArrayList<String>();
            for (int i = 1; i < operation.getVariables().size(); i++) {
                List<String> line = operation.getVariables().get(i);
                if (flabel != null) {
                    if ((line.get(filterIndex).equals(flabel.replaceAll("\"", "")) || flabel == null)
                            && (l.replaceAll("\"", "")).equals(line.get(groupIndex))) {
                        newLine.add(line.get(outcomeIndex));
                    }
                } else {
                    if (l.replaceAll("\"", "").equals(line.get(groupIndex))) {
                        newLine.add(line.get(outcomeIndex));
                    }
                }
            }
            dataset.add(newLine);
        }
        operation.setDataset(dataset);

    }*/
    
    public String renderResults(){
        StringBuilder res=new StringBuilder();
        for(StatisticalAnalysisResult statResult:result)
            res.append(resultsRenderer.render(statResult));
        return res.toString();
    }

    private void validateDatasetSpecfication(DataSet dataset,DatasetSpecification spec) {
        ValidationError error;
        for(Projection p:spec.getProjections()){
            for(String variable:p.getProjectedVariables()){
                if(dataset.getColumn(variable)==null){
                    error=new ValidationError(spec, ERROR_SEVERITY.ERROR, "Variable '"+variable+"' is not present in the datset as a column");
                    this.errors.add(error);
                }
            }
        }
    }
}
