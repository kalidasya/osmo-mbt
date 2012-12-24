package osmo.miner.testminer.testcase;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Step {
  private static final Logger log = new Logger(Step.class);
  private final TestCase parent;
  private final String name;
  private Map<String, String> variables = new HashMap<>();
  private Map<String, Step> subSteps = new HashMap<>();

  public Step(TestCase parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  public TestCase getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public void addVariable(String name, String value) {
    if (!variables.containsKey(name)) {
      variables.put(name, value);
    }
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public void merge(TestCase with) {
    Map<String, String> map = with.getVariables();
    for (String name : map.keySet()) {
      addVariable(name, map.get(name));
    }

    List<Step> programSteps = with.getSteps();
    for (Step programStep : programSteps) {
      String name = programStep.getName();
      Step step = subSteps.get(name);
      if (step == null) {
        step = new Step(parent, name);
        subSteps.put(name, step);
      }
      for (String vName : programStep.variables.keySet()) {
        step.addVariable(vName, programStep.variables.get(vName));
      }
    }
  }

  public List<Step> getSubSteps() {
    List<Step> steps = new ArrayList<>();
    steps.addAll(subSteps.values());
    return steps;
  }

  public Step deepCopy(TestCase parent) {
    Step copy = new Step(parent, name);
    for (String name : variables.keySet()) {
      copy.addVariable(name, variables.get(name));
    }
    return copy;
  }
}
