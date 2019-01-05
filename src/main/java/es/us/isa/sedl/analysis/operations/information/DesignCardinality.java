package es.us.isa.sedl.analysis.operations.information;


import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;


public class DesignCardinality  extends AbstractAnalysisOperation<ControlledExperiment, Integer> {

	public static final String NAME = "Design cardinality";
	public static final String DESCRIPTION = "";

	private MeasuresForExperimentalBlock mBlock = new MeasuresForExperimentalBlock();
	private NumberOfBlocks blocks = new NumberOfBlocks();
	
	public DesignCardinality() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Integer apply(ControlledExperiment input) {
		
		Integer result = 1;
		result = blocks.apply(input) * mBlock.apply(input);
		return result;
		
	}

}
