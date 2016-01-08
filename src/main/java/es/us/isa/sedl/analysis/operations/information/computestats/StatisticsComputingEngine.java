/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats;

import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;
import java.util.List;

/**
 *
 * @author José Antonio Parejo
 */
public interface StatisticsComputingEngine {        
    boolean isSupported(Statistic s);
    List<StatisticalAnalysisResult> compute(StatisticalAnalysisOperation operation) throws UnsupportedStatisticException;    
}
