/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

/** Ingest endpoint integration and contract testing class. */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IngestIntegrationTest {
  private static final byte[] TEST_FILE = "some-content".getBytes();
  private static final int TEST_FILE_SIZE = TEST_FILE.length;

  @Autowired private MockMvc mvc;

  @Autowired private RestTemplate transformClientRestTemplate;

  @Value("${endpoints.store.url}")
  private String storeUrl;

  @Value("${endpoints.transform.url}")
  private String transformUrl;

  @Value("${endpoints.transform.version}")
  private String tranformApiVersion;

  private MockRestServiceServer server;

  @Before
  public void beforeEach() {
    server = MockRestServiceServer.createServer(transformClientRestTemplate);
  }

  @After
  public void afterEach() {
    server.verify();
    server.reset();
  }

  @Test
  public void successfulRequest() throws Exception {
    server
        .expect(requestTo(transformUrl))
        .andExpect(method(POST))
        .andExpect(header("Accept-Version", tranformApiVersion))
        .andExpect(this::matchTransformRequest)
        .andRespond(withStatus(ACCEPTED));
    // Note: Not returning anything since the response is going away in the next version.

    mvc.perform(
            multipart("/ingest")
                .file("file", TEST_FILE)
                .param("fileSize", String.valueOf(TEST_FILE_SIZE))
                .param("fileName", "file")
                .param("title", "qualityTitle")
                .param("mimeType", TEXT_PLAIN_VALUE)
                .header("Accept-Version", "1.2.1") // TODO - Get the version configuration
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isAccepted());
  }

  /**
   * Tests that the Ingest service returns a 400 (bad request) status code when the Transformation
   * service returns a 400 status code. This should not happen since the ingest endpoint should
   * perform the proper validation before submitting the transformation request, but if it does
   * happen, the client should not re-submit the same request, thus the 400 status code being
   * returned.
   */
  @Test
  public void transformRequestReturnsStatusCode400() throws Exception {
    server
        .expect(requestTo(transformUrl))
        .andExpect(method(POST))
        .andExpect(header("Accept-Version", tranformApiVersion))
        .andExpect(this::matchTransformRequest)
        .andRespond(
            withStatus(BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse(BAD_REQUEST.value(), "Bad request", "Invalid request")));

    mvc.perform(
            multipart("/ingest")
                .file("file", TEST_FILE)
                .param("fileSize", String.valueOf(TEST_FILE_SIZE))
                .param("fileName", "file")
                .param("title", "qualityTitle")
                .param("mimeType", TEXT_PLAIN_VALUE)
                .header("Accept-Version", "1.2.1") // TODO - Get the version configuration
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  private void matchTransformRequest(ClientHttpRequest request) throws IOException {
    // TODO - Ensure that the ID generated is the same as the one sent to Transform
    jsonPath("$.id", not(isEmptyOrNullString())).match(request);
    // TODO - Ensure that the callback and stagedLocation URLs contain the proper unique ID
    jsonPath("$.callbackUrl", startsWith(storeUrl)).match(request);
    jsonPath("$.stagedLocation", startsWith(storeUrl)).match(request);
    jsonPath("$.productLocation", startsWith(storeUrl)).match(request);
    jsonPath("$.mimeType", is(TEXT_PLAIN_VALUE)).match(request);
    jsonPath("$.bytes", is(TEST_FILE_SIZE)).match(request);
  }

  private String errorResponse(int status, String error, String message) {
    //    return "{}";
    return String.format(
        "{ \"timestamp\": \"%s\", \"path\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"",
        "2019-05-09T14:47:13.101+0000", "/transform", status, error, message);
  }
}
