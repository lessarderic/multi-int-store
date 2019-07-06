/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.config;

import com.connexta.ingest.service.api.IngestService;
import com.connexta.ingest.service.impl.IngestServiceImpl;
import com.connexta.ingest.transform.TransformClient;
import java.net.URL;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/** Spring configuration class for Ingest service. */
@Configuration
public class ApplicationConfiguration {
  // TODO - This should really be something returned by the multi-int store and not
  // generated here...
  @Bean
  public Supplier<String> idGenerator() {
    return () -> UUID.randomUUID().toString().replace("-", "");
  }

  @Bean
  public IngestService ingestServiceImpl(
      TransformClient transformClient, @Value("${endpoints.store.url}") URL storeEndpoint) {
    return new IngestServiceImpl(transformClient, storeEndpoint, idGenerator());
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    final CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeClientInfo(true);
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setIncludeHeaders(true);
    filter.setAfterMessagePrefix("Inbound Request: ");
    filter.setMaxPayloadLength(5120);
    return filter;
  }
}
