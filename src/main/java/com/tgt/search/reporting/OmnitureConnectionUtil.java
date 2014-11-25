package com.tgt.search.reporting;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class negotiates the connection to Adobe's services.  Make sure that spring can find a resource containing "omniture.user" and "omniture.password."
 */
@Component
public class OmnitureConnectionUtil {

  @Value("${omniture.user}")
  private String username;
  @Value("${omniture.password}")
  private String password;

  public HttpHeaders getOmnitureHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-WSSE", getHeader());
    return headers;
  }

  /**
   * This peculiar code comes directly from Adobe.  Putting this string together in any other format just didn't seem to work.
   * All I've done to it is make it more "Groovy." I also had to change the timestamp to UTC.
   */
  private String getHeader() {
    try {
      byte[] nonceB = generateNonce();
      String nonce = base64Encode(nonceB);
      String created = generateTimestamp();
      String password64 = getBase64Digest(nonceB, created.getBytes("UTF-8"), password.getBytes("UTF-8"));
      StringBuffer header = new StringBuffer("UsernameToken Username=\"");
      header.append(username);
      header.append("\", ");
      header.append("PasswordDigest=\"");
      header.append(password64.trim());
      header.append("\", ");
      header.append("Nonce=\"");
      header.append(nonce.trim());
      header.append("\", ");
      header.append("Created=\"");
      header.append(created);
      header.append("\"");
      return header.toString();
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  private static byte[] generateNonce() {
    String nonce = Long.toString(new Date().getTime());
    return nonce.getBytes();
  }

  private static String generateTimestamp() {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return dateFormatter.format(new Date());
  }

  private static synchronized String getBase64Digest(byte[] nonce, byte[] created, byte[] password) {
    try {
      MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");
      // SHA-1 ( nonce + created + password )
      messageDigester.reset();
      messageDigester.update(nonce);
      messageDigester.update(created);
      messageDigester.update(password);
      return base64Encode(messageDigester.digest());
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static String base64Encode(byte[] bytes) {
    return new Base64().encodeAsString(bytes);
  }
}