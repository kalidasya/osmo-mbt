package osmo.tester.scripting.slicing;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.model.dataflow.ValueSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for a model slicing configured test generation session.
 *
 * @author Teemu Kanstren
 */
public class SlicingConfiguration {
  /** List of names for test steps that need to be covered. */
  private final Map<String, StepRequirement> stepRequirements = new HashMap<>();
  /** List of data values for variables that needs to be covered. */
  private final List<DataCoverageRequirement> dataRequirements = new ArrayList<>();
  /** The object providing model objects. */
  private String modelFactory = null;
  /** The test generation algorithm. */
  private String algorithm = null;
  /** The random seed for OSMOTester. If not set (null), defaults from OSMOConfiguration are used. */
  private Long seed = null;
  /** Allows the slicer to define the value options for a variable to be used. */
  private Map<String, ValueSet<String>> values = new HashMap<>();
  /** If the user wants to add a listener, this is the place to do it. */
  private GenerationListener listener = null;

  public Long getSeed() {
    return seed;
  }

  /**
   * Set the random generation seed.
   *
   * @param seed The seed.
   */
  public void setSeed(long seed) {
    this.seed = seed;
  }

  public GenerationListener getListener() {
    return listener;
  }

  public void setListener(GenerationListener listener) {
    this.listener = listener;
  }

  /**
   * Add minimum number for how many times a step needs to be taken.
   *
   * @param step The step (transition) name.
   * @param min  The minimum number it must be taken.
   */
  public void addStepMin(String step, int min) {
    StepRequirement req = getStepRequirement(step);
    req.setMin(min);
  }

  /**
   * Add maximum number for how many times a step can be taken.
   *
   * @param step The step (transition) name.
   * @param max  The maximum number it can be taken.
   */
  public void addStepMax(String step, int max) {
    StepRequirement req = getStepRequirement(step);
    req.setMax(max);
  }

  private StepRequirement getStepRequirement(String step) {
    StepRequirement req = stepRequirements.get(step);
    if (req == null) {
      req = new StepRequirement(step);
      stepRequirements.put(step, req);
    }
    return req;
  }

  public void add(DataCoverageRequirement req) {
    dataRequirements.add(req);
  }

  public Collection<StepRequirement> getStepRequirements() {
    return stepRequirements.values();
  }

  public List<DataCoverageRequirement> getDataRequirements() {
    return dataRequirements;
  }

  public boolean hasRequiments() {
    return dataRequirements.size() > 0 || stepRequirements.size() > 0;
  }

  public String getModelFactory() {
    return modelFactory;
  }

  public void setModelFactory(String modelFactory) {
    this.modelFactory = modelFactory;
  }

  /**
   * Sets the test generation algorithm. Can be custom, in which case it needs to be a fully qualified class name.
   * For built-in algorithms, it can be the fully-qualified name or "random"/"weighted random"/"optimized random".
   *
   * @param algorithm The name of the algorithm to use.
   */
  public void setAlgorithm(String algorithm) {
    if (algorithm.equalsIgnoreCase("random")) {
      algorithm = RandomAlgorithm.class.getName();
    }
    if (algorithm.equalsIgnoreCase("weighted random") || algorithm.equalsIgnoreCase("weighted-random") || algorithm.equalsIgnoreCase("weightedrandom")) {
      algorithm = WeightedRandomAlgorithm.class.getName();
    }
    if (algorithm.equalsIgnoreCase("less random") || algorithm.equalsIgnoreCase("less-random") || algorithm.equalsIgnoreCase("lessrandom")) {
      algorithm = WeightedBalancingAlgorithm.class.getName();
    }
    this.algorithm = algorithm;
  }

  public String getAlgorithm() {
    if (algorithm == null || algorithm.length() == 0) {
      setAlgorithm("random");
    }
    return algorithm;
  }

  public Map<String, ValueSet<String>> getValues() {
    return values;
  }

  public void addValue(String name, String serialized) {
    ValueSet<String> set = values.get(name);
    if (set == null) {
      set = new ValueSet<>();
      values.put(name, set);
    }
    set.add(serialized);
  }

  /** Validate the overall parsed configuration. */
  public void validate() {
    String errors = "";
    String warnings = "";
    if (!hasRequiments() && values.size() == 0) {
      warnings += "Warning:Input does not define any valid coverage requirements (steps or variables) or script.";
    }
    if (modelFactory == null) {
      errors += "Input does not define model object factory.";
    }
    if (errors.length() > 0) {
      throw new IllegalArgumentException(errors);
    }
    if (warnings.length() > 0) {
      System.out.println(warnings);
    }
  }
}
