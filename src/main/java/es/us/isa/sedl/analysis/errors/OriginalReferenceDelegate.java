package es.us.isa.sedl.analysis.errors;

import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.RuleNode;

public interface OriginalReferenceDelegate<T> {

	public List<ValidationError<T>> fillValidationError ( List<RuleNode> lCtx, CommonTokenStream tokens, List<ValidationError<T>> errorsToFill );
	public Error fillSemanticError( RuleNode Ctx, CommonTokenStream tokens, Error errorToFill );
	
}
