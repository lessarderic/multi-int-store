/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.service.impl;

import com.connexta.ingest.service.api.IngestService;
import com.connexta.ingest.transform.TransformClient;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class that contains the Ingest service business logic. */
public class IngestServiceImpl implements IngestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IngestServiceImpl.class);

  private final TransformClient transformClient;

  private final URL storeEndpoint;

  private final Supplier<String> idGenerator;

  /**
   * Initializes a new Ingest service instance.
   *
   * @param transformClient transformation client that will be used to process the file being
   *     ingested
   * @param storeEndpoint URL of the Multi-Int Store endpoint
   * @param idGenerator supplier of unique ID for resources
   */
  // TODO - Replace storeEndpoint with Multi-Int Store client
  // TODO - Remove idGenerator as the URL should be returned by the Multi-Int Store instead
  public IngestServiceImpl(
      TransformClient transformClient, URL storeEndpoint, Supplier<String> idGenerator) {
    this.transformClient = transformClient;
    this.storeEndpoint = storeEndpoint;
    this.idGenerator = idGenerator;
  }

  /**
   * {@inheritDoc} This method stores the file received in the multi-int store and then sends a
   * transformation request to the transformation service to perform the validation, metadata
   * extraction.
   */
  @Override
  public void ingest(InputStream file, String fileName, String mimeType, long fileSizeInBytes) {
    URL productUrl = storeFile();
    transformClient.transform(productUrl, mimeType, fileSizeInBytes);
  }

  // TODO - Replace with call to Multi-Int Store client
  private URL storeFile() {
    try {
      return new URL(storeEndpoint.toString() + idGenerator.get());
    } catch (MalformedURLException e) {
      // TODO - This should go away when we have the Multi-Int Store client
      throw new RuntimeException(e);
    }
  }
}
