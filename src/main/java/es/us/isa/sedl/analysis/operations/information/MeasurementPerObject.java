package es.us.isa.sedl.analysis.operations.information;


import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.design.FullySpecifiedExperimentalDesign;
import es.us.isa.sedl.core.design.ProtocolScheme;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;


public class MeasurementPerObject  extends AbstractAnalysisOperation<ControlledExperiment, Integer> {

	public static final String NAME = "Measurement per object";
	public static final String DESCRIPTION = "";

	public MeasurementPerObject() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Integer apply(ControlledExperiment input) {
		
		Integer result = 1;
		FullySpecifiedExperimentalDesign design = (FullySpecifiedExperimentalDesign) input.getDesign().getExperimentalDesign();
		if ( !design.getExperimentalProtocol().getScheme().equals(ProtocolScheme.RANDOM )) {
			// qué hacer aquí si no es random?
			result = -1;
		}
//		if ( )
		
		return result;
		
	}
	

}
