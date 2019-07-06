/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.service.api;

import java.io.InputStream;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/** Provides clients with a way to ingest Products into ION for processing and storage */
public interface IngestService {

  /**
   * Ingest the file provided in the Multi-Int Store after performing all the required validation
   * and metadata extraction.
   *
   * @param file file to ingest. Cannot be empty.
   * @param fileName name associated with the file
   * @param mimeType mime type of the file to ingest
   * @param fileSizeInBytes size of files to ingest in bytes
   */
  void ingest(
      InputStream file,
      @NotEmpty String fileName,
      @NotEmpty String mimeType,
      @Min(1) long fileSizeInBytes);
}
