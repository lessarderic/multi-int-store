/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.endpoint;

import com.connexta.ingest.rest.spring.IngestApi;
import com.connexta.ingest.service.api.IngestService;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Controller class that implements the Ingest endpoint API. */
@RestController()
public class IngestController implements IngestApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(IngestController.class);

  private final IngestService ingestService;

  /**
   * Constructs a new REST Spring Web controller.
   *
   * @param ingestService service where requests will be forwarded
   */
  public IngestController(IngestService ingestService) {
    this.ingestService = ingestService;
  }

  @Override
  public ResponseEntity<Void> ingest(
      String acceptVersion,
      Long fileSize,
      String mimeType,
      MultipartFile file,
      String title,
      String fileName) {
    LOGGER.debug("Received ingest request for file [{}]", fileName);

    InputStream fileInputStream;

    // TODO - Add other input validation
    try {
      fileInputStream = file.getInputStream();
    } catch (IOException e) {
      LOGGER.error("Failed to read file [{}] input stream", fileName);
      throw new IllegalArgumentException("Failed to read file input stream", e);
    }

    ingestService.ingest(fileInputStream, fileName, mimeType, fileSize);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}
