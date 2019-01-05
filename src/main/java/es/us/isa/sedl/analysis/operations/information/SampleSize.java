/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.sedl.analysis.operations.information;



import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.design.FullySpecifiedExperimentalDesign;
import es.us.isa.sedl.core.design.Group;
import es.us.isa.sedl.core.design.LiteralSizing;
import es.us.isa.sedl.runtime.analysis.AbstractAnalysisOperation;

/**
 *
 * @author japarejo
 */
public class SampleSize extends AbstractAnalysisOperation<ControlledExperiment, Integer> {

    public static final String NAME="Sample Size";
    public static final String DESCRIPTION="";
    
    private NumberOfBlocks blocks = new NumberOfBlocks();
    
    public SampleSize() {
        super(NAME, DESCRIPTION);
    }
    
    public Integer apply(ControlledExperiment input) {
        Integer result=0;
        if(input.getDesign()!=null && input.getDesign().getExperimentalDesign()!=null)
        {
        	int groupsSize = 0;
        	FullySpecifiedExperimentalDesign design = (FullySpecifiedExperimentalDesign) input.getDesign().getExperimentalDesign();
        	for ( Group g : design.getGroups() ) {
        		if ( g.getSizing() instanceof LiteralSizing ) {
        			groupsSize += ((LiteralSizing)g.getSizing()).getValue().intValue();
        		}
        	}
        	result = blocks.apply(input) * groupsSize;
        }
        return result;
    }
    
}
