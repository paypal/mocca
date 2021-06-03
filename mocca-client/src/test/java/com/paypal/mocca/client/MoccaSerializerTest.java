package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Variable;
import com.paypal.mocca.client.sample.ComplexSampleType;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link MoccaSerializer}
 *
 * @author fabiocarvlaho777@gmail.com
 */
public class MoccaSerializerTest {

    private final MoccaSerializer moccaSerializer = new MoccaSerializer();

    @Test
    public void simpleRequestTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Query,
                null, null, "{\n  \"query\" : \"query{getOneSample(bar: \\\"bar\\\", foo: \\\"foo\\\") {bar foo}}\"\n}");
    }

    @Test
    public void simpleRequestNullFieldTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO().setFoo("foo");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Mutation,
                null, null, "{\n  \"query\" : \"mutation{getOneSample(foo: \\\"foo\\\") {bar foo}}\"\n}");
    }

    @Test
    public void simpleRequestIgnoreTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        Variable variable = newVariable("foo");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, SampleResponseDTO.class,"getOneSample", MoccaUtils.OperationType.Query,
                null, variable, "{\n  \"query\" : \"query{getOneSample(bar: \\\"bar\\\") {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, "sevenseven", false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, "seven", true, complexField);

        requestTest(complexSampleType, ComplexSampleType.class, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Mutation,
                null, null, "{\n  \"query\" : \"mutation{getOneSample(booleanVar: true, complexField: {innerBooleanVar: false, innerIntVar: 77, innerStringVar: \\\"sevenseven\\\"}, intVar: 7, stringVar: \\\"seven\\\") {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestNullFieldTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, null, false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, null, true, complexField);

        requestTest(complexSampleType, ComplexSampleType.class, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Query,
                null, null, "{\n  \"query\" : \"query{getOneSample(booleanVar: true, complexField: {innerBooleanVar: false, innerIntVar: 77}, intVar: 7) {bar foo}}\"\n}");
    }

    @Test
    public void complexRequestIgnoreTest() throws IOException {
        ComplexSampleType.ComplexField complexField = new ComplexSampleType.ComplexField(77, "sevenseven", false);
        ComplexSampleType complexSampleType = new ComplexSampleType(7, "seven", true, complexField);
        Variable variable = newVariable("intVar", "complexField.innerStringVar", "complexField.innerBooleanVar");

        requestTest(complexSampleType, ComplexSampleType.class, SampleResponseDTO.class, "getOneSample", MoccaUtils.OperationType.Mutation,
                null, variable, "{\n  \"query\" : \"mutation{getOneSample(booleanVar: true, complexField: {innerIntVar: 77}, stringVar: \\\"seven\\\") {bar foo}}\"\n}");
    }

    @Test
    public void complexResponseTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                null, null, "{\n  \"query\" : \"query{getABeer(bar: \\\"bar\\\", foo: \\\"foo\\\") {booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}}\"\n}");
    }

    @Test
    public void complexResponseCustomSelectionSetAllFieldsTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        SelectionSet selectionSet = newSelectionSet("{booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                selectionSet, null, "{\n  \"query\" : \"query{getABeer(bar: \\\"bar\\\", foo: \\\"foo\\\") {booleanVar complexField {innerBooleanVar innerIntVar innerStringVar} intVar stringVar}}\"\n}");
    }

    @Test
    public void complexResponseCustomSelectionSetLessFieldsTest() throws IOException {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("foo", "bar");
        SelectionSet selectionSet = newSelectionSet("{booleanVar complexField stringVar}");

        requestTest(sampleRequestDTO, SampleRequestDTO.class, ComplexSampleType.class,"getABeer", MoccaUtils.OperationType.Query,
                selectionSet, null, "{\n  \"query\" : \"query{getABeer(bar: \\\"bar\\\", foo: \\\"foo\\\") {booleanVar complexField stringVar}}\"\n}");
    }

    private void requestTest(Object object, Type requestType, Type responseType, String operationName, MoccaUtils.OperationType operationType, SelectionSet selectionSet, Variable variable, String expectedRequest) throws IOException {
        byte[] requestBytes = moccaSerializer.serialize(object, requestType, responseType, operationName, operationType, selectionSet, variable);
        String actualRequest = new String(requestBytes);
        assertEquals(actualRequest, expectedRequest);
    }

    private Variable newVariable(String... ignore) {
        return new Variable(){
            @Override public Class<? extends Annotation> annotationType() { return Variable.class; }
            @Override public String[] ignore() { return ignore;}
        };
    }

    private SelectionSet newSelectionSet(String value) {
        return new SelectionSet(){
            @Override public Class<? extends Annotation> annotationType() { return SelectionSet.class; }
            @Override public String value() { return value;}
        };
    }
}