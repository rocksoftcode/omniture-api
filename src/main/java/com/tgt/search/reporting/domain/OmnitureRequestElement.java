package com.tgt.search.reporting.domain;

import java.util.Map;
import java.util.List;

/**
 * Represents a specific aspect of your request to Omniture.
 */
public class OmnitureRequestElement {
  private String id;
  private int limit = -1;
  private int startingWith = 0;
  private Map<String, List<String>> elementTypeAndKeywordFilter;
  private String classification;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getStartingWith() {
    return startingWith;
  }

  public void setStartingWith(int startingWith) {
    this.startingWith = startingWith;
  }

  public Map<String, List<String>> getElementTypeAndKeywordFilter() {
    return elementTypeAndKeywordFilter;
  }

  public void setElementTypeAndKeywordFilter(Map<String, List<String>> elementTypeAndKeywordFilter) {
    this.elementTypeAndKeywordFilter = elementTypeAndKeywordFilter;
  }

  public String getClassification() {
    return classification;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }
}
