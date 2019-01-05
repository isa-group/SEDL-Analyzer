package es.us.isa.sedl.analysis.operations.information;


import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.design.ExtensionDomain;
import es.us.isa.sedl.core.design.FullySpecifiedExperimentalDesign;
import es.us.isa.sedl.core.design.Variable;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;

public class NumberOfBlocks extends
		AbstractAnalysisOperation<ControlledExperiment, Integer> {

	public static final String NAME = "Number of blocks";
	public static final String DESCRIPTION = "";

	public NumberOfBlocks() {
		super(NAME, DESCRIPTION);
	}

	public Integer apply(ControlledExperiment input) {

		Integer result = 1;

		if (input.getDesign() != null
				&& input.getDesign().getExperimentalDesign() != null) {
			FullySpecifiedExperimentalDesign expDesign = (FullySpecifiedExperimentalDesign) input
					.getDesign().getExperimentalDesign();
			if (expDesign.getAssignmentMethod().getBlockingVariables() != null
					&& !expDesign.getAssignmentMethod().getBlockingVariables().isEmpty()) {
				for (Variable v : input.getDesign().getVariables()
						.getVariables()) {
					if (expDesign.getAssignmentMethod().getBlockingVariables().contains(v.getName())) {
						if (v.getDomain() instanceof ExtensionDomain) {
							
							int nLvls = ((ExtensionDomain)v.getDomain()).getLevels().size();
							result *= nLvls;
						}
					}
				}
			} else {
				// No blocking vars
				result = 1;
			}
		}
		return result;
	}
}
