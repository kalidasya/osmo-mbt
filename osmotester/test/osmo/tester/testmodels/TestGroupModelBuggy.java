package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

/** @author Tamas Kende */
public class TestGroupModelBuggy {

  @Guard("groupName")
  public boolean guard() {
    return false;
  }

  @Guard("testWithGroup2")
  public boolean guard2() {
    return false;
  }

  @Guard("testWithoutGroup")
  public boolean guard3() {
    return false;
  }

  @TestStep(name = "testWithGroup", group = "groupName")
  public void testStep() {

  }

  @TestStep(name = "testWithGroup2", group = "groupName")
  public void testStep2() {

  }

  @TestStep(name = "testWithoutGroup")
  public void testStep3() {

  }

  /**
   * Because this teststep use the name of the group, the group guard will only belong to this
   */
  @TestStep(name = "groupName")
  public void buggy() {

  }
}
