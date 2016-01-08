/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.analysis.operations.information.computestats.engine;

import es.us.isa.jdataset.DataSet;
import es.us.isa.jdataset.loader.CSVLoader;
import es.us.isa.sedl.core.analysis.statistic.DescriptiveStatistic;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;

/**
 *
 * @author José Antonio Parejo
 */
public class DescriptiveStatisticsComputingEngineTest extends TestCase {

    public DescriptiveStatisticsComputingEngineTest(String testName) {
        super(testName);
    }

    /**
     * Test of isSupported method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testIsSupported() {
        DescriptiveStatisticsComputingEngine engine = new DescriptiveStatisticsComputingEngine();
        Reflections reflections = new Reflections("es.us.isa.sedl.core.analysis.statistic");
        Set<Class<? extends DescriptiveStatistic>> descStatsClasses = reflections.getSubTypesOf(DescriptiveStatistic.class);
        for (Class<? extends DescriptiveStatistic> descStatClass : descStatsClasses) {
            DescriptiveStatistic ds = instantiate(descStatClass);            
            if(ds!=null)
                assertTrue(engine.isSupported(ds));
        }

    }

    /**
     * Test of compute method, of class DescriptiveStatisticsComputingEngine.
     */
    public void testCompute() throws Exception {
    }

    /**
     * Test of computeMean method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMean() {
        String integerData =    "height\n"
                                +"5\n"
                                +"4";
        String doubleData =    "height\n"
                                +"1.60\n"
                                +"1.90";
        DescriptiveStatisticsComputingEngine engine = new DescriptiveStatisticsComputingEngine();
        CSVLoader loader = new CSVLoader(true);
        InputStream is = IOUtils.toInputStream(integerData);
        try {
            DataSet integerDataset = loader.load(is, "csv");
            Double result=engine.computeMean(integerDataset.<Integer>getColumn("height"));
            Double expectedResult=4.5;
            assertEquals(result,expectedResult);
        } catch (IOException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        is = IOUtils.toInputStream(doubleData);
        try {
            DataSet doubleDataset = loader.load(is, "csv");
            Double result=engine.computeMean(doubleDataset.<Double>getColumn("height"));
            Double expectedResult=1.75;
            assertEquals(result,expectedResult);
        } catch (IOException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of computeMedian method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMedian() {
    }

    /**
     * Test of computeMode method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMode_StatisticalAnalysisOperation() {
    }

    /**
     * Test of computeMode method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMode_Column() {
    }

    /**
     * Test of computeStandardDesviation method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeStandardDesviation() {
    }

    /**
     * Test of computeStandartdDeviation method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeStandartdDeviation() {
    }

    /**
     * Test of computeVariance method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeVariance_StatisticalAnalysisOperation() {
    }

    /**
     * Test of computeVariance method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeVariance_Column() {
    }

    /**
     * Test of computeIQR method, of class DescriptiveStatisticsComputingEngine.
     */
    public void testComputeIQR() {
    }

    /**
     * Test of computeRange method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeRange_StatisticalAnalysisOperation() {
    }

    /**
     * Test of computeRange method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testComputeRange_Column() {
    }

    /**
     * Test of computeMax method, of class DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMax() {
    }

    /**
     * Test of computeMin method, of class DescriptiveStatisticsComputingEngine.
     */
    public void testComputeMin() {
    }

    /**
     * Test of sort method, of class DescriptiveStatisticsComputingEngine.
     */
    public void testSort() {
    }

    /**
     * Test of supportedStatistic method, of class
     * DescriptiveStatisticsComputingEngine.
     */
    public void testSupportedStatistic() {
    }

    private <T> T instantiate(Class<? extends T> descStatClass) {
        T result = null;
        Constructor constructor;
        try {
            constructor = descStatClass.getConstructor();

            if (constructor != null) {
                result = (T) constructor.newInstance();
            }
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DescriptiveStatisticsComputingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
