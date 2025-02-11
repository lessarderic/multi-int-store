/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * Released under the GNU Lesser General Public License version 3; see
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */
package com.connexta.multiintstore;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.connexta.multiintstore.callbacks.CallbackValidator;
import com.connexta.multiintstore.callbacks.FinishedCallback;
import com.connexta.multiintstore.callbacks.MetadataCallback;
import com.connexta.multiintstore.callbacks.ProductCallback;
import com.connexta.multiintstore.callbacks.UnsupportedCallbackException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class CallbackValidatorTest {

  private final CallbackValidator validator = new CallbackValidator();
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void MetadataCallbackTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"COMPLETE\",\n"
                + "\"type\": \"cst\",\n"
                + "\"mimeType\": \"application/xml\",\n"
                + "\"bytes\": \"256\",\n"
                + "\"location\": \"https://localhost:8080\",\n"
                + "\"security\": {\n"
                + "\t\"classification\": \"U\",\n"
                + "\t\"ownerProducer\": \"ownerProducer\"\n"
                + "}\n"
                + "}");
    assertThat(validator.parse(validMetadata), is(instanceOf(MetadataCallback.class)));
  }

  @Test
  public void ProductCallbackTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"COMPLETE\",\n"
                + "\"type\": \"product\",\n"
                + "\"security\": {\n"
                + "\t\"classification\": \"U\",\n"
                + "\t\"ownerProducer\": \"ownerProducer\"\n"
                + "}\n"
                + "}");
    assertThat(validator.parse(validMetadata), is(instanceOf(ProductCallback.class)));
  }

  @Test
  public void FinishedCallbackTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree("{\n" + "\t\"id\": \"42\",\n" + "\"status\": \"COMPLETE\"}\n");
    assertThat(validator.parse(validMetadata), is(instanceOf(FinishedCallback.class)));
  }

  @Test(expected = UnsupportedCallbackException.class)
  public void InvalidSecurityMetadataCallbackTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"COMPLETE\",\n"
                + "\"type\": \"cst\",\n"
                + "\"mimeType\": \"application/xml\",\n"
                + "\"bytes\": \"256\",\n"
                + "\"location\": \"https://localhost:8080\",\n"
                + "\"security\": {\n"
                + "\t\"ownerProducer\": \"ownerProducer\"\n"
                + "}\n"
                + "}");
    validator.parse(validMetadata);
  }

  @Test(expected = UnsupportedCallbackException.class)
  public void MetadataCallbackMissingBytesTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"COMPLETE\",\n"
                + "\"type\": \"cst\",\n"
                + "\"mimeType\": \"application/xml\",\n"
                + "\"location\": \"https://localhost:8080\",\n"
                + "\"security\": {\n"
                + "\t\"ownerProducer\": \"ownerProducer\"\n"
                + "}\n"
                + "}");
    validator.parse(validMetadata);
  }

  @Test(expected = UnsupportedCallbackException.class)
  public void MetadataCallbackStringBytesTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"COMPLETE\",\n"
                + "\"type\": \"cst\",\n"
                + "\"mimeType\": \"application/xml\",\n"
                + "\"bytes\": \"Heh\",\n"
                + "\"location\": \"https://localhost:8080\",\n"
                + "\"security\": {\n"
                + "\t\"classification\": \"U\",\n"
                + "\t\"ownerProducer\": \"ownerProducer\"\n"
                + "}\n"
                + "}");
    validator.parse(validMetadata);
  }

  @Test
  public void FinishedCallbackWithMessageTest() throws Exception {
    JsonNode validMetadata =
        mapper.readTree(
            "{\n"
                + "\t\"id\": \"42\",\n"
                + "\"status\": \"Failed\",\n"
                + "\"message\": \"This data sucks\""
                + "}");
    assertThat(validator.parse(validMetadata), is(instanceOf(FinishedCallback.class)));
  }
}
