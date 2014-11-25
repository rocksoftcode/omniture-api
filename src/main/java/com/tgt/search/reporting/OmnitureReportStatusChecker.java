package com.tgt.search.reporting;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OmnitureReportStatusChecker {

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  OmnitureConnectionUtil omnitureConnectionUtil;

  static final String ENDPOINT_URL = "https://api.omniture.com/admin/1.3/rest/?method=Report.GetStatus";

  /**
   * Checks to see if a report has completed generation
   * @param reportId Ominture report ID
   * @return true if a report is ready to be retrieved, false otherwise
   */
  public boolean isReady(Long reportId) {
    String body = "{ \"reportID\": \"" + reportId + "\" }";
    HttpEntity<String> request = new HttpEntity<String>(body, omnitureConnectionUtil.getOmnitureHeaders());
    String response = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, request, String.class).getBody();
    Map parsedResponse = new HashMap();
    try {
      parsedResponse = new ObjectMapper().readValue(response, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "done".equals(parsedResponse.get("status"));
  }
}
