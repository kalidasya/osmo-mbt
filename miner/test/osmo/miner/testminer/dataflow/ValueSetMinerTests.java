package osmo.miner.testminer.dataflow;

import org.junit.Before;
import org.junit.Test;
import osmo.miner.testminer.MainTCMiner;
import osmo.miner.testminer.model.general.InvariantCollection;
import osmo.miner.testminer.testcase.Suite;
import osmo.miner.testmodels.TestModels1;

import static junit.framework.Assert.assertEquals;
import static osmo.common.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class ValueSetMinerTests {
  private MainTCMiner main;

  @Before
  public void setup() {
    ValueSetMiner vrMiner = new ValueSetMiner();
    main = new MainTCMiner();
    main.addMiner(vrMiner);
  }

  @Test
  public void invariantsForModel1() {
    Suite suite = TestModels1.model1();
    main.mine(suite);
    InvariantCollection invariants = main.getInvariants();
    String expected = getResource(getClass(), "expected-vs-model1.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Invariants for ValueSet", expected, invariants.toString());
  }

  @Test
  public void setAndRangeForModel1() {
    Suite suite = TestModels1.model1();
    main.addMiner(new ValueRangeMiner());
    main.mine(suite);
    InvariantCollection invariants = main.getInvariants();
    String expected = getResource(getClass(), "expected-vs-vr-model1.txt");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Invariants for ValueSet", expected, invariants.toString());
  }

}
