package com.tgt.search.reporting

import com.tgt.search.reporting.domain.OmnitureReport
import com.tgt.search.reporting.domain.OmnitureReportRequest
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericGroovyApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

class OmnitureReportToolSpec extends Specification {

  def "Loads application context"() {
    setup:
    OmnitureReportTool.context = Mock(GenericGroovyApplicationContext)

    when:
    ApplicationContext context = OmnitureReportTool.getApplicationContext()

    then:
    1 * OmnitureReportTool.context.load({ it.path == OmnitureReportTool.CONTEXT_LOCATION } as ClassPathResource)
    1 * OmnitureReportTool.context.refresh()
    OmnitureReportTool.context == context
  }

  def "Enqueues report"() {
    setup:
    OmnitureReportTool.context = Mock(GenericGroovyApplicationContext)
    OmnitureReportEnqueuer mockEnqueuer = Mock(OmnitureReportEnqueuer)
    OmnitureReportRequest mockRequest = new OmnitureReportRequest()

    when:
    String reportId = OmnitureReportTool.enqueueReport(mockRequest)

    then:
    1 * OmnitureReportTool.context.getBean(OmnitureReportEnqueuer) >> mockEnqueuer
    1 * mockEnqueuer.enqueueReport(mockRequest) >> "report id"
    reportId == "report id"
  }

  def "Retrieves report"() {
    setup:
    OmnitureReportTool.context = Mock(GenericGroovyApplicationContext)
    OmnitureReportRetriever mockRetriever = Mock(OmnitureReportRetriever)
    OmnitureReport mockReport = new OmnitureReport()

    when:
    OmnitureReport report = OmnitureReportTool.retrieveReport(54321)

    then:
    1 * OmnitureReportTool.context.getBean(OmnitureReportRetriever) >> mockRetriever
    1 * mockRetriever.retrieveReportById(54321) >> mockReport
    report == mockReport
  }

  def "Gets status"() {
    setup:
    OmnitureReportTool.context = Mock(GenericGroovyApplicationContext)
    OmnitureReportStatusChecker mockChecker = Mock(OmnitureReportStatusChecker)

    when:
    boolean result = OmnitureReportTool.isReportReady(12345)

    then:
    1 * OmnitureReportTool.context.getBean(OmnitureReportStatusChecker) >> mockChecker
    1 * mockChecker.isReady(12345) >> value
    result == value

    where:
    value << [true, false]
  }

  def "Retries on exception"() {
    setup:
    OmnitureReportTool.context = Mock(GenericGroovyApplicationContext)
    OmnitureReportStatusChecker mockChecker = Mock(OmnitureReportStatusChecker)
    mockChecker.isReady(666) >> { throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "First failure") } >> { throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Second Failure")} >> false
    OmnitureReportTool.retryWait = 100

    when:
    boolean result = OmnitureReportTool.isReportReady(666)

    then:
    1 * OmnitureReportTool.context.getBean(OmnitureReportStatusChecker) >> mockChecker

    !result
  }
}
