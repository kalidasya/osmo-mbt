package osmo.tester.scripter.robotframework;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates scripts that can be executed with the Robot Framework.
 *
 * @author Teemu Kanstren
 */
public class Scripter {
  /** Test library to be used by Robot Framework */
  private String testLibrary;
  /** For template->script generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();
  /** The test case variables. */
  private Map<String, String> variables = new HashMap<>();
  /** The test cases to be generated. */
  private Collection<RFTestCase> tests = new ArrayList<>();
  /** Test currently being scripted. */
  private RFTestCase currentTest = null;
  /** How many cells should the RF test script table have? Used to fill empty cells and make a nice looking table. */
  private final int cellCount;

  public Scripter(int cellCount) {
    this.cellCount = cellCount;
  }

  public void setTestLibrary(String testLibrary) {
    this.testLibrary = testLibrary;
  }

  /**
   * Adds a variable to the RF script. These are given in a separate table in the script file beginning.
   *
   * @param name  Name of the variable.
   * @param value Value for the variable.
   */
  public void addVariable(String name, String value) {
    variables.put("${" + name + "}", value);
  }

  /**
   * Starts a new test case in the script.
   *
   * @param name Name of the new test case.
   */
  public void startTest(String name) {
    if (currentTest != null) {
      tests.add(currentTest);
    }
    currentTest = new RFTestCase(name, cellCount);
  }

  /**
   * Adds a test step (keyword) into the currently generated test case.
   *
   * @param keyword The keyword for the test step.
   * @param params  The parameters of the test step.
   */
  public void addStep(String keyword, RFParameter... params) {
    currentTest.addStep(keyword, params);
  }

  /**
   * Adds a test step (keyword) into the currently generated test case,
   * with a definition of a variable storing the output of the step.
   *
   * @param keyword      The keyword for the test step.
   * @param variableName The name of the variable to generate in RF for storing the output.
   * @param params       The parameters for the test step.
   */
  public void addStepWithResult(String keyword, String variableName, RFParameter... params) {
    currentTest.addStepWithResult(keyword, variableName, params);
  }

  /**
   * Creates the test script based on the given test cases, test steps, variables, and the template.
   *
   * @return The test (suite) script as text.
   */
  public String createScript() {
    if (!tests.contains(currentTest)) {
      tests.add(currentTest);
    }
    vc.put("library", testLibrary);
    vc.put("argument_headers", getArgumentHeaders());
    vc.put("variables", variables.entrySet());
    vc.put("css", new CSSHelper());
    vc.put("tests", tests);
    velocity.setProperty("resource.loader", "class");
    velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("osmo/tester/scripter/robotframework/script.vm", "UTF8", vc, sw);
    return sw.toString();
  }

  /**
   * Creates the set of table headers for the test case table in order to enable template rendering.
   *
   * @return The Argument headers for the test table, according to the number of cells defined.
   */
  private Collection<String> getArgumentHeaders() {
    Collection<String> headers = new ArrayList<>();
    for (int i = 0; i < cellCount; i++) {
      headers.add("Argument");
    }
    return headers;
  }

}
