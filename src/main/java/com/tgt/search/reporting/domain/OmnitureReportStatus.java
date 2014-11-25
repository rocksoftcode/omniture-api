package com.tgt.search.reporting.domain;

import java.util.Date;

public class OmnitureReportStatus {
  private String status;
  private Date queueTime;
  private String reportType;
  private Long resultSize;
  private int errorCode;
  private String errorMessage;

  public boolean isComplete() {
    return "done".equals(status);
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  };

  public Date getQueueTime() {
    return queueTime;
  }

  public void setQueueTime(Date queueTime) {
    this.queueTime = queueTime;
  }

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  public Long getResultSize() {
    return resultSize;
  }

  public void setResultSize(Long resultSize) {
    this.resultSize = resultSize;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
