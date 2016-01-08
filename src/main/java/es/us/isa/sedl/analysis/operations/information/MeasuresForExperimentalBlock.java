package es.us.isa.sedl.analysis.operations.information;


import es.us.isa.sedl.core.BasicExperiment;
import es.us.isa.sedl.core.design.FullySpecifiedExperimentalDesign;
import es.us.isa.sedl.core.design.Group;
import es.us.isa.sedl.core.design.Literal;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;


public class MeasuresForExperimentalBlock  extends AbstractAnalysisOperation<BasicExperiment, Integer> {

	public static final String NAME = "Measurement for experimental block";
	public static final String DESCRIPTION = "";

	private MeasurementPerObject measurement = new MeasurementPerObject();
	
	public MeasuresForExperimentalBlock() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Integer apply(BasicExperiment input) {
		
		Integer result = 1;
		if ( input.getDesign().getExperimentalDesign() instanceof FullySpecifiedExperimentalDesign ) {
			FullySpecifiedExperimentalDesign design = (FullySpecifiedExperimentalDesign) input.getDesign().getExperimentalDesign();
			Integer measurementPerObject = measurement.apply(input);
			for ( Group g : design.getGroups() ) {
				if ( g.getSizing() instanceof Literal ) {
					Literal l = (Literal)g.getSizing();
					result += (l.getValue().intValue() * measurementPerObject);
				}
			}
		}
		
		return result;
		
	}
	

}
