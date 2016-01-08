/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.renderer;


import es.us.isa.sedl.analysis.operations.information.computestats.StatisticalResultsRenderer;
import es.us.isa.sedl.core.analysis.statistic.DescriptiveStatisticValue;
import es.us.isa.sedl.core.analysis.statistic.PValue;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;
import es.us.isa.sedl.util.ImplementationsRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Antonio Parejo
 */
public class ExtensibleStatisticalResultsRenderer extends ImplementationsRegistry<StatisticalResultsRenderingPlugin> implements StatisticalResultsRenderer<String>{

    Map<Class<? extends StatisticalAnalysisResult>, StatisticalResultsRenderer<String>> subRenderers;

    public ExtensibleStatisticalResultsRenderer() {
        super(StatisticalResultsRenderingPlugin.class,"es.us.isa.sedl");
        subRenderers=new HashMap<>();
        subRenderers.put(DescriptiveStatisticValue.class, new DescriptiveStatisticsValueRenderer());
        subRenderers.put(PValue.class, new DescriptiveStatisticsValueRenderer());
        for(Class<? extends StatisticalResultsRenderingPlugin> srrpClass:getSubClasses()){
            try {
                Constructor constructor=srrpClass.getConstructor();
                StatisticalResultsRenderingPlugin srrp=(StatisticalResultsRenderingPlugin)constructor.newInstance();
                subRenderers.put(srrp.getRenderingClass(),srrp);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ExtensibleStatisticalResultsRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    @Override
    public String render(StatisticalAnalysisResult staresult) {
        String result="";
        for(Class<? extends StatisticalAnalysisResult> sClass:subRenderers.keySet())
            if(sClass.isInstance(staresult))
                result+=subRenderers.get(sClass).render(staresult);
        return result;
    }
    
}
