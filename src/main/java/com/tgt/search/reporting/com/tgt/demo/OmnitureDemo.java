package com.tgt.search.reporting.com.tgt.demo;

import com.tgt.search.reporting.OmnitureReportTool;
import com.tgt.search.reporting.domain.OmnitureReport;
import com.tgt.search.reporting.domain.OmnitureReportDatum;
import com.tgt.search.reporting.domain.OmnitureReportRequest;

import java.util.Arrays;
import java.util.Date;

public class OmnitureDemo {

  public static void main(String[] args) throws InterruptedException {
    OmnitureReportRequest request = new OmnitureReportRequest();
    request.setDateFrom(new Date(new Date().getTime() - 12345678));
    request.setDateTo(new Date(new Date().getTime() - 12345678));
    request.setSegmentIds(Arrays.asList("seg1", "seg4"));
    request.setReportSuiteID("mycompanysuite");
    String id = OmnitureReportTool.enqueueReport(request);
    Thread.sleep(60000);
    OmnitureReport report = OmnitureReportTool.retrieveReport(Long.valueOf(id));
    System.out.println("Period " + report.getPeriod());
    for (OmnitureReportDatum datum : report.getData()) {
      System.out.println(datum.getName() + ": ");
      for (Number count : datum.getCounts()) {
        System.out.println(count);
      }
    }
  }
}
