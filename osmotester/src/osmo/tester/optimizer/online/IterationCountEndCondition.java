package osmo.tester.optimizer.online;

/**
 * Ends search after a given set of iterations.
 *
 * @author Teemu Kanstren
 */
public class IterationCountEndCondition implements SearchEndCondition {
  private final int maxIterations;

  public IterationCountEndCondition(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  @Override
  public boolean shouldEnd(SearchState state) {
    return state.getIterationCount() >= maxIterations;
  }
}
