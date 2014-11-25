package com.tgt.search.reporting;

import com.tgt.search.reporting.domain.OmnitureReport;
import com.tgt.search.reporting.domain.OmnitureReportDatum;
import com.tgt.search.reporting.domain.OmnitureReportElement;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class allows a report to be retrieved from Omniture.
 */
@Component
public class OmnitureReportRetriever {

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  OmnitureConnectionUtil omnitureConnectionUtil;

  static final String ENDPOINT_URL = "https://api.omniture.com/admin/1.4/rest/?method=Report.Get";

  /**
   * Pulls a report from Adobe's services over HTTP
   * @param reportId Any valid Ominture report ID
   * @return A populated report object
   */
  public OmnitureReport retrieveReportById(Long reportId) {
    String body = "{ \"reportID\": \"" + reportId + "\" }";
    HttpEntity<String> request = new HttpEntity<String>(body, omnitureConnectionUtil.getOmnitureHeaders());
    String response = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, request, String.class).getBody();
    Map parsedResponse = null;
    try {
      parsedResponse = new ObjectMapper().readValue(response, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    OmnitureReport report = new OmnitureReport();
    Map parsedReport = (Map) parsedResponse.get("report");
    report.setType(parsedReport.get("type").toString());
    Map reportSuite = (Map) parsedReport.get("reportSuite");
    report.setReportSuite((String) reportSuite.get("id"));
    report.setPeriod((String)parsedReport.get("period"));
    report.setRunTime(Double.valueOf((String) parsedResponse.get("runSeconds")));
    report.setWaitTime(Double.valueOf((String) parsedResponse.get("waitSeconds")));
    report.setMetrics(getMetrics((List<Map>) parsedReport.get("metrics")));
    report.setElements(getElements((List<Map>) parsedReport.get("elements")));
    report.setData(getData((List<Map>) parsedReport.get("data")));

    return report;
  }

  List<OmnitureReportElement> getMetrics(List<Map> metricsIn) {
    List<OmnitureReportElement> results = new ArrayList<>();
    for (Map metric : metricsIn) {
      OmnitureReportElement row = new OmnitureReportElement();
      row.setId((String) metric.get("id"));
      row.setName((String) metric.get("name"));
      row.setType((String) metric.get("type"));
      row.setDecimalPlaces((Integer) metric.get("decimals"));
      row.setLatency((Integer) metric.get("latency"));
      results.add(row);
    }
    return results;
  }

  List<OmnitureReportElement> getElements(List<Map> elementsIn) {
    List<OmnitureReportElement> results = new ArrayList<>();
    for (Map element : elementsIn) {
      OmnitureReportElement row = new OmnitureReportElement();
      row.setId((String) element.get("id"));
      row.setName((String) element.get("name"));
      results.add(row);
    }
    return results;
  }

  List<OmnitureReportDatum> getData(List<Map> dataIn) {
    List<OmnitureReportDatum> results = new ArrayList<>();
    for (Map datum : dataIn) {
      OmnitureReportDatum row = new OmnitureReportDatum();
      row.setYear(getValueOrNull(datum, "year"));
      row.setMonth(getValueOrNull(datum, "month"));
      row.setDay(getValueOrNull(datum, "day"));
      row.setHour(getValueOrNull(datum, "hour"));
      row.setName((String) datum.get("name"));
      row.setUrl((String) datum.get("url"));
      List<Number> counts = getCounts((List<String>) datum.get("counts"));
      row.setCounts(counts);
      if (datum.containsKey("breakdown")) {
        List<OmnitureReportDatum> items = new ArrayList<>();
        for (Map item : (List<Map>) datum.get("breakdown")) {
          OmnitureReportDatum itemRow = new OmnitureReportDatum();
          itemRow.setName(item.get("name").toString());
          itemRow.setUrl(item.get("url").toString());
          itemRow.setCounts(getCounts((List<String>) item.get("counts")));
          items.add(itemRow);
        }
        row.setItems(items);
      } else {
        OmnitureReportDatum item = new OmnitureReportDatum();
        item.setName((String) datum.get("name"));
        item.setUrl((String) datum.get("url"));
        item.setCounts(getCounts((List<String>) datum.get("counts")));
        row.setItems(Collections.singletonList(item));
      }
      results.add(row);
    }
    return results;
  }

  Integer getValueOrNull(Map datum, String key) {
    if (datum.get(key) != null) {
      return Integer.valueOf(datum.get(key).toString());
    } else {
      return null;
    }
  }

  private List<Number> getCounts(List<String> rawCounts) {
    List<Number> counts = new ArrayList<>();
    if (rawCounts != null) {
      for (String count : rawCounts) {
        if (count.contains(".")) {
          counts.add(new BigDecimal(count));
        } else {
          counts.add(new Long(count));
        }
      }
    }
    return counts;
  }
}