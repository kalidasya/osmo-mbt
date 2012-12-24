package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes the test suite being generated.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestSuite {
  /** The current test being generated. */
  private TestCase current = null;
  /** The test cases generated so far, excluding the current test case. */
  private final List<TestCase> testCases = new ArrayList<>();
  /** List of covered transitions and number of how many times it exist in the test suite */
  private Map<FSMTransition, Integer> transitionCoverage = new HashMap<>();
  private boolean shouldEndTest = false;
  private boolean shouldEndSuite = false;

  /** Start a new test case. */
  public void startTest() {
    current = new TestCase();
    current.setStartTime(System.currentTimeMillis());
  }

  /**
   * Resets the suite to avoid memory leaks if test generator is used to produce long test sets,
   * for example, in suite optimization when tests can be generated in phases.
   */
  public void reset() {
    current = null;
    testCases.clear();
    transitionCoverage.clear();
  }

  /** End the current test case and moves it to the suite "history". */
  public void endTest() {
    current.setEndTime(System.currentTimeMillis());
    testCases.add(current);
    current = null;
  }

  /**
   * Adds the given transition as a step into the current test case.
   *
   * @param transition The transition to add.
   * @return The added step object.
   */
  public TestStep addStep(FSMTransition transition) {
    TestStep step = current.addStep(transition);
    Integer count = transitionCoverage.get(transition);
    if (count == null) {
      count = 0;
    }
    transitionCoverage.put(transition, count + 1);
    return step;
  }

  /**
   * Marks the given requirement as covered by the current test case.
   *
   * @param requirement The requirement identifier.
   */
  public void covered(String requirement) {
    current.covered(requirement);
  }

  /**
   * Counts the total number of tests in the test suite and the test case currently being generated.
   *
   * @return The total number of test steps in test suite.
   */
  public int totalSteps() {
    int count = 0;
    for (TestCase test : testCases) {
      count += test.getSteps().size();
    }
    //current is null when suite is initialized but no tests are started
    if (current != null) {
      count += current.getSteps().size();
    }
    return count;
  }

  /**
   * Access to the test case being currently generated.
   *
   * @return The current test case.
   */
  public TestCase getCurrentTest() {
    return current;
  }

  /**
   * Gives the test cases in this test suite. Excludes the currently generated test case (if not yet finished).
   *
   * @return The test cases.
   */
  public List<TestCase> getFinishedTestCases() {
    return testCases;
  }

  /**
   * Gives all test cases in this test suite, including the one being currently generated.
   *
   * @return The test cases.
   */
  public List<TestCase> getAllTestCases() {
    List<TestCase> all = new ArrayList<>(testCases.size() + 1);
    all.addAll(testCases);
    if (current != null) {
      //current is null if we finished test generation
      all.add(current);
    }
    return all;
  }

  /**
   * Gives all transitions in this test suite, including coverage number
   * Coverage number tells how many times transition is covered in this test suite
   *
   * @return The transitions with coverage number
   */
  public Map<FSMTransition, Integer> getTransitionCoverage() {
    return transitionCoverage;
  }

  /**
   * Gives the number of test steps in the current test case.
   *
   * @return The number of test steps in the current test case.
   */
  public int currentSteps() {
    //current is null before starting the first test case
    if (current == null) {
      return 0;
    }
    return current.getSteps().size();
  }

  /**
   * Checks if the given transition is present in any of the previously generated test cases (history+current test case).
   *
   * @param transition The transition to check.
   * @return True if transition is present, false if not.
   */
  public boolean contains(FSMTransition transition) {
    for (TestCase testCase : testCases) {
      if (testContains(testCase, transition)) {
        return true;
      }
    }
    if (current != null && testContains(current, transition)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the given test case contains the given transition.
   *
   * @param testCase   The test case to check.
   * @param transition The transition to check.
   * @return True if the given transition is found in the given test case, otherwise false.
   */
  private boolean testContains(TestCase testCase, FSMTransition transition) {
    List<TestStep> steps = testCase.getSteps();
    for (TestStep step : steps) {
      if (step.getTransition().equals(transition)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Coverage of variables and their values for all test cases in this test suite.
   *
   * @return [variable name, variable coverage] mapping.
   */
  public Map<String, ModelVariable> getVariables() {
    Map<String, ModelVariable> variables = new HashMap<>();
    for (TestCase test : testCases) {
      Map<String, ModelVariable> testVariables = test.getVariables();
      for (ModelVariable testVar : testVariables.values()) {
        String name = testVar.getName();
        ModelVariable var = variables.get(name);
        if (var == null) {
          var = new ModelVariable(name);
          variables.put(name, var);
        }
        var.addAll(testVar);
      }
    }
    return variables;
  }

  public boolean shouldEndTest() {
    return shouldEndTest;
  }

  public boolean shouldEndSuite() {
    return shouldEndSuite;
  }

  public void setShouldEndTest(boolean shouldEndTest) {
    this.shouldEndTest = shouldEndTest;
  }

  public void setShouldEndSuite(boolean shouldEndSuite) {
    this.shouldEndSuite = shouldEndSuite;
  }
}
