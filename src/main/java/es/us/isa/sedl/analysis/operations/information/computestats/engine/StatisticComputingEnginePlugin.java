/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.engine;

import es.us.isa.sedl.analysis.operations.information.computestats.StatisticsComputingEngine;
import es.us.isa.sedl.core.analysis.statistic.Statistic;

/**
 *
 * @author José Antonio Parejo
 */
public interface StatisticComputingEnginePlugin extends StatisticsComputingEngine {
    Class<? extends Statistic> supportedStatistic();
}
