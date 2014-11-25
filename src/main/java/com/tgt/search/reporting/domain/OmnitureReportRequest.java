package com.tgt.search.reporting.domain;

import java.util.Date;
import java.util.List;

/**
 * This is the central request object for interacting with Omniture
 */
public class OmnitureReportRequest {

  private String reportSuiteID;
  private DateGranularity granularity;
  private Date dateFrom;
  private Date dateTo;
  private String sortBy;
  private List<OmnitureRequestMetric> metrics;
  private List<OmnitureRequestElement> elements;
  private List<String> segmentIds;
  private int limit = -1;

  public String getReportSuiteID() {
    return reportSuiteID;
  }

  public void setReportSuiteID(String reportSuiteID) {
    this.reportSuiteID = reportSuiteID;
  }

  public DateGranularity getGranularity() {
    return granularity;
  }

  public void setGranularity(DateGranularity granularity) {
    this.granularity = granularity;
  }

  public Date getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(Date dateFrom) {
    this.dateFrom = dateFrom;
  }

  public Date getDateTo() {
    return dateTo;
  }

  public void setDateTo(Date dateTo) {
    this.dateTo = dateTo;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public List<OmnitureRequestMetric> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<OmnitureRequestMetric> metrics) {
    this.metrics = metrics;
  }

  public List<OmnitureRequestElement> getElements() {
    return elements;
  }

  public void setElements(List<OmnitureRequestElement> elements) {
    this.elements = elements;
  }

  public List<String> getSegmentIds() {
    return segmentIds;
  }

  public void setSegmentIds(List<String> segmentIds) {
    this.segmentIds = segmentIds;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}