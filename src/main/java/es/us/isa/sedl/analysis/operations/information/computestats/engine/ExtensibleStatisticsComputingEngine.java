/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.engine;


import es.us.isa.sedl.analysis.operations.information.computestats.StatisticalAnalysisOperation;
import es.us.isa.sedl.analysis.operations.information.computestats.StatisticsComputingEngine;
import es.us.isa.sedl.analysis.operations.information.computestats.UnsupportedStatisticException;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;
import es.us.isa.sedl.grammar.SEDL4PeopleLexer;
import es.us.isa.sedl.util.ImplementationsRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Antonio Parejo
 */
public class ExtensibleStatisticsComputingEngine extends ImplementationsRegistry<StatisticComputingEnginePlugin> implements StatisticsComputingEngine {

    Map<Class<? extends Statistic>, StatisticsComputingEngine> subEngines;

    public ExtensibleStatisticsComputingEngine() {
        super(StatisticComputingEnginePlugin.class,"es.us.isa.sedl");
        subEngines=new HashMap<>();
        for(Class<? extends StatisticComputingEnginePlugin> c:getSubClasses())
        {
            if(c!=this.getClass()){
                try {
                    Constructor constructor=c.getConstructor();
                    StatisticComputingEnginePlugin instance=(StatisticComputingEnginePlugin) constructor.newInstance();
                    subEngines.put(instance.supportedStatistic(),instance);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }    
    
    @Override
    public boolean isSupported(Statistic s) {
        return findSuitableComputingEngine(s)!=null;
    }

    protected static final String token(int i) {
        String result = null;
        try {                                    
            result = SEDL4PeopleLexer.VOCABULARY.getLiteralName(i).replace("'", "");
        } catch (SecurityException ex) {
            Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ExtensibleStatisticsComputingEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    

    @Override
    public List<StatisticalAnalysisResult> compute(StatisticalAnalysisOperation operation) throws UnsupportedStatisticException {
        List<StatisticalAnalysisResult> result=Collections.EMPTY_LIST;
        StatisticsComputingEngine sce=findSuitableComputingEngine(operation.getStatistic());
        if(sce!=null)
            result=sce.compute(operation);
        else
            throw new UnsupportedStatisticException(operation.getStatistic(), this);
        return result;
    }
    private StatisticsComputingEngine findSuitableComputingEngine(Statistic statistic) {
        StatisticsComputingEngine result=null;    
        for(Class<? extends Statistic> statisticClass:subEngines.keySet()){
            if(statisticClass.isInstance(statistic)){
                if(subEngines.get(statisticClass).isSupported(statistic))
                    result=subEngines.get(statisticClass);
            }
        }
        return result;
    }

    

    
}
