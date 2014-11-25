/**
 * Represents date granularity, with the unit of "hour" being the smallest
 */
package com.tgt.search.reporting.domain;

public enum DateGranularity {
  HOUR("hour"), DAY("day"), WEEK("week"), MONTH("month"), QUARTER("quarter"), YEAR("year");

  private String value;
  DateGranularity(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}