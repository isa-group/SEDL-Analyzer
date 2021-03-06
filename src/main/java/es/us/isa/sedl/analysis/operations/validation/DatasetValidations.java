package es.us.isa.sedl.analysis.operations.validation;

import java.util.ArrayList;
import java.util.List;


import es.us.isa.jdataset.Column;
import es.us.isa.jdataset.DataSet;
import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.design.Level;
import es.us.isa.sedl.core.design.Outcome;
import es.us.isa.sedl.core.util.Error.ERROR_SEVERITY;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;

public class DatasetValidations {
	private DataSet dataset;
	private ControlledExperiment experiment;
	private List<Integer> outcomeIndexes = new ArrayList<Integer>(); 
	public DatasetValidations (DataSet dataset, ControlledExperiment exp){
		this.dataset = dataset;
		this.experiment = exp;
	}
	public List<ValidationError<ControlledExperiment>> validateDataset(){
		List<ValidationError<ControlledExperiment>> result = new ArrayList<ValidationError<ControlledExperiment>>();				
		checkHeader(this.dataset,this.experiment, result);
		checkOutcomesValues(dataset, experiment, result);
		return result;
	}
	private void checkHeader(DataSet dataset, ControlledExperiment experiment, List<ValidationError<ControlledExperiment>> errors){
		List<Outcome> outcomes = experiment.getDesign().getOutcomes();
		boolean flagOutcome = false;
		if(dataset.getColumns().size()<2){
			ValidationError<ControlledExperiment> ve = new ValidationError<ControlledExperiment>(experiment, ERROR_SEVERITY.ERROR, "[ERROR] Dataset isn't correct, please search an example in 'SEDL-Example-WS' workspace.");
			errors.add(ve);
		}
		for(Outcome outcome : outcomes){
			if(dataset.getColumn(outcome.getName())==null){
                            ValidationError<ControlledExperiment> ve = new ValidationError<ControlledExperiment>(experiment, ERROR_SEVERITY.ERROR, 
					"[ERROR] There outcome varaible '"+outcome.getName()+"' is not present as a column in the data set");
                            errors.add(ve);
			}
		}						
	}
	private void checkOutcomesValues(DataSet dataset, ControlledExperiment experiment, List<ValidationError<ControlledExperiment>> errors){
		List<Outcome> outcomes = experiment.getDesign().getOutcomes();
                Level level=new Level();
		for(Outcome outcome : outcomes){
                    Column<?> column=dataset.getColumn(outcome.getName());
                    if(column==null){
                        ValidationError<ControlledExperiment> ve = new ValidationError<ControlledExperiment>(experiment, ERROR_SEVERITY.ERROR, 
								"[ERROR] There is no data for outcome variable '"+outcome.getName()+"', please review.");
                        errors.add(ve);
                    }else{                        
                        for(int i=0;i<column.size();i++){
                          level.setValue(column.get(i).toString());
                          if(!outcome.getDomain().contains(level)){
                              ValidationError<ControlledExperiment> ve = new ValidationError<ControlledExperiment>(experiment, ERROR_SEVERITY.ERROR, 
								"[ERROR] Value '"+level.getValue()+"' (row: "+i+") of column '"+column.getName()+"' does not pertain to the domain of its corresponding variable");
                              errors.add(ve);
                            }
                        }
                    }
                    
			
		}
	}
}
