package com.paypal.mocca.client;

import com.paypal.mocca.client.sample.SampleResponseDTO;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

/**
 * Unit tests for {@link MoccaDeserializer}
 *
 * @author fabiocarvalho777@gmail.com
 */
public class MoccaDeserializerTest {

    private final MoccaDeserializer moccaDeserializer = new MoccaDeserializer();

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "Response does not include data nor errors JSON fields")
    public void invalidResponse1Test() {
        String response = "{}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "Response contains an empty errors list")
    public void invalidResponse2Test() {
        String response = "{\"errors\": [],\"data\": {\"getOneSample\": null}}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "Response contains a null errors field")
    public void invalidResponse3Test() {
        String response = "{\"errors\": null,\"data\": {\"getOneSample\": null}}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "\\[\\{\"message\":\"error 1\"},\\{\"message\":\"error 2\"}]")
    public void invalidResponse4Test() {
        String response = "{\"errors\": [{\"message\": \"error 1\"}, {\"message\": \"error 2\"}],\"data\": {\"getOneSample\": null}}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "Response JSON payload does not contain data for the requested operation: getOneSample")
    public void invalidResponse5Test() {
        String response = "{\"data\": {\"blah\": null}}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "Error processing response JSON payload")
    public void invalidResponse6Test() {
        String response = "{\"data\": }";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
        moccaDeserializer.deserialize(inputStream, SampleResponseDTO.class, "getOneSample");
    }

}