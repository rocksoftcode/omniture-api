package com.tgt.search.reporting.domain;

import java.util.List;

/**
 * Represents a unit of data as it is returned from Omniture.  It can contain a nested list of items, depending upon the data returned from Omniture.
 */
public class OmnitureReportDatum {

  private Integer year;
  private Integer month;
  private Integer day;
  private Integer hour;

  private String name;
  private String url;
  private List<Number> counts;

  private List<OmnitureReportDatum> items;

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public Integer getHour() {
    return hour;
  }

  public void setHour(Integer hour) {
    this.hour = hour;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<Number> getCounts() {
    return counts;
  }

  public void setCounts(List<Number> counts) {
    this.counts = counts;
  }

  public List<OmnitureReportDatum> getItems() {
    return items;
  }

  public void setItems(List<OmnitureReportDatum> items) {
    this.items = items;
  }
}
