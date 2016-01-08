/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats;

import es.us.isa.sedl.core.analysis.statistic.Statistic;

/**
 *
 * @author José Antonio Parejo
 */
public class UnsupportedStatisticException extends Exception {

    public UnsupportedStatisticException(Statistic statistic,StatisticsComputingEngine engine) {
        super("The statistic '"+statistic+"' is not supported by the computing engine '"+engine.getClass().getCanonicalName()
                +"'.");
    }
   }
