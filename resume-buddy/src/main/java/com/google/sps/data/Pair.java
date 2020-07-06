package com.google.sps.data;

/**
 * Pair class to store two objects together. Used for testing purposes for reviewee-reviewer pair
 */
public class Pair<K, V> {
  private final K key;
  private final V val;

  public Pair(K key, V val) {
    this.key = key;
    this.val = val;
  }

  public K getKey() {
    return key;
  }

  public V getVal() {
    return val;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    if (this == o) {
      return true;
    }
    Pair<K, V> that = (Pair<K, V>) o;

    return this.key.equals(that.getKey()) && this.val.equals(that.getVal());
  }
}
