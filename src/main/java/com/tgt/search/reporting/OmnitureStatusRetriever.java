package com.tgt.search.reporting;

import com.tgt.search.reporting.domain.OmnitureReportStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Component
public class OmnitureStatusRetriever {

  static final String STATUS_ENDPOINT_URL = "https://api.omniture.com/admin/1.3/rest/?method=Report.GetStatus";
  static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  OmnitureConnectionUtil omnitureConnectionUtil;

  /**
   * Retrieves a report's status from Omniture
   * @param reportId Any valid report ID
   * @return An object with information about the report
   */
  public OmnitureReportStatus retrieveStatusById(Long reportId) {
    HttpHeaders headers = omnitureConnectionUtil.getOmnitureHeaders();
    String body = "{ \"reportId\": \"" + reportId + "\" }";
    String responseText = restTemplate.exchange(STATUS_ENDPOINT_URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class).getBody();
    Map response = null;
    try {
      response = new ObjectMapper().readValue(responseText, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    OmnitureReportStatus result = new OmnitureReportStatus();
    result.setStatus((String) response.get("status"));
    result.setReportType((String) response.get("report_type"));
    result.setResultSize(Long.parseLong((String) response.get("result_size")));
    result.setErrorCode(Integer.valueOf((String) response.get("error_code")));

    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    try {
      result.setQueueTime(format.parse((String) response.get("queue_time")));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }
}
