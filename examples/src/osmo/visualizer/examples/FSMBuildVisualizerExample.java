package osmo.visualizer.examples;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.endcondition.Length;
import osmo.visualizer.model.FSMBuildVisualizer;

/** @author Teemu Kanstren */
public class FSMBuildVisualizerExample {
  public static void main(String[] args) {
    FSMBuildVisualizer gv = new FSMBuildVisualizer();
    OSMOTester osmo = new OSMOTester(new CalculatorModel());
    osmo.addTestEndCondition(new Length(15));
    osmo.addSuiteEndCondition(new Length(5));
    osmo.addListener(gv);
    osmo.generate();
  }
}
