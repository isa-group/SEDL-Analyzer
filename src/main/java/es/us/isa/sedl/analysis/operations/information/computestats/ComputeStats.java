package es.us.isa.sedl.analysis.operations.information.computestats;

import es.us.isa.sedl.analysis.operations.information.computestats.engine.ExtensibleStatisticsComputingEngine;

import java.util.ArrayList;
import java.util.List;


import es.us.isa.jdataset.DataSet;
import es.us.isa.jdataset.loader.CSVLoader;
import es.us.isa.sedl.analysis.operations.information.computestats.ComputeStats.StatisticalAnalysisConfiguration;
import es.us.isa.sedl.analysis.operations.information.computestats.renderer.ExtensibleStatisticalResultsRenderer;
import es.us.isa.sedl.analysis.operations.validation.DatasetValidations;
import es.us.isa.sedl.core.BasicExperiment;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.design.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.configuration.Configuration;
import es.us.isa.sedl.core.configuration.InputDataSource;
import es.us.isa.sedl.core.design.AnalysisSpecification;
import es.us.isa.sedl.core.design.Outcome;
import es.us.isa.sedl.core.execution.Execution;
import es.us.isa.sedl.core.execution.ExperimentalResult;
import es.us.isa.sedl.core.util.Error.ERROR_SEVERITY;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;


public class ComputeStats extends AbstractAnalysisOperation<StatisticalAnalysisConfiguration, List<StatisticalAnalysisOperation>>{

    private static final Logger log =Logger.getLogger(ComputeStats.class.getName());
    public static final String CODE = "COMPUTE-STATS";
    public static final String NAME = "Compute stats";
    public static final String DESCRIPTION = "";
    
    public static final String DEFAULT_ROW_DELIMITER="\n";
    public static final String DEFAULT_COLUMN_DELIMITER=";";
    
    private StatisticsComputingEngine sce;
    private StatisticalResultsRenderer<String> renderer;
    //private Map<STATISTICS_OUTPUT_FORMAT,StatisticalResultsRenderer> renderers;
    
    private List<Outcome> outcomes = new ArrayList<>();
    
        public ComputeStats() {
            this(new ExtensibleStatisticsComputingEngine(),new ExtensibleStatisticalResultsRenderer());
        }
    
	public ComputeStats(StatisticsComputingEngine sce,StatisticalResultsRenderer<String> renderer) {
		super(CODE,DESCRIPTION);
                this.sce=sce;
                this.renderer=renderer;
	}
	
	public List<String> getCandidateDatasetFiles(BasicExperiment element){
            List<String> result = new ArrayList<String>();
            for (Configuration conf : element.getConfigurations()) {
                result.addAll(getCandidateDatasetFilesFromExecutions(conf));

                if (!conf.getExperimentalInputs().getInputDataSources().isEmpty()) {
                    for (InputDataSource in : conf.getExperimentalInputs().getInputDataSources()) {
                        result.add(in.getFile().getPath());
                        log.finest(" add: " + in.getFile().getPath());
                    }
                }

            }
            return result;
	}
        
        public List<String> getCandidateDatasetFilesFromExecutions(Configuration conf)
        {
            List<String> result = new ArrayList<String>();
            try {
                if (!conf.getExecutions().isEmpty()) {
                    for (Execution e : conf.getExecutions()) {
                        if (!e.getResults().isEmpty()) {
                            for (ExperimentalResult er : e.getResults()) {
                                result.add(er.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.finest(" No executions!");
            }
            return result;
        }
	

	public List<StatisticalAnalysisOperation> apply(BasicExperiment element) {
		List<StatisticalAnalysisOperation> result = new ArrayList<StatisticalAnalysisOperation>();
		StatisticalAnalysisOperation op = new StatisticalAnalysisOperation(sce,renderer);
		String description = "Please insert a dataset using a csv file";
		ValidationError<BasicExperiment> error=new ValidationError<BasicExperiment>(element,ERROR_SEVERITY.WARNING, description);
		op.getErrors().add(error);
		result.add(op);
		return result;
	}
	
	public List<StatisticalAnalysisOperation> applyCSV(BasicExperiment experiment, String csvContent) {
		outcomes = experiment.getDesign().getOutcomes();
		DataSet dataset = parseCsvContent(csvContent);
		DatasetValidations dv = new DatasetValidations(dataset, experiment);
		List<ValidationError<BasicExperiment>> validation = dv.validateDataset();
                List<StatisticalAnalysisOperation> operations = generateStatisticalAnalysisOperations(experiment);                
                for(StatisticalAnalysisOperation operation:operations){
                    if(validation.isEmpty()){
                        operation.apply(dataset,experiment);
                    }else
                       operation.getErrors().addAll(validation);
                }
                		
		return operations;
	}
	
	public DataSet parseCsvContent(String csvContent){
            DataSet dataset=null;
        try {
            /*List<List<String>> result = new ArrayList<List<String>>();
            String[] lines = csvContent.trim().split(DEFAULT_ROW_DELIMITER);
            String columnDelimiter=DEFAULT_COLUMN_DELIMITER;
            if(lines.length>0)
            columnDelimiter=determineColumnDelimeter(lines[0]);
            for(int i = 0; i<lines.length;i++){
            String[] values = lines[i].split(columnDelimiter);
            List<String> lineV = new ArrayList<String>();
            for(int j=0;j<values.length;j++){
            String aux = values[j].trim();
            lineV.add(aux);
            }
            result.add(lineV);
            }
            log.finest(" Data: "+result.toString());
            return result;*/
            CSVLoader loader=new CSVLoader(null);
            InputStream is =  IOUtils.toInputStream(csvContent);
            dataset=loader.load(is, "csv");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ComputeStats.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return dataset;
	}
	
	public List<StatisticalAnalysisOperation> generateStatisticalAnalysisOperations(BasicExperiment experiment){
		List<AnalysisSpecification> analyses = experiment.getDesign().getExperimentalDesign().getIntendedAnalyses();
		List<StatisticalAnalysisOperation> operations = new ArrayList<StatisticalAnalysisOperation>();
		for(AnalysisSpecification analSpec : analyses){
			StatisticalAnalysisSpec statSpec = (StatisticalAnalysisSpec) analSpec;			
			for(Statistic statistic : statSpec.getExpandedStatistics(experiment)){								
				StatisticalAnalysisOperation operation = new StatisticalAnalysisOperation(sce,renderer);			
                                operation.setStatistic(statistic);
				operations.add(operation);
			}
		}
		return operations;
	}
	
	
	public List<StatisticalAnalysisOperation> calculateStats(List<StatisticalAnalysisOperation> operations, String csvContent, BasicExperiment experiment){
		List<StatisticalAnalysisOperation> result = operations;
		DataSet dataset = extractDataset(csvContent);
		for(StatisticalAnalysisOperation operation : result){						
                    operation.apply(dataset);
		}
		return result;
	}
		
    @Override
    public List<StatisticalAnalysisOperation> apply(StatisticalAnalysisConfiguration f) {
        return applyCSV(f.getExperiment(), f.getDatasetURI());
    }
    
    protected DataSet extractDataset(String csvContent)
    {
        return null;
    }

    private String determineColumnDelimeter(String line) {
        String [] alternativeDelimiters={",","|"};
        String result=null;
        if(line.contains(DEFAULT_COLUMN_DELIMITER))
            result=DEFAULT_COLUMN_DELIMITER;
        else{
            for(String altDelimiter:alternativeDelimiters){
                if(line.contains(altDelimiter)){
                    result=altDelimiter;
                    break;
                }
            }
        }
        return result;
    }    
        
        class StatisticalAnalysisConfiguration
        {
            private BasicExperiment experiment;
            private String datasetURI;

            public StatisticalAnalysisConfiguration(BasicExperiment experiment, String datasetURI) {
                this.experiment = experiment;
                this.datasetURI = datasetURI;
            }

            /**
             * @return the experiment
             */
            public BasicExperiment getExperiment() {
                return experiment;
            }

            /**
             * @param experiment the experiment to set
             */
            public void setExperiment(BasicExperiment experiment) {
                this.experiment = experiment;
            }

            /**
             * @return the datasetURI
             */
            public String getDatasetURI() {
                return datasetURI;
            }

            /**
             * @param datasetURI the datasetURI to set
             */
            public void setDatasetURI(String datasetURI) {
                this.datasetURI = datasetURI;
            }

        }
}
