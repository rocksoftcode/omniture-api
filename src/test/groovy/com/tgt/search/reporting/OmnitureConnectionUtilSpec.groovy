package com.tgt.search.reporting

import org.springframework.http.HttpHeaders
import spock.lang.Specification

import java.security.MessageDigest

class OmnitureConnectionUtilSpec extends Specification {
  OmnitureConnectionUtil omnitureConnectionUtil = new OmnitureConnectionUtil(username: "foobar", password: "foobarP")

  def "Builds header with requisite info"() {
    when:
    HttpHeaders headers = omnitureConnectionUtil.getOmnitureHeaders()

    then:
    List<String> xwsseParts = headers.getFirst("X-WSSE").split(",")*.trim()
    xwsseParts[0] == "UsernameToken Username=\"foobar\""
    xwsseParts[1] ==~ /^PasswordDigest=".*="/
    xwsseParts[2] ==~ /^Nonce=\".*=="$/
    xwsseParts[3].startsWith("Created=\"${new Date().format("yyyy-MM-dd'T'", TimeZone.getTimeZone("UTC"))}")
    xwsseParts[3].endsWith("Z\"")

    cleanup:
    OmnitureConnectionUtil.metaClass = null
  }
}
