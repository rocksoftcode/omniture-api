package com.tgt.search.reporting

import com.tgt.search.reporting.domain.OmnitureReportStatus
import groovy.json.JsonSlurper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class OmnitureStatusRetrieverSpec extends Specification {

  OmnitureStatusRetriever omnitureStatusRetriever = new OmnitureStatusRetriever(restTemplate: Mock(RestTemplate), omnitureConnectionUtil: Mock(OmnitureConnectionUtil))

  def "Retrieves individual report status"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String responseText = new File("src/test/resources/response_report_status_done.json").text
    Map response = new JsonSlurper().parseText(responseText)

    when:
    OmnitureReportStatus status = omnitureStatusRetriever.retrieveStatusById(12345)

    then:
    1 * omnitureStatusRetriever.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * omnitureStatusRetriever.restTemplate.exchange(OmnitureStatusRetriever.STATUS_ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders
      assert new JsonSlurper().parseText(it.body) == [reportId: "12345"]
      return true
    } as HttpEntity, String) >> new ResponseEntity(responseText, HttpStatus.OK)
    status.errorCode == Integer.valueOf(response.error_code)
    status.errorMessage == response.error_message
    status.queueTime == Date.parse("yyyy-MM-dd HH:mm:ss", response.queue_time)
    status.reportType == response.report_type
    status.resultSize == Long.parseLong(response.result_size)
    status.status == response.status
  }

  def "Determines doneness"() {
    setup:
    OmnitureReportStatus status = new OmnitureReportStatus(status: value)

    expect:
    status.isComplete() == expected

    where:
    value  | expected
    "blah" | false
    "done" | true
    null   | false
  }

}
