package com.tgt.search.reporting.domain;

/**
 * Represents what Omniture calls a "metric."  The main thing you're interested in here is the "segment," which specifies the set of data to query.
 */
public class OmnitureRequestMetric {
  private String id;
  private String segmentId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSegmentId() {
    return segmentId;
  }

  public void setSegmentId(String segmentId) {
    this.segmentId = segmentId;
  }
}
