package com.google.sps;

import com.google.sps.data.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for Pair class */
@RunWith(JUnit4.class)
public class PairTest {

  @Test
  public void testEqualsTrue() {
    Pair<String, Integer> pair1 = new Pair("apple", 1);
    Pair<String, Integer> pair2 = new Pair("apple", 1);

    Assert.assertTrue(pair1.equals(pair2));
  }

  @Test
  public void testEqualsFalse() {
    Pair<String, String> pair1 = new Pair("apple", "banana");
    Pair<String, String> pair2 = new Pair("apple", "apple");

    Assert.assertFalse(pair1.equals(pair2));
  }
}
