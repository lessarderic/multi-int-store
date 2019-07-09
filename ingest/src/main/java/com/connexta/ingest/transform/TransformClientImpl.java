/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.transform;

import com.connexta.transformation.rest.models.TransformRequest;
import com.connexta.transformation.rest.models.TransformResponse;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Rest implementation of the transform service client. */
@Service
public class TransformClientImpl implements TransformClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransformClientImpl.class);

  private final RestTemplate restTemplate;

  private final String transformEndpoint;

  private final String transformEndpointVersion;

  /**
   * Initializes a new instance of the {@link TransformClientImpl}.
   *
   * @param transformClientRestTemplate {@link RestTemplate} used to communicate with the
   *     transformation service
   * @param transformEndpoint transformation service endpoint URL
   * @param transformEndpointVersion transformation service API version
   */
  public TransformClientImpl(
      RestTemplate transformClientRestTemplate,
      @Value("${endpoints.transform.url}") String transformEndpoint,
      @Value("${endpoints.transform.version}") String transformEndpointVersion) {
    this.restTemplate = transformClientRestTemplate;
    this.transformEndpoint = transformEndpoint;
    this.transformEndpointVersion = transformEndpointVersion;
  }

  @Override
  public void transform(URL productUrl, String mimeType, long sizeInBytes) {
    LOGGER.debug("Calling transform service [{}] for product [{}]", transformEndpoint, productUrl);

    TransformRequest transformRequest = buildRequest(productUrl, mimeType, sizeInBytes);
    HttpEntity<TransformRequest> requestHttpEntity = buildHttpEntity(transformRequest);
    try {
      restTemplate.postForObject(transformEndpoint, requestHttpEntity, TransformResponse.class);
      // TODO - Update when the transformation service returns something we need
    } catch (HttpClientErrorException.BadRequest e) {
      String message =
          String.format(
              "Failed to transform product [%s] with mime type [%s] and a size of [%d] bytes",
              productUrl, mimeType, sizeInBytes);
      LOGGER.error(message, e);
      throw new IllegalArgumentException(message, e);
    }
    // TODO - Handle other status codes
  }

  private TransformRequest buildRequest(URL productUrl, String mimeType, long sizeInBytes) {
    final TransformRequest transformRequest = new TransformRequest();
    transformRequest.setBytes(sizeInBytes);
    transformRequest.setCallbackUrl(productUrl.toString());
    transformRequest.setId("1"); // TODO This should be removed from the API
    transformRequest.setMimeType(mimeType);
    transformRequest.setProductLocation(
        productUrl.toString()); // TODO This should be removed from the API
    transformRequest.setStagedLocation(productUrl.toString());
    return transformRequest;
  }

  private HttpEntity<TransformRequest> buildHttpEntity(TransformRequest transformRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Version", transformEndpointVersion);

    return new HttpEntity<>(transformRequest, headers);
  }
}
