package osmo.tester.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class PartialModel2 {
  @RequirementsField
  private final Requirements req;
  @TestSuiteField
  private final TestSuite history;
  public static final String REQ_HELLO = "hello";
  public static final String REQ_WORLD = "world";
  public static final String REQ_EPIX = "epix";
  private PrintStream out;

  public PartialModel2(Requirements req, TestSuite suite) {
    this(req, null, suite);
  }

  public PartialModel2(Requirements req, PrintStream out, TestSuite suite) {
    this.req = req;
    this.out = out;
    this.history = suite;
  }

  @BeforeSuite
  public void beforeAll() {
    if (out == null) {
      out = System.out;
    }
  }

  @AfterSuite
  public void endAll() {
  }

  @BeforeTest
  public void start2() {
  }

  @AfterTest
  public void end2() {
  }

  @Guard("world")
  public boolean worldCheck() {
    return req.isCovered(REQ_HELLO) && !req.isCovered(REQ_WORLD) && !req.isCovered(REQ_EPIX);
  }

  @Transition("world")
  public void epix() {
    req.covered(REQ_WORLD);
    out.print(":world");
  }

  @Transition("epixx")
  public void epixx() {
    req.covered(REQ_EPIX);
    out.print(":epixx");
  }

  @Post({"hello", "world"})
  public void sharedCheck() {
    out.print(":two_oracle");
  }

  @EndCondition
  public boolean ec2() {
    return false;
  }
}
