package es.us.isa.sedl.analysis.operations.validation;


import static es.us.isa.exemplar.commons.util.Error.ERROR_SEVERITY.ERROR;

import java.util.ArrayList;
import java.util.List;


import es.us.isa.sedl.core.BasicExperiment;
import es.us.isa.sedl.core.configuration.Configuration;
import es.us.isa.sedl.core.configuration.InputDataSource;
import es.us.isa.sedl.core.design.Level;
import es.us.isa.sedl.core.design.Outcome;
import es.us.isa.sedl.core.design.Variable;
import es.us.isa.sedl.core.design.Variables;
import es.us.isa.sedl.core.execution.Execution;
import es.us.isa.sedl.core.execution.ExperimentalResult;
import es.us.isa.sedl.core.execution.ResultsFile;
import es.us.isa.sedl.core.util.Error.ERROR_SEVERITY;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import es.us.isa.sedl.runtime.analysis.validation.ValidationLevel;
import es.us.isa.sedl.runtime.analysis.validation.ValidationRule;

import java.util.logging.Logger;

public class OutOfRange extends ValidationRule<BasicExperiment>{

    private static final Logger log=Logger.getLogger(OutOfRange.class.getName());
    
    public static final String CODE = "OUT-OF-RANGE";
    public static final String NAME = "Out of range";
    public static final String DESCRIPTION = "";
    
	public OutOfRange(ValidationLevel level) {
		super(level, CODE, NAME, DESCRIPTION);
	}
	public OutOfRange() {
		super(ValidationLevel.SEMANTIC, CODE, NAME, DESCRIPTION);
	}
	
	//-----------------------------
	//VALIDATE METHODS
	//-----------------------------

	public List<String> parseDocument(BasicExperiment element){

		List<String> csvRoots = new ArrayList<String>();
		log.finest("create List ");
		for(Configuration conf : element.getConfigurations()){
			log.finest("for1 ");
			
			try{
				if(!conf.getExecutions().isEmpty()){
					log.finest("if1 ");
					for(Execution e : conf.getExecutions()){
						log.finest("for2 ");
						if(!e.getResults().isEmpty()){
							log.finest("if2 ");
							for(ExperimentalResult er : e.getResults()){
								log.finest("for3 ");
                                                                if(er instanceof ResultsFile)
                                                                {
                                                                    ResultsFile rf=(ResultsFile)er;
                                                                    csvRoots.add(rf.getFile().getPath()+rf.getFile().getName());
                                                                    log.finest("add: "+er);
                                                                }
							}
						}
					}
				}
			}catch(Exception e){
				log.finest("No executions!");
			}
			csvRoots.add("CONFIG");
			try{
				log.finest("parse configurations ");
				if(!conf.getExperimentalInputs().getInputDataSources().isEmpty()){
					log.finest("if ");
					for(InputDataSource in : conf.getExperimentalInputs().getInputDataSources()){
						csvRoots.add(in.getFile().getPath());
						log.finest("add: "+in.getFile().getPath());
					}
				}
			}catch(Exception e){
				System.out.println("No configurations!");
			}
		}
		return csvRoots;
	}
	public List<List<String>> parseCsvContent(String csvContent){
		List<List<String>> result = new ArrayList<List<String>>();
		String[] lines = csvContent.trim().split("\n");
		for(int i = 0; i<lines.length;i++){
			String[] values = lines[i].split(";");
			List<String> lineV = new ArrayList<String>();
			for(int j=0;j<values.length;j++){
				String aux = values[j].trim();
				lineV.add(aux);
			}
			result.add(lineV);
		}
		log.finest(" Data: "+result.toString());
		return result;
	}
	@Override
	public List<ValidationError<BasicExperiment>> validate(BasicExperiment element) {
		
		List<ValidationError<BasicExperiment>> errors=new ArrayList<ValidationError<BasicExperiment>>();
		
		String variableType = element.getDesign().getVariables().getVariable().get(0).getKind().name().toString();
		log.finest("variableType: "+variableType);
		
		boolean result = true;
		Variables variables = element.getDesign().getVariables();
		for(Variable v : variables.getVariable()){
			for(Outcome out : element.getDesign().getOutcomes()){
				for(Level lOut : out.getDomain().getLevels()){
					try {
						result = result && validateVariable(getVariablesValues(variables.getVariable(), v.getName()), lOut.getValue());
						if(!result){
							String description = "[ERROR/WARNING] Variable '"+v.getName()+"' it's Out Of Range, or maybe is necessary to specify a dataset.";
							ValidationError<BasicExperiment> error = new ValidationError<BasicExperiment>(element, ERROR_SEVERITY.WARNING, description);
							errors.add(error);
						}
					} catch (Exception e) {
						String description = "[ERROR] To evaluate variable '"+v.getName()+"' is necessary to specify a dataset.";
						ValidationError<BasicExperiment> error = new ValidationError<BasicExperiment>(element, ERROR_SEVERITY.ERROR, description);
						errors.add(error);
					}
				}
			}
		}
		return errors;
	}
	

	public List<ValidationError<BasicExperiment>> validateCsv(BasicExperiment element, String csvContent) {
		log.finest("csv Content: "+csvContent);
		List<ValidationError<BasicExperiment>> errors=new ArrayList<ValidationError<BasicExperiment>>();
		ValidationError<BasicExperiment> error=null;
                Level observation=new Level();
		boolean result = true;
		
		List<List<String>> dataset = parseCsvContent(csvContent);
		for(Outcome out : element.getDesign().getOutcomes()){
			int outIndex = 0;
			if(dataset.get(0).contains(out.getName())){
				outIndex = dataset.get(0).indexOf(out.getName());				
				for(int i=1; i<dataset.size();i++){
					observation.setValue(dataset.get(i).get(outIndex));
                                        if(!out.getDomain().contains(observation)){
                                            error=new ValidationError<BasicExperiment>(element, ERROR_SEVERITY.ERROR, "The value '"+observation.getValue()+"' of line "+i+" for variable "+out.getName()+"  is not valid.");
                                            errors.add(error);
                                        }
				}
				
			}
		}
		
		return errors;
	}
	
	//-----------------------------
	//GET VARIABLES
	//-----------------------------

	private List<String> getVariablesValues(List<Variable> variables, String variableName){
		System.out.println("[INFO]variableNameParam: "+variableName);
		List<String> result = new ArrayList<String>();
		for(Variable v : variables){
			if(v.getName() == variableName){
				List<Level> levels = v.getDomain().getLevels();
				for(Level l : levels){
					result.add(l.getValue().replaceAll("\"", ""));
				}
			}
		}
		return result;
	}
	
	//---------------------------
	//VALIDATE VARIABLE VALUES
	//---------------------------
	
	private boolean validateVariable(List<String> variableValues, String outcomeValue){
		boolean result = true;
		if(outcomeValue.equals("R")){
			for(String value : variableValues){
				result = result && isReal(value);
			}
			System.out.println("[INFO] validate real number: "+result);
		}else{
			if(outcomeValue.equals("I")){
				for(String value : variableValues){
					result = result && isIrrational(value);
				}
				System.out.println("[INFO] validate irrational number: "+result);
			}else{
				if(outcomeValue.equals("Z")){
					for(String value : variableValues){
						result = result && isInteger(value);
					}
					System.out.println("[INFO] validate integer number: "+result);
				}else{
					if(outcomeValue.equals("N")){
						for(String value : variableValues){
							result = result && isNatural(value);
						}
						System.out.println("[INFO] validate natural number: "+result);
					}else{
						if(outcomeValue.equals("Q")){
							for(String value : variableValues){
								result = result && isRational(value);
								System.out.println("value: "+value);
							}
							System.out.println("[INFO] validate rational number: "+result);
						}else{
							if(outcomeValue.equals("C")){
								for(String value : variableValues){
									result = result && isComplex(value);
								}
								System.out.println("[INFO] validate complex number: "+result);
							}
						}
					}
				}
			}
		}
		return result;
	}
	private boolean isReal(String value){
		boolean result = true;
		result = isRational(value) || isInteger(value);
		try{
			new Double(value);
			new Float(value);
			result = result || true;
		}catch(Exception e){
			result = result && false;
		}
		return result;
	}
	private boolean isRational(String value){
		boolean result = true;
		boolean frac = value.split("\\/").length == 2;
		boolean dec = value.split("\\.").length == 2;
		if(frac){
			String[] split = value.split("\\/");
			for(String s: split){
				result = result && isInteger(s);
			}
		}
		if(dec){
			String[] split = value.split("\\.");
			for(String s: split){
				result = result && isInteger(s);
			}
		}
		return result;	
	}
	private boolean isInteger(String value){
		boolean result = true;
		try{
			new Integer(value);
		}catch(Exception e){
			result = false;
		}
		return result;
	}
	private boolean isNatural(String value){
		boolean result = true;
		boolean isInt = isInteger(value);
		result = result && isInt;
		if(isInt){
			result = result && (new Integer(value)>0);
		}
		return result;
	}
	private boolean isIrrational(String value){
		return true;
	}
	private boolean isComplex(String value){
		return true;
	}
}
