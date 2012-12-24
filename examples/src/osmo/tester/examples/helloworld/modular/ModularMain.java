package osmo.tester.examples.helloworld.modular;

import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;

/** @author Teemu Kanstren */
public class ModularMain {
  public static void main(String[] args) {
    SeparateState state = new SeparateState();
    OSMOTester tester = new OSMOTester();
    tester.addModelObject(new HelloModule(state));
    tester.addModelObject(new WorldModule(state));
    tester.setSeed(345);
    tester.addTestEndCondition(new Length(5));
    tester.addSuiteEndCondition(new Length(3));
    tester.generate();
  }
}
