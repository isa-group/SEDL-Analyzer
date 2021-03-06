package es.us.isa.sedl.analysis.operations.validation;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.analysis.datasetspecification.GroupFilter;
import es.us.isa.sedl.core.analysis.datasetspecification.ValuationFilter;
import es.us.isa.sedl.core.analysis.statistic.Nhst;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.design.ExtensionDomain;
import es.us.isa.sedl.core.design.Variable;
import es.us.isa.sedl.core.design.VariableValuation;
import es.us.isa.sedl.grammar.SEDL4PeopleLexer;
import java.util.ArrayList;
import java.util.List;
import es.us.isa.sedl.core.analysis.datasetspecification.Filter;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.design.AnalysisSpecificationGroup;
import static es.us.isa.sedl.core.util.Error.ERROR_SEVERITY.ERROR;
import es.us.isa.sedl.runtime.analysis.validation.ValidationError;
import es.us.isa.sedl.runtime.analysis.validation.ValidationLevel;
import es.us.isa.sedl.runtime.analysis.validation.ValidationRule;

/**
 *
 * @author japarejo
 */
public class MultipleComparison extends ValidationRule<ControlledExperiment> {

    public static final String CODE = "MULTIPLE-COMPARISON";
    public static final String NAME = "Multiple Comparison";
    public static final String DESCRIPTION = "This analysis operation checks if several datasets are being compared using a simple"
            + "comparison statistical test, instead of a specific test for multiple comparison. "
            + "If a simple comparison test is used, the error rate could be accumulated leading "
            + "to erroneous conclusions. To neutralize this threat, the statistical analysis"
            + "should be repeated using a suitable technique for comparing multiple datasets.";

    List<String> multipleComparisonNames = new ArrayList<String>();

    public MultipleComparison() {
        super(ValidationLevel.SEMANTIC, CODE, NAME, DESCRIPTION);
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.ANOVA));
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.FRIEDMAN));
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.ALIGNED_FRIEDMAN));
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.IMAN_DAVEPORT));
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.QUADE));
        multipleComparisonNames.add(getTokenName(SEDL4PeopleLexer.COCHRAN_Q));
    }

    //TODO: Se puede hacer mas eficiente. Lo dejamos asi� para la demo del 7/3/14
    @Override
    public List<ValidationError<ControlledExperiment>> validate(ControlledExperiment exp) {

        List<ValidationError<ControlledExperiment>> lErrors = new ArrayList<ValidationError<ControlledExperiment>>();

        _analysisLoop: //TODO: Refactorizar para soportar varias lineas de error
        for (AnalysisSpecificationGroup eas : exp.getDesign().getExperimentalDesign().getIntendedAnalyses()) {
            if (eas instanceof StatisticalAnalysisSpec) {
                boolean multiple = true;
                boolean multipleLevels = false;
                StatisticalAnalysisSpec analysis = (StatisticalAnalysisSpec) eas;
                for (Statistic stat : analysis.getStatistic()) {

                    if (stat instanceof Nhst) {
                        _search:
                        for (Filter filter : stat.getDatasetSpecification().getFilters()) {
                            String varName = "";
                            if (filter instanceof GroupFilter) {
                                ((GroupFilter) filter).getGroup();
                            } else if (filter instanceof ValuationFilter) {
                                for (VariableValuation varVal : ((ValuationFilter) filter).getVariableValuations()) {
                                    varName = varVal.getVariable();

                                    for (Variable designVariable : exp.getDesign().getVariables().getVariables()) {
                                        if (designVariable.getName().equals(varName) && designVariable.getDomain() instanceof ExtensionDomain && ((ExtensionDomain) designVariable.getDomain()).getLevels().size() > 2) {
                                            multipleLevels = true;
                                            break _search;
                                        }
                                    }

                                }
                            }

                        }

                        Nhst nhst = (Nhst) stat;
                        if (!multipleComparisonNames.contains(nhst.getName())) {
                            multiple = false;
                        }
                    }

                }

                if (multiple == false && multipleLevels) {
                    String errDesc = "Multiple datasets cannot be compared using simple comparison tests. Use a multiple comparison test instead.";
                    ValidationError<ControlledExperiment> error = new ValidationError<ControlledExperiment>(exp, ERROR, errDesc);
                    lErrors.add(error);
                    break _analysisLoop;
                }

                multipleLevels = false;
                multiple = true;

            }
        }

        return lErrors;

    }

    private String getTokenName(int token) {
        String tk = SEDL4PeopleLexer.tokenNames[token];
        return tk.substring(1, tk.length() - 1).trim();
    }

}
