/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.engine;
	 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.us.isa.jdataset.Column;
import es.us.isa.jdataset.DataSet;
import es.us.isa.sedl.analysis.operations.information.computestats.StatisticalAnalysisOperation;
import es.us.isa.sedl.analysis.operations.information.computestats.UnsupportedStatisticException;
import es.us.isa.sedl.core.analysis.statistic.DescriptiveStatistic;
import es.us.isa.sedl.core.analysis.statistic.DescriptiveStatisticValue;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisResult;


/**
 *
 * @author José Antonio Parejo
 */
public class DescriptiveStatisticsComputingEngine implements StatisticComputingEnginePlugin {

    @Override
    public boolean isSupported(Statistic s) {
        return s instanceof DescriptiveStatistic;
    }

    @Override
    public List<StatisticalAnalysisResult> compute(StatisticalAnalysisOperation operation) throws UnsupportedStatisticException {
        List<StatisticalAnalysisResult> result = Collections.EMPTY_LIST;
        String statistic = operation.getStatistic().getClass().getSimpleName();
        switch (statistic) {
            case "Mean":
                result = computeMean(operation);
                break;
            case "Median":
                result = computeMedian(operation);
                break;
            case "Mode":
                result = computeMode(operation);
                break;
            case "StandardDeviation":
                result = computeStandardDesviation(operation);
                break;
            case "Variance":
                result = computeVariance(operation);
                break;
            case "InterquartileRange":
                result = computeIQR(operation);
                break;
            case "Range":
                result = computeRange(operation);
                break;
            default:
                throw new UnsupportedStatisticException(operation.getStatistic(), this);
        }
        return result;
    }

    private List<StatisticalAnalysisResult> computeMean(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();                
        for (Column c :operation.getDataset().getColumns()) {
            result.add(createDSV(operation, computeMean(c)));
        }
        return result;
    }
    
    
    public Double computeMean(Column<? extends Number> column)
    {
        Double sum=0.0;
        Integer observations=0;
        for(Number n:column){
            if(n!=null){
                sum+=n.doubleValue();
                observations++;
            }
        }
        return sum/observations;
    }

    private List<StatisticalAnalysisResult> computeMedian(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();
        DataSet dataset = operation.getDataset();
        for (Column column : dataset.getColumns()) {            
            result.add(createDSV(operation, computeMedian(column)));
        }
        return result;
    }
    
    public Double computeMedian(Column<? extends Number> column)
    {
            Double median = 0.0;
            List<Double> params = sort(column);
            if (params.size() % 2 == 0) {
                Number n =  params.get((params.size() / 2) - 1);
                Number n1 = params.get((params.size() / 2) + 1 - 1);
                median = (n.doubleValue() + n1.doubleValue()) / 2;
            } else {
                Number n = params.get((params.size() / 2) + 1);
                median = n.doubleValue();
            }
            return median;
    }

    public List<StatisticalAnalysisResult> computeMode(StatisticalAnalysisOperation operation) {
        List<Map<String, Integer>> aux = new ArrayList<Map<String, Integer>>();
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();
        DataSet dataset = operation.getDataset();
        for (Column column : dataset.getColumns()) {
            result.add(createDSV(operation, computeMode(column).toString()));
        }
        return result;
    }

    public Object computeMode(Column column) {
        List params = null;
        if(Comparable.class.isAssignableFrom(column.getClass()))
            params=order(column);
        else if(Number.class.isAssignableFrom(column.getClass()))
            params=sort(column);        
        Object prev = null;
        Integer actualCount = 0, modeCount = 0;
        Object mode = null;
        for (Object value : params) {
            if (value.equals(prev)) {
                actualCount++;
            } else {
                if (actualCount > modeCount) {
                    modeCount = actualCount;
                    mode = value;
                }
                prev = value;
                actualCount = 1;
            }
        }
        return mode;
    }

    public List<StatisticalAnalysisResult> computeStandardDesviation(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();                        
        for (Column column : operation.getDataset().getColumns()) {            
            result.add(createDSV(operation,computeStandartdDeviation(column)));
        }
        return result;
    }
    
    public Double computeStandartdDeviation(Column<? extends Number> column)            
    {
            Double stdDev = 0.0;
            Integer N = column.size();
            Double sumatory = 0.0, mean = 0.0;
            Integer count=0;
            for (Number n:column) {
                if (n != null) {
                    mean = mean + n.doubleValue();
                    count++;
                }
            }
            mean = mean / count;
            for (Number n:column) {
                if (n != null) {
                    sumatory += ((n.doubleValue() - mean) * (n.doubleValue() - mean));
                }
            }
            stdDev = Math.sqrt(sumatory / count);
            return stdDev;
        
    }

    public List<StatisticalAnalysisResult> computeVariance(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();                
        for (Column column : operation.getDataset().getColumns())            
            result.add(createDSV(operation, computeVariance(column)));
        return result;
    }
    
    public Double computeVariance(Column<? extends Number> column)
    {
        Double sumatory = 0.0;
        Double mean = computeMean(column);
        Double variance = 0.0;
        Double N = 0.0;
        for (Number n:column) {
            if(n!=null){
                sumatory+= (n.doubleValue() * N.doubleValue());
                N++;
            }
        }
        return ((1.0 / N) * sumatory) - (mean * mean);
    }
    
    

    private List<StatisticalAnalysisResult> computeIQR(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();        
        for (Column column : operation.getDataset().getColumns())                        
            result.add(createDSV(operation, computeIQR(column)));        
        return result;
    }
    
    public Double computeIQR(Column<? extends Number> column)
    {
            List<Double> params = sort(column);
            int index1, index2, index3;
            Double n = new Double(column.size());
            Double q1 = (n * 1.0) / 4.0;
            Double q2 = (n * 2.0) / 4.0;
            Double q3 = (n * 3.0) / 4.0;
            String decimal1 = q1.toString().split("\\.")[1];
            String decimal2 = q2.toString().split("\\.")[1];
            String decimal3 = q3.toString().split("\\.")[1];
            if (new Integer(decimal1) < 5) {
                index1 = new Integer(q1.toString().split("\\.")[0]) - 1;
            } else {
                index1 = new Integer(q1.toString().split("\\.")[0]) + 1 - 1;
            }
            if (new Integer(decimal2) < 5) {
                index2 = new Integer(q2.toString().split("\\.")[0]) - 1;
            } else {
                index2 = new Integer(q2.toString().split("\\.")[0]) + 1 - 1;
            }
            if (new Integer(decimal3) < 5) {
                index3 = new Integer(q3.toString().split("\\.")[0]) - 1;
            } else {
                index3 = new Integer(q3.toString().split("\\.")[0]) + 1 - 1;
            }
            q1 = params.get(index1);
            q2 = params.get(index2);
            q3 = params.get(index3);
            return q3 - q1;
    }

    public List<StatisticalAnalysisResult> computeRange(StatisticalAnalysisOperation operation) {
        List<StatisticalAnalysisResult> result = new ArrayList<StatisticalAnalysisResult>();        
        for (Column column : operation.getDataset().getColumns())
            result.add(createDSV(operation, computeRange(column)));
        return result;
    }

    public Double computeRange(Column<? extends Number> c) {
        List<Double> params = sort(c);
        Double max = Double.MIN_VALUE;
        Double min = Double.MAX_VALUE;
        Double range = 0.0;
        for (Double d : params) {
            if (d > max) {
                max = d;
            }
            if (d < min) {
                min = d;
            }
        }
        return max - min;
    }

    public Double computeMax(Column<? extends Number> c) {
        Double result = Double.MIN_VALUE;
        for (Number n : c) {
            if (n != null && n.doubleValue() > result) {
                result = n.doubleValue();
            }
        }
        return result;
    }

    public Double computeMin(Column<? extends Number> c) {
        Double result = Double.MAX_VALUE;
        for (Number n : c) {
            if (n != null && n.doubleValue() < result) {
                result = n.doubleValue();
            }
        }
        return result;
    }

    private DescriptiveStatisticValue createDSV(StatisticalAnalysisOperation operation, String value) {
        DescriptiveStatisticValue dsv = new DescriptiveStatisticValue();
        dsv.setValue(value);
        dsv.setDescriptiveStatistic(operation.getStatistic().getClass().getSimpleName());
        dsv.setDatasetSpecification(operation.getStatistic().getDatasetSpecification());
        return dsv;
    }

    private DescriptiveStatisticValue createDSV(StatisticalAnalysisOperation operation, double value) {
        return createDSV(operation, String.valueOf(value));
    }

    private List<? extends Comparable> order(Column<? extends Comparable> params) {
        List<? extends Comparable> result = new ArrayList<Comparable>(params);
        Collections.sort(result);
        return result;
    }        
    
    public List<Double> sort(Column<? extends Number> params)
    {
        List<Double> result=new ArrayList<>(params.size());
        for(Number n:params)
            result.add(n.doubleValue());
        Collections.sort(result);
        return result;
    }

    @Override
    public Class<? extends Statistic> supportedStatistic() {
        return DescriptiveStatistic.class;
    }
}
