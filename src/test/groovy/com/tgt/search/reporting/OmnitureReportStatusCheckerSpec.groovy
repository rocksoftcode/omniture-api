package com.tgt.search.reporting

import groovy.json.JsonSlurper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class OmnitureReportStatusCheckerSpec extends Specification {
  OmnitureReportStatusChecker checker = new OmnitureReportStatusChecker(restTemplate: Mock(RestTemplate), omnitureConnectionUtil: Mock(OmnitureConnectionUtil))

  def "Correctly reports status"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()

    when:
    boolean result = checker.isReady(12345)

    then:
    1 * checker.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * checker.restTemplate.exchange(OmnitureReportStatusChecker.ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders
      assert new JsonSlurper().parseText(it.body).reportID == "12345"
      return true
    } as HttpEntity, String) >> new ResponseEntity<String>(text, HttpStatus.OK)
    result == expected

    where:
    text                                                                     | expected
    new File("src/test/resources/response_report_status_done.json").text     | true
    new File("src/test/resources/response_report_status_not_done.json").text | false
    new File("src/test/resources/response_report_status_error.json").text    | false
  }
}
