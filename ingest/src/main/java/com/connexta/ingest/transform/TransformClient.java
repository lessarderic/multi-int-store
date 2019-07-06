/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.transform;

import java.net.URL;

/** Interface for the Transformation service client. */
public interface TransformClient {
  /**
   * Submits a product for transformation.
   *
   * @param productUrl URL to the product to transform
   * @param mimeType mime type of the product to transform
   * @param sizeInBytes size of the product to transform
   */
  void transform(URL productUrl, String mimeType, long sizeInBytes);
}
