package es.us.isa.sedl.analysis.errors;

import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.RuleNode;




public class SEDL4PeopleReferenceDelegate implements OriginalReferenceDelegate {

    @Override
    public List fillValidationError(List lCtx, CommonTokenStream tokens, List errorsToFill) {
        
		if ( lCtx.size() == errorsToFill.size() ) {
			for ( int i = 0; i < lCtx.size(); i++ ) {
				RuleNode ctx = (RuleNode)lCtx.get(i);
				ValidationError errorToFill = (ValidationError)errorsToFill.get(i);
				Interval interval = ctx.getSourceInterval();
				Token token = tokens.get(interval.a);
				int line = token.getLine() - 1;
				errorToFill.setLineNo(line);
			}
		}
		return errorsToFill;
	}

    @Override
    public Error fillSemanticError(RuleNode Ctx, CommonTokenStream tokens, Error errorToFill) {
        return errorToFill;
    }
    
	
	

    

}
