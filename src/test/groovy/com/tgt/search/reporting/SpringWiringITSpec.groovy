package com.tgt.search.reporting

import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericGroovyApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class SpringWiringITSpec extends Specification {

  def "Context loads"() {
    setup:
    ApplicationContext context = new GenericGroovyApplicationContext("applicationContext.groovy")

    when:
    context.load()

    then:
    context.getBean(RestTemplate) != null
    context.getBean(OmnitureReportEnqueuer) != null
    context.getBean(OmnitureReportEnqueuer).restTemplate != null
    context.getBean(OmnitureReportEnqueuer).omnitureConnectionUtil != null
    context.getBean(OmnitureReportRetriever) != null
    context.getBean(OmnitureReportRetriever).restTemplate != null
    context.getBean(OmnitureReportRetriever).omnitureConnectionUtil != null
    context.getBean(OmnitureStatusRetriever) != null
    context.getBean(OmnitureStatusRetriever).omnitureConnectionUtil != null
    context.getBean(OmnitureStatusRetriever).restTemplate != null
    context.getBean(OmnitureStatusRetriever).omnitureConnectionUtil != null
    context.getBean(OmnitureConnectionUtil) != null
    context.getBean(OmnitureConnectionUtil).username != null
    context.getBean(OmnitureConnectionUtil).password != null
  }

}
