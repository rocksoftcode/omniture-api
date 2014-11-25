package com.tgt.search.reporting;

import com.tgt.search.reporting.domain.OmnitureReport;
import com.tgt.search.reporting.domain.OmnitureReportRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericGroovyApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.logging.Logger;

public class OmnitureReportTool {

  static final String CONTEXT_LOCATION = "applicationContext.groovy";
  static GenericGroovyApplicationContext context = new GenericGroovyApplicationContext();
  static int retryWait = 5000;
  static int retryCount = 5;

  static Logger logger = Logger.getLogger(OmnitureReportTool.class.getSimpleName());

  /**
   * Pushes a report request to Omniture
   * @param omnitureReport
   * @return Omniture's ID for the report
   */
  public static String enqueueReport(OmnitureReportRequest omnitureReport) {
    OmnitureReportEnqueuer enqueuer = getApplicationContext().getBean(OmnitureReportEnqueuer.class);
    return enqueuer.enqueueReport(omnitureReport);
  }

  /**
   * Retrieves a report from Omniture
   * @param reportId a report ID that is ready for retrieval
   * @return A fully-populated report object
   */
  public static OmnitureReport retrieveReport(Long reportId) {
    OmnitureReportRetriever retriever = getApplicationContext().getBean(OmnitureReportRetriever.class);
    return retriever.retrieveReportById(reportId);
  }

  /**
   * Determines whether a report is ready for retrieval
   * @param reportId a report ID
   * @return true if the report is ready, false otherwise
   */
  static boolean isReportReady(Long reportId) {
    OmnitureReportStatusChecker checker = getApplicationContext().getBean(OmnitureReportStatusChecker.class);
    Boolean result = null;
    int retries = 0;
    Exception caught = null;
    while (result == null && retries++ < retryCount) {
      try {
        result = checker.isReady(reportId);
      } catch (Exception e) {
        caught = e;
      }
    }
    if (result == null) {
      logger.throwing(OmnitureReport.class.getSimpleName(), "isReportReady", caught);
    }
    return result;
  }

  static ApplicationContext getApplicationContext() {
    if (!context.isActive()) {
      context.load(new ClassPathResource(CONTEXT_LOCATION));
      context.refresh();
    }
    return context;
  }
}
