/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.ingest.transform;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/** Spring Configuration for the {@link TransformClientImpl} class. */
@Configuration
@EnableAutoConfiguration
public class TransformClientConfiguration {

  @Bean
  public RestTemplate transformClientRestTemplate(RestTemplateBuilder builder) {
    return builder.errorHandler(new DefaultResponseErrorHandler()).build();
  }
}
