package com.tgt.search.reporting.domain;

import java.util.List;

public class OmnitureReport {
  private String type;
  private String period;
  private String reportSuite;

  private List<OmnitureReportElement> elements;
  private List<OmnitureReportElement> metrics;

  private List<OmnitureReportDatum> data;

  private Double runTime;
  private Double waitTime;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public String getReportSuite() {
    return reportSuite;
  }

  public void setReportSuite(String reportSuite) {
    this.reportSuite = reportSuite;
  }

  public List<OmnitureReportElement> getElements() {
    return elements;
  }

  public void setElements(List<OmnitureReportElement> elements) {
    this.elements = elements;
  }

  public List<OmnitureReportElement> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<OmnitureReportElement> metrics) {
    this.metrics = metrics;
  }

  public List<OmnitureReportDatum> getData() {
    return data;
  }

  public void setData(List<OmnitureReportDatum> data) {
    this.data = data;
  }

  public Double getRunTime() {
    return runTime;
  }

  public void setRunTime(Double runTime) {
    this.runTime = runTime;
  }

  public Double getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(Double waitTime) {
    this.waitTime = waitTime;
  }
}
