package com.tgt.search.reporting

import com.tgt.search.reporting.domain.OmnitureReport
import com.tgt.search.reporting.domain.OmnitureReportDatum
import groovy.json.JsonSlurper
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class OmnitureReportRetrieverSpec extends Specification {

  OmnitureReportRetriever omnitureReportRetriever = new OmnitureReportRetriever(restTemplate: Mock(RestTemplate), omnitureConnectionUtil: Mock(OmnitureConnectionUtil))

  def "Retrieves report by ID, hourly data"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String mockResponse = new File("src/test/resources/response_visits_by_keyword_by_hour.json").text
    Map topLevelParsedResponse = new JsonSlurper().parseText(mockResponse)
    Map parsedResponse = topLevelParsedResponse.report

    when:
    OmnitureReport response = omnitureReportRetriever.retrieveReportById(54321)

    then:
    1 * omnitureReportRetriever.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * omnitureReportRetriever.restTemplate.exchange(OmnitureReportRetriever.ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders
      assert new JsonSlurper().parseText(it.body) == [reportID: "54321"]
      return true
    } as HttpEntity<String>, String) >> new ResponseEntity<String>(mockResponse, HttpStatus.OK)

    response.type == parsedResponse.type

    response.elements[0].id == parsedResponse.elements[0].id
    response.elements[0].name == parsedResponse.elements[0].name

    response.reportSuite == parsedResponse.reportSuite.id
    response.period == parsedResponse.period

    response.metrics[0].id == parsedResponse.metrics[0].id
    response.metrics[0].name == parsedResponse.metrics[0].name
    response.metrics[0].type == parsedResponse.metrics[0].type
    response.metrics[0].decimalPlaces == parsedResponse.metrics[0].decimals
    response.metrics[0].latency == parsedResponse.metrics[0].latency

    response.data[0].year == parsedResponse.data[0].year
    response.data[0].month == parsedResponse.data[0].month
    response.data[0].day == parsedResponse.data[0].day
    response.data[0].hour == parsedResponse.data[0].hour

    response.data[0].items.eachWithIndex { OmnitureReportDatum item, int i ->
      assert item.counts[0].toString() == parsedResponse.data[0].breakdown[i].counts[0];
      assert item.name == parsedResponse.data[0].breakdown[i].name;
      assert item.url == parsedResponse.data[0].breakdown[i].url;
    }

    response.runTime == topLevelParsedResponse.runSeconds.toDouble()
    response.waitTime == topLevelParsedResponse.waitSeconds.toDouble()
  }

  def "Retrieves report by ID, non-hourly data"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String mockResponse = new File("src/test/resources/response_visits_by_keyword.json").text
    Map topLevelParsedResponse = new JsonSlurper().parseText(mockResponse)
    Map parsedResponse = topLevelParsedResponse.report

    when:
    OmnitureReport response = omnitureReportRetriever.retrieveReportById(54321)

    then:
    1 * omnitureReportRetriever.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * omnitureReportRetriever.restTemplate.exchange(OmnitureReportRetriever.ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders
      assert new JsonSlurper().parseText(it.body) == [reportID: "54321"]
      return true
    } as HttpEntity<String>, String) >> new ResponseEntity<String>(mockResponse, HttpStatus.OK)

    response.type == parsedResponse.type

    response.elements[0].id == parsedResponse.elements[0].id
    response.elements[0].name == parsedResponse.elements[0].name

    response.reportSuite == parsedResponse.reportSuite.id
    response.period == parsedResponse.period

    response.metrics[0].id == parsedResponse.metrics[0].id
    response.metrics[0].name == parsedResponse.metrics[0].name
    response.metrics[0].type == parsedResponse.metrics[0].type
    response.metrics[0].decimalPlaces == parsedResponse.metrics[0].decimals
    response.metrics[0].latency == parsedResponse.metrics[0].latency

    response.data[0].items.each { OmnitureReportDatum item ->
      assert parsedResponse.data.find { Map parsedItem ->
        item.counts[0].toString() == parsedItem.counts[0] && item.name == parsedItem.name && item.url == parsedItem.url
      }
    }

    response.runTime == topLevelParsedResponse.runSeconds.toDouble()
    response.waitTime == topLevelParsedResponse.waitSeconds.toDouble()
  }

  def "Retrieves report by ID, breakdown data"() {
    setup:
    HttpHeaders mockHeaders = new HttpHeaders()
    String mockResponse = new File("src/test/resources/response_instances_by_keyword_breakdown.json").text
    Map topLevelParsedResponse = new JsonSlurper().parseText(mockResponse)
    Map parsedResponse = topLevelParsedResponse.report

    when:
    OmnitureReport response = omnitureReportRetriever.retrieveReportById(54321)

    then:
    1 * omnitureReportRetriever.omnitureConnectionUtil.getOmnitureHeaders() >> mockHeaders
    1 * omnitureReportRetriever.restTemplate.exchange(OmnitureReportRetriever.ENDPOINT_URL, HttpMethod.POST, {
      assert it.headers == mockHeaders
      assert new JsonSlurper().parseText(it.body) == [reportID: "54321"]
      return true
    } as HttpEntity<String>, String) >> new ResponseEntity<String>(mockResponse, HttpStatus.OK)

    response.type == parsedResponse.type

    response.elements[0].id == parsedResponse.elements[0].id
    response.elements[0].name == parsedResponse.elements[0].name
    response.elements[1].id == parsedResponse.elements[1].id
    response.elements[1].name == parsedResponse.elements[1].name

    response.reportSuite == parsedResponse.reportSuite.id
    response.period == parsedResponse.period

    response.metrics[0].id == parsedResponse.metrics[0].id
    response.metrics[0].name == parsedResponse.metrics[0].name
    response.metrics[0].type == parsedResponse.metrics[0].type
    response.metrics[0].decimalPlaces == parsedResponse.metrics[0].decimals
    response.metrics[0].latency == parsedResponse.metrics[0].latency

    response.data[0].name == parsedResponse.data[0].name
    response.data[0].url == parsedResponse.data[0].url
    response.data[0].counts[0].toString() == parsedResponse.data[0].counts[0]
    response.data[1].name == parsedResponse.data[1].name
    response.data[1].url == parsedResponse.data[1].url
    response.data[1].counts[0].toString() == parsedResponse.data[1].counts[0]

    response.data[0].items.eachWithIndex { OmnitureReportDatum item, int i ->
      assert item.counts[0].toString() == parsedResponse.data[0].breakdown[i].counts[0]
      assert item.name == parsedResponse.data[0].breakdown[i].name
      assert item.url == parsedResponse.data[0].breakdown[i].url
    }
    response.data[1].items.eachWithIndex { OmnitureReportDatum item, int i ->
      assert item.counts[0].toString() == parsedResponse.data[1].breakdown[i].counts[0]
      assert item.name == parsedResponse.data[1].breakdown[i].name
      assert item.url == parsedResponse.data[1].breakdown[i].url
    }

    response.runTime == topLevelParsedResponse.runSeconds.toDouble()
    response.waitTime == topLevelParsedResponse.waitSeconds.toDouble()
  }
}