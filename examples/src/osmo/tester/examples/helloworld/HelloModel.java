package osmo.tester.examples.helloworld;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

/** @author Teemu Kanstren */
public class HelloModel {
  @Variable
  private int helloCount = 0;
  @Variable
  private int worldCount = 0;
  private ValueSet<String> names = new ValueSet<>("teemu", "bob");
  private ValueSet<String> worlds = new ValueSet<>("mars", "venus");
  //  private Text words = new Text(3, 7);
  private ValueSet<Integer> sizes = new ValueSet<>(1, 2, 6);
  private ValueRange<Double> ranges = new ValueRange<>(0.1d, 5.2d);

  @BeforeSuite
  public void init() {
    names.setStrategy(DataGenerationStrategy.BALANCING);
  }

  @BeforeTest
  public void startTest() {
    helloCount = 0;
    worldCount = 0;
    System.out.println("TEST START");
  }

  @AfterTest
  public void endTest() {
    System.out.println("TEST END");
  }

  @Guard("hello")
  public boolean thisNameReallyIsIrrelevant() {
    return helloCount == worldCount;
  }

  @TestStep("hello")
  public void sayHello() {
    System.out.println("HELLO " + names.next() + " (" + sizes.next() + ")");
    helloCount++;
  }

  @Guard("world")
  public boolean thisNameIsIrrelevant() {
    return helloCount > worldCount;
  }

  @TestStep("world")
  public void sayWorld() {
    System.out.println("WORLD " + worlds.next() + " (" + ranges.next() + ")");
    worldCount++;
  }
}
