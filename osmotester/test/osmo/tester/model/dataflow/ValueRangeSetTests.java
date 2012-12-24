package osmo.tester.model.dataflow;

import org.junit.Test;
import osmo.tester.OSMOTester;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ValueRangeSetTests {
  @Test
  public void minMaxTest() {
    ValueRangeSet ni = new ValueRangeSet();
    try {
      ni.addPartition(10d, 0d);
      fail("Wrong order of min/max should throw exception.");
    } catch (Exception e) {
      //expected
    }
  }

  @Test
  public void separatePositivePartitionsWithLoop() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingPositivePartitionsWithLoop() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(50d, 200d);
    ni.addPartition(150d, 300d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 50 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 150 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void overlappingNegativePartitionsWithLoop() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partition 1 (-10-100) was " + d1, d1 >= -10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (-200-(-50)) was " + d2, d2 <= -50 && d2 >= -200);
    assertTrue("Generated value 3 should be in partition 3 (-150-0) was " + d3, d3 >= -150 && d3 <= 0);
    assertTrue("Generated value 4 should be in partition 4 (-10-100) was " + d4, d4 >= -10 && d4 <= 100);
    assertFalse("Number generation should be random, appears not (repeat to be sure): generated value was the same twice (" + d4 + ")", d1 == d4);
  }

  @Test
  public void randomInput() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(-10d, 100d);
    ni.addPartition(-200d, -50d);
    ni.addPartition(-150d, 0d);
    ni.setStrategy(DataGenerationStrategy.RANDOM);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partitions 1-3 (-10-100, -50-(-200) was " + d1, (d1 >= -10 && d1 <= 100) || (d1 <= 0 && d1 >= -200));
    assertTrue("Generated value 2 should be in partitions 1-3 (-10-100, -50-(-200) was " + d2, (d2 >= -10 && d2 <= 100) || (d2 <= 0 && d2 >= -200));
    assertTrue("Generated value 3 should be in partitions 1-3 (-10-100, -50-(-200) was " + d3, (d3 >= -10 && d3 <= 100) || (d3 <= 0 && d3 >= -200));
    assertTrue("Generated value 4 should be in partitions 1-3 (-10-100, -50-(-200) was " + d4, (d4 >= -10 && d4 <= 100) || (d4 <= 0 && d4 >= -200));
  }

  @Test
  public void optimizedRandomInput() {
    OSMOTester osmo = new OSMOTester();
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.setStrategy(DataGenerationStrategy.BALANCING);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    int p1 = findInPartitions(d1, 10d, 100d, 150d, 200d, 250d, 300d);
    int p2 = findInPartitions(d2, 10d, 100d, 150d, 200d, 250d, 300d);
    int p3 = findInPartitions(d3, 10d, 100d, 150d, 200d, 250d, 300d);
    int p4 = findInPartitions(d4, 10d, 100d, 150d, 200d, 250d, 300d);
    assertTrue("Generated values should cover all partitions now: " + p1 + ", " + p2 + ", " + p3, p1 != p2 && p1 != p3 && p2 != p3);
    assertTrue("Overflowing generated value should be in one of the already covered partitions.", p4 == p1 || p4 == p2 || p4 == p3);

  }

  private int findInPartitions(double value, double min1, double max1, double min2, double max2, double min3, double max3) {
    if (value >= min1 && value <= max1) {
      return 1;
    }
    if (value >= min2 && value <= max2) {
      return 2;
    }
    if (value >= min3 && value <= max3) {
      return 3;
    }
    fail("Generated value is not in any given partition.");
    //this is in practice never executed
    return -1;
  }

  @Test
  public void addRemovePartitions() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.addPartition(101d, 120d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    double d1 = ni.nextDouble();
    double d2 = ni.nextDouble();
    double d3 = ni.nextDouble();
    double d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 4 (101-120) was " + d4, d4 >= 101 && d4 <= 120);
    ni.removePartition(101d, 120d);

    d1 = ni.nextDouble();
    d2 = ni.nextDouble();
    d3 = ni.nextDouble();
    d4 = ni.nextDouble();
    assertTrue("Generated value 1 should be in partition 1 (10-100) was " + d1, d1 >= 10 && d1 <= 100);
    assertTrue("Generated value 2 should be in partition 2 (150-200) was " + d2, d2 >= 150 && d2 <= 200);
    assertTrue("Generated value 3 should be in partition 3 (250-300) was " + d3, d3 >= 250 && d3 <= 300);
    assertTrue("Generated value 4 should be in partition 1 (10-100) was " + d4, d4 >= 10 && d4 <= 100);
  }

  @Test
  public void zeroPartitions() {
    ValueRangeSet ni = new ValueRangeSet();
    try {
      double d1 = ni.nextDouble();
      fail("Zero partitions should fail generation.");
    } catch (IllegalStateException e) {
      assertTrue("Zero partitions should fail generation.", e.getMessage().startsWith("No partitions defined"));
    }
  }

  @Test
  public void evaluationWithOnePartition() {
    OSMOTester osmo = new OSMOTester();
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    //TODO: test data generation with boundaries for partitions
    assertInvariantDoesNotContain(1d, ni);
    assertInvariantDoesNotContain(9d, ni);
    assertInvariantContains(10d, ni);
    assertInvariantContains(11d, ni);
    assertInvariantContains(50d, ni);
    assertInvariantContains(99d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantDoesNotContain(101d, ni);
  }

  public void assertInvariantContains(double value, ValueRangeSet di) {
    assertTrue("Value " + value + " should be contained in partition(s):" + di, di.evaluate(value));
  }

  public void assertInvariantDoesNotContain(double value, ValueRangeSet di) {
    assertFalse("Value " + value + " should not be contained in partition(s):" + di, di.evaluate(value));
  }

  @Test
  public void evaluationWithThreePartitions() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    assertInvariantContains(10d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantContains(150d, ni);
    assertInvariantContains(200d, ni);
    assertInvariantContains(250d, ni);
    assertInvariantContains(275d, ni);
    assertInvariantContains(300d, ni);

    assertInvariantDoesNotContain(9.9d, ni);
    assertInvariantDoesNotContain(0d, ni);
    assertInvariantDoesNotContain(100.1d, ni);
    assertInvariantDoesNotContain(149.9d, ni);
    assertInvariantDoesNotContain(145d, ni);
    assertInvariantDoesNotContain(200.1d, ni);
    assertInvariantDoesNotContain(249.9d, ni);
    assertInvariantDoesNotContain(300.1d, ni);
    assertInvariantDoesNotContain(400d, ni);
    assertInvariantDoesNotContain(-400d, ni);
  }

  @Test
  public void evaluationWithOverlappingPartitions() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(10d, 100d);
    ni.addPartition(150d, 200d);
    ni.addPartition(250d, 300d);
    ni.addPartition(50d, 120d);
    assertInvariantContains(10d, ni);
    assertInvariantContains(100d, ni);
    assertInvariantContains(119.9d, ni);
    assertInvariantContains(120d, ni);
    assertInvariantContains(150d, ni);
    assertInvariantContains(200d, ni);
    assertInvariantContains(250d, ni);
    assertInvariantContains(275d, ni);
    assertInvariantContains(300d, ni);

    assertInvariantDoesNotContain(9.9d, ni);
    assertInvariantDoesNotContain(0d, ni);
    assertInvariantDoesNotContain(120.1d, ni);
    assertInvariantDoesNotContain(149.9d, ni);
    assertInvariantDoesNotContain(145d, ni);
    assertInvariantDoesNotContain(200.1d, ni);
    assertInvariantDoesNotContain(249.9d, ni);
    assertInvariantDoesNotContain(300.1d, ni);
    assertInvariantDoesNotContain(400d, ni);
    assertInvariantDoesNotContain(-400d, ni);
  }

  @Test
  public void intGenerationWithSingleValue() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(1d, 1d);
    ni.addPartition(11d, 11d);
    ni.addPartition(-22d, -22d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    int d1 = ni.nextInt();
    int d2 = ni.nextInt();
    int d3 = ni.nextInt();
    int d4 = ni.nextInt();
    assertEquals("Generated value 1", 1, d1);
    assertEquals("Generated value 2", 11, d2);
    assertEquals("Generated value 3", -22, d3);
    assertEquals("Generated value 4", 1, d4);
  }

  @Test
  public void intGenerationWithTwoValues() {
    ValueRangeSet ni = new ValueRangeSet();
    ni.addPartition(1d, 2d);
    ni.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    boolean b1 = false;
    boolean b2 = false;
    for (int i = 0; i < 100; i++) {
      int n = ni.nextInt();
      if (n == 1) {
        b1 = true;
      }
      if (n == 2) {
        b2 = true;
      }
    }
    assertTrue("Should generate value 1", b1);
    assertTrue("Should generate value 2", b2);
  }

  @Test
  public void generics() {
    ValueRangeSet<Integer> vrs1 = new ValueRangeSet<>();
    vrs1.addPartition(1, 2);
    Integer o1 = vrs1.next();
    assertEquals("Integer value range should produce integers..", Integer.class, o1.getClass());

    ValueRangeSet<Long> vrs2 = new ValueRangeSet<>();
    vrs2.addPartition(Long.class, 1, 2);
    Long o2 = vrs2.next();
    assertEquals("Long value range should produce longs..", Long.class, o2.getClass());

    ValueRangeSet<Long> vrs3 = new ValueRangeSet<>();
    vrs3.addPartition(1l, 2l);
    Long o3 = vrs3.next();
    assertEquals("Long value range should produce longs..", Long.class, o3.getClass());

    ValueRangeSet<Double> vrs4 = new ValueRangeSet<>();
    vrs4.addPartition(Double.class, 1, 2);
    Double o4 = vrs4.next();
    assertEquals("Double value range should produce doubles..", Double.class, o4.getClass());

    ValueRangeSet<Double> vrs5 = new ValueRangeSet<>();
    vrs5.addPartition(1d, 2d);
    Double o5 = vrs5.next();
    assertEquals("Double value range should produce doubles..", Double.class, o5.getClass());
  }

  @Test
  public void boundaryScanningInteger() {
    ValueRangeSet<Integer> vr = new ValueRangeSet<>();
    vr.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    vr.setPartitionStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    vr.addPartition(0, 100);
    vr.addPartition(-100, -50);
    vr.addPartition(200, 300);
    vr.addPartition(-300, -200);
    Collection<Integer> actual = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      actual.add(vr.next());
    }
    String expected = "[0, -100, 200, -300, 100, -50, 300, -200, 1, -99, 201, -299, 99, -51, 299, -201, 2, -98, 202, -298, 98, -52, 298, -202, 3, -97, 203, -297, 97, -53]";
    assertEquals("Generated integers for value range with boundary scan", expected, actual.toString());
  }

  @Test
  public void boundaryScanningInvalidInteger() {
    ValueRangeSet<Integer> vr = new ValueRangeSet<>();
    vr.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    vr.setPartitionStrategy(DataGenerationStrategy.BOUNDARY_SCAN_INVALID);
    vr.addPartition(0, 100);
    vr.addPartition(-100, -50);
    vr.addPartition(200, 300);
    vr.addPartition(-300, -200);
    Collection<Integer> actual = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      actual.add(vr.next());
    }
    String expected = "[101, -49, 301, -199, -1, -101, 199, -301, 102, -48, 302, -198, -2, -102, 198, -302, 103, -47, 303, -197, -3, -103, 197, -303, 104, -46, 304, -196, -4, -104]";
    assertEquals("Generated integers for value range with boundary scan", expected, actual.toString());
  }

  @Test
  public void boundaryScanningFloat() {
    ValueRangeSet<Double> vr = new ValueRangeSet<>();
    vr.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    vr.setPartitionStrategy(DataGenerationStrategy.BOUNDARY_SCAN);
    vr.addPartition(0f, 100f);
    vr.addPartition(-100f, -50f);
    vr.addPartition(200f, 300f);
    vr.addPartition(-300f, -200f);
    vr.setIncrement(0.1f);
    //the valuerangeset actually converts float to double
    double[] expected = new double[]{0, -100, 200, -300, 100, -50, 300, -200, 0.1, -99.9, 200.1, -299.9, 99.9, -50.1, 299.9, -200.1, 0.2, -99.8, 200.2, -299.8, 99.8, -50.2, 299.8, -200.2, 0.3, -99.7, 200.3, -299.7, 99.7, -50.3};
    for (int i = 0; i < 30; i++) {
      assertEquals("Generated integers for value range with boundary scan (index " + i + ")", expected[i], vr.next(), 0.01d);
    }
  }


  private void assertValues(ValueRangeSet<Double> range, double... expected) {
    for (double i : expected) {
      assertEquals(i, range.next());
    }
  }
}
