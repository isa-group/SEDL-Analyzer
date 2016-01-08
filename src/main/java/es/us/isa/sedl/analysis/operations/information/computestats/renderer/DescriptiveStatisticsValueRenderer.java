/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.renderer;

import es.us.isa.sedl.analysis.operations.information.computestats.StatisticalResultsRenderer;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author José Antonio Parejo
 */
public class DescriptiveStatisticsValueRenderer implements StatisticalResultsRenderer<String>
{
    public static final String templateFileName="templates/SEDL.stg";
    
    private STGroup group;
    private ST st;

    public DescriptiveStatisticsValueRenderer() {    
        group = new STGroupFile(templateFileName);        
    }
    
    @Override
    public String render(StatisticalAnalysisResult staresult) {
        st=group.getInstanceOf("exeFunction");
        st.add("a", staresult);
        return st.render();
    }

    
}
