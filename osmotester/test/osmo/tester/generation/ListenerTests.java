package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.model.Requirements;
import osmo.tester.testmodels.ValidTestModel1;
import osmo.tester.testmodels.ValidTestModel2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ListenerTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @Test
  public void noEnabledTransition() {
    listener.addExpected("suite-start", "start", "g:epixx", "g:world", "end", "suite-end");
    osmo.addModelObject(new ValidTestModel1());
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Generation without available transitions should fail.");
    } catch (IllegalStateException e) {
      assertEquals("No transition available.", e.getMessage());
    }
    listener.validate("No enabled transitions, generated sequence");
  }

  @Test
  public void generateTestModel2() {
    listener.addExpected("suite-start");
    listener.addExpected("start", "g:epixx", "g:hello", "g:world", "t:hello");
    listener.addExpected("g:epixx", "g:hello", "g:world", "t:world");
    listener.addExpected("g:epixx", "g:hello", "g:world", "pre:epixx", "t:epixx", "post:epixx", "end");
    listener.addExpected("suite-end");
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream ps = new PrintStream(out);
    osmo.addModelObject(new ValidTestModel2(new Requirements(), ps));
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    listener.validate("Generated sequence for test model 2");
  }
}
