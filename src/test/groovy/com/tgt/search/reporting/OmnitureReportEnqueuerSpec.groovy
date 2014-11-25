package com.tgt.search.reporting

import com.tgt.search.reporting.domain.DateGranularity
import com.tgt.search.reporting.domain.OmnitureReportRequest
import com.tgt.search.reporting.domain.OmnitureRequestElement
import com.tgt.search.reporting.domain.OmnitureRequestMetric
import groovy.json.JsonSlurper
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class OmnitureReportEnqueuerSpec extends Specification {

  OmnitureReportEnqueuer omnitureReportEnqueuer = new OmnitureReportEnqueuer(restTemplate: Mock(RestTemplate),
          omnitureConnectionUtil: Mock(OmnitureConnectionUtil))

  def "Builds omniture request and calls ReST API"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String response = new File("src/test/resources/response_queued_report.json").text
    OmnitureReportRequest request = new OmnitureReportRequest(dateFrom: Date.parse("MM-dd-yyyy", "01-01-2015"),
            dateTo: Date.parse("MM-dd-yyyy", "01-02-2015"),
            granularity: DateGranularity.DAY,
            metrics: [new OmnitureRequestMetric(id: "foo"),
                      new OmnitureRequestMetric(id: "bar", segmentId: "metric1 segment"),
                      new OmnitureRequestMetric(id: "baz", segmentId: "metric2 segment")],
            elements: [new OmnitureRequestElement(id: "blat", startingWith: 3, limit: 100, elementTypeAndKeywordFilter: ["type": "NOT", "keywords": ["foo: ^bar\$"]]),
                       new OmnitureRequestElement(id: "blah", startingWith: 5),
                       new OmnitureRequestElement(id: "blatz", limit:50, elementTypeAndKeywordFilter: ["wrong": "AND", "donotuse": ["ddd"]])],
            segmentIds: ["my segment"],
            sortBy: "foobar",
            limit: 667
    )

    when:
    String reportId = omnitureReportEnqueuer.enqueueReport(request)

    then:
    1 * omnitureReportEnqueuer.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * omnitureReportEnqueuer.restTemplate.exchange(OmnitureReportEnqueuer.ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders

      Map<String, ?> parsedBody = new JsonSlurper().parseText(it.body)
      Map<String, ?> requestParameters = parsedBody.reportDescription
      assert requestParameters.reportSuiteId == request.reportSuiteID
      assert requestParameters.dateFrom == request.dateFrom.format("yyyy-MM-dd")
      assert requestParameters.dateTo == request.dateTo.format("yyyy-MM-dd")
      assert requestParameters.dateGranularity == "day"
      assert requestParameters.sortBy == "foobar"
      assert requestParameters.metrics.findAll {request.metrics.id.contains(it.id)}.size() == request.metrics.size()
      assert requestParameters.elements.findAll {request.elements.id.contains(it.id)}.size() ==
              request.elements.size()
      assert requestParameters.elements[0].search.type == "NOT"
      assert requestParameters.elements[0].search.keywords == ["foo: ^bar\$"]
      assert requestParameters.elements[1].search == null
      assert requestParameters.elements[2].search == null
      assert requestParameters.elements[0].startingWith == 3
      assert requestParameters.elements[1].startingWith == 5
      assert requestParameters.elements[2].startingWith == 0
      assert requestParameters.elements[0].top == 100
      assert requestParameters.elements[1].top == 667
      assert requestParameters.elements[2].top == 50
      assert requestParameters.metrics[0].segments == null
      assert requestParameters.metrics[1].segments == [[id: "metric1 segment"]]
      assert requestParameters.metrics[2].segments == [[id: "metric2 segment"]]
      assert requestParameters.segments == [[id: "my segment"]]

      return true
    } as HttpEntity, String.class) >> new ResponseEntity<String>(response, HttpStatus.OK)
    reportId == String.valueOf(new JsonSlurper().parseText(response).reportID)
  }

  def "Consolidates dateFrom and dateTo into date when values are the same"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String response = new File("src/test/resources/response_queued_report.json").text
    Date date = Date.parse("MM-dd-yyyy", "02-02-2015")
    OmnitureReportRequest request = new OmnitureReportRequest(dateFrom: date,
            dateTo: date,
            granularity: DateGranularity.DAY,
            metrics: [new OmnitureRequestMetric(id: "foo"),
                      new OmnitureRequestMetric(id: "bar", segmentId: "metric1 segment"),
                      new OmnitureRequestMetric(id: "baz", segmentId: "metric2 segment")],
            elements: [new OmnitureRequestElement(id: "blat", elementTypeAndKeywordFilter: ["type": "AND", "keywords": "foo: ^bar\$"]),
                       new OmnitureRequestElement(id: "blah"),
                       new OmnitureRequestElement(id: "blatz")],
            segmentIds: ["my segment"],
            sortBy: "foobar",
            limit: 667
    )
    omnitureReportEnqueuer.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders

    when:
    String reportId = omnitureReportEnqueuer.enqueueReport(request)

    then:
    1 * omnitureReportEnqueuer.restTemplate.exchange(OmnitureReportEnqueuer.ENDPOINT_URL, HttpMethod.POST, {
      Map<String, ?> parsedBody = new JsonSlurper().parseText(it.body)
      Map<String, ?> requestParameters = parsedBody.reportDescription
      assert requestParameters.dateFrom == null
      assert requestParameters.dateTo == null
      assert requestParameters.date == date.format("yyyy-MM-dd")
      return true
    } as HttpEntity, String.class) >> new ResponseEntity<String>(response, HttpStatus.OK)
    reportId == String.valueOf(new JsonSlurper().parseText(response).reportID)
  }
}
