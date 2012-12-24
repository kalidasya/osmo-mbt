package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Coverage values for a single test case (as opposed to suite in {@link osmo.tester.optimizer.TestCoverage}.
 * Used in reporting for coverage matrix. Number of times different elements were covered or the values for
 * variables.
 *
 * @author Teemu Kanstren
 */
public class SingleTestCoverage {
  /** Test name. */
  private final String name;
  /** Key=requirement name, value=times covered in this test. */
  private final Map<String, Integer> requirementCount = new HashMap<>();
  /** Key=transition name, value=times covered in this test. */
  private final Map<String, Integer> transitionCount = new HashMap<>();
  /** Key=transition pair name ("T1->T2"), value=times covered in this test. */
  private final Map<String, Integer> pairCount = new HashMap<>();
  /** Key=variable name, value=values covered in this test. */
  private final Map<String, Collection<Object>> variableValues = new HashMap<>();

  /**
   * Counts and collects the coverage of the given test.
   *
   * @param tc The test for which to get the coverage.
   */
  public SingleTestCoverage(TestCase tc) {
    this.name = "Test" + tc.getId();
    countRequirements(tc);
    countTransitions(tc);
    collectVariableValues(tc);
  }

  /**
   * Count the number of times different requirements have been covered.
   * 
   * @param tc The test case to count the requirements from..
   */
  private void countRequirements(TestCase tc) {
    Collection<String> covered = tc.getFullRequirementsCoverage();
    for (String req : covered) {
      incrementCountFor(requirementCount, req);
    }
  }

  /**
   * Count the number of times each transition and transition pair has been covered in a given test case.
   * If coverage is 0, nothing is given for that transition/pair.
   * 
   * @param tc The test case for which to get the coverage.
   */
  private void countTransitions(TestCase tc) {
    Collection<String> names = tc.getAllTransitionNames();
    String previous = "init";
    for (String name : names) {
      incrementCountFor(transitionCount, name);
      String pair = previous + "->" + name;
      incrementCountFor(pairCount, pair);
      previous = name;
    }
  }

  /**
   * Increment the count for the given key in the given map.
   * 
   * @param map Where to find the value.
   * @param name The name of the key for the value.
   */
  private void incrementCountFor(Map<String, Integer> map, String name) {
    Integer count = map.get(name);
    if (count == null) {
      count = 0;
    }
    count++;
    map.put(name, count);
  }

  /**
   * Captures all values that different model variables have received in a given test case.
   * 
   * @param tc To get the values from.
   */
  private void collectVariableValues(TestCase tc) {
    Map<String, ModelVariable> variables = tc.getVariables();
    for (String var : variables.keySet()) {
      ModelVariable modelVariable = variables.get(var);
      Collection<Object> values = variableValues.get(var);
      if (values == null) {
        values = new ArrayList<>();
        variableValues.put(var, values);
      }
      values.addAll(modelVariable.getValues());
    }
  }

  public String getName() {
    return name;
  }

  /**
   * Gives the coverage count for the given key.
   * 
   * @param map Where to get the coverage count from.
   * @param name The key name to get the count for.
   * @return The coverage count for the given key.
   */
  private int countFor(Map<String, Integer> map, String name) {
    Integer count = map.get(name);
    if (count == null) {
      count = 0;
    }
    return count;
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given requirement was covered in this test case.
   *
   * @param requirement The name of the requirement to get the count for.
   * @return The times covered in this test case.
   */
  public int requirementCount(String requirement) {
    return countFor(requirementCount, requirement);
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given transition was covered in this test case.
   *
   * @param transition The name of the transition to get the count for.
   * @return The times covered in this test case.
   */
  public int transitionCount(String transition) {
    return countFor(transitionCount, transition);
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given transition pair was covered in this test case.
   *
   * @param pair The name of the transition pair to get the count for.
   * @return The times covered in this test case.
   */
  public int pairCount(String pair) {
    return countFor(pairCount, pair);
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the values that were covered for the given variable in this test case.
   *
   * @param variable The name of the variable to get the values for.
   * @return The values covered in this test case.
   */
  public Collection<Object> variableCoverage(String variable) {
    return variableValues.get(variable);
  }


}
