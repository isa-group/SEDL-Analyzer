package es.us.isa.sedl.analysis.operations.validation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import es.us.isa.sedl.analysis.operations.information.SampleSize;
import es.us.isa.sedl.core.BasicExperiment;
import static es.us.isa.sedl.core.util.Error.ERROR_SEVERITY.WARNING;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import es.us.isa.sedl.runtime.analysis.validation.ValidationLevel;
import es.us.isa.sedl.runtime.analysis.validation.ValidationRule;


/**
 *
 * @author japarejo
 */
public class SmallSampling extends ValidationRule<BasicExperiment>{

    public static final String CODE = "SMALL-SAMPLING";
    public static final String NAME = "Small Sampling";
    public static final String DESCRIPTION = "";
    public static final Integer DEFAULT_THRESHOLD = 30;
    
    private Integer threshold;
    
    //-------------------------------------------------------------
    // Auxiliary operations:
    //-------------------------------------------------------------
    private SampleSize sampleSize = new SampleSize();
    //-------------------------------------------------------------
    
    public SmallSampling()
    {
        this(DEFAULT_THRESHOLD);
    }
    
    public SmallSampling(Integer threshold) {        
        super(ValidationLevel.SEMANTIC, CODE, NAME, DESCRIPTION);
        this.threshold=threshold;
    }

    @Override
    public List<ValidationError<BasicExperiment>> validate(BasicExperiment exp) {
        List<ValidationError<BasicExperiment>> errors=null;
        Integer experimentSampleSize=sampleSize.apply(exp);
        if(experimentSampleSize>=threshold)
            errors=Collections.EMPTY_LIST;
        else{
            errors=new ArrayList<ValidationError<BasicExperiment>>();
            String description="The size of the sample used in the experiment is too small";
            ValidationError<BasicExperiment> error=new ValidationError<BasicExperiment>(exp, WARNING, description);
            errors.add(error);
        }
        return errors;
    }

    public Integer getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Integer value)
    {
        this.threshold=value;
    }

    

    
    
}
