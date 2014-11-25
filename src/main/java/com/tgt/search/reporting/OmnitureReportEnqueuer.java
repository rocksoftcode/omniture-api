package com.tgt.search.reporting;

import com.tgt.search.reporting.domain.OmnitureReportRequest;
import com.tgt.search.reporting.domain.OmnitureRequestElement;
import com.tgt.search.reporting.domain.OmnitureRequestMetric;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class OmnitureReportEnqueuer {

  private static final String ENDPOINT_URL = "https://api.omniture.com/admin/1.4/rest/?method=Report.Queue";
  private static final String OMNITURE_REPORT_DATE_FORMAT = "yyyy-MM-dd";

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private OmnitureConnectionUtil omnitureConnectionUtil;

  /**
   * Adds a report to your Omniture queue.
   * @param omnitureReport
   * @return A string representation of your report's ID
   */
  public String enqueueReport(OmnitureReportRequest omnitureReport) {
    DateFormat formatter = new SimpleDateFormat(OMNITURE_REPORT_DATE_FORMAT);
    HttpHeaders headers = omnitureConnectionUtil.getOmnitureHeaders();
    Map<String, Map> requestStructure = new HashMap<>();
    Map reportDescription = new HashMap<>();
    requestStructure.put("reportDescription", reportDescription);
    reportDescription.put("reportSuiteID", omnitureReport.getReportSuiteID());

    String dateFrom = formatter.format(omnitureReport.getDateFrom());
    String dateTo = formatter.format(omnitureReport.getDateTo());
    reportDescription.put("dateFrom", dateFrom);
    reportDescription.put("dateTo", dateTo);
    reportDescription.put("metrics", getMetricsForRequest(omnitureReport.getMetrics()));
    reportDescription.put("sortBy", omnitureReport.getSortBy());
    reportDescription.put("dateGranularity", omnitureReport.getGranularity() != null ? omnitureReport.getGranularity().value() : null);
    reportDescription.put("elements", getElementsForRequest(omnitureReport));
    reportDescription.put("segments", getSegmentsForRequest(omnitureReport.getSegmentIds()));
    if (dateFrom.equals(dateTo)) {
      String date = dateFrom;
      reportDescription.put("date", date);
      reportDescription.remove("dateFrom");
      reportDescription.remove("dateTo");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      objectMapper.writeValue(out, requestStructure);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String payload = out.toString();
    HttpEntity<String> request = new HttpEntity<String>(payload, headers);
    String body = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, request, String.class).getBody();
    Map response = null;
    try {
      response = objectMapper.readValue(body, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return String.valueOf(response.get("reportID"));
  }

  private List<Map<String, String>> getSegmentsForRequest(List<String> segmentIds) {
    if (segmentIds != null && segmentIds.size() > 0) {
      List<Map<String, String>> result = new ArrayList<>();
      for (String id : segmentIds) {
        result.add(Collections.singletonMap("id", id));
      }
      return result;
    } else {
      return null;
    }
  }

  private List<Map<String, ?>> getElementsForRequest(OmnitureReportRequest request) {
    List<OmnitureRequestElement> elements = request.getElements();
    if (elements != null && elements.size() > 0) {
      List<Map<String, ?>> results = new ArrayList<>();
      for (OmnitureRequestElement element : elements) {
        Map row = new HashMap<>();
        row.put("id", element.getId());
        row.put("top", element.getLimit() == -1 ? request.getLimit() : element.getLimit());
        row.put("search", getSearchKeywords(element.getElementTypeAndKeywordFilter()));
        row.put("classification", element.getClassification() == null ? "" : element.getClassification());
        row.put("startingWith", element.getStartingWith());
        results.add(row);
      }
      return results;
    } else {
      return null;
    }
  }

  private List<Map<String, ?>> getMetricsForRequest(List<OmnitureRequestMetric> metrics) {
    List<Map<String, ?>> result = new ArrayList<>();
    for (OmnitureRequestMetric metric : metrics) {
      Map row = new HashMap<>();
      row.put("id", metric.getId());
      row.put("segments", metric.getSegmentId() != null ? Collections.singletonList(Collections.singletonMap("id", metric.getSegmentId())): null);
      result.add(row);
    }
    return result;
  }

  private Map<String, List<String>> getSearchKeywords(Map<String, List<String>> elementTypeAndKeywordFilter) {
    Map<String, List<String>> result = new HashMap<>();
    if (elementTypeAndKeywordFilter != null && elementTypeAndKeywordFilter.containsKey("type") &&
            elementTypeAndKeywordFilter.containsKey("keywords")) {
      result.put("type", elementTypeAndKeywordFilter.get("type"));
      result.put("keywords", elementTypeAndKeywordFilter.get("keywords"));
    } else {
      return null;
    }
    return result;
  }
}