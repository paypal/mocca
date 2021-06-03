package com.paypal.mocca.client.sample;

public class SampleRequestDTO {

    private String foo;
    private String bar;

    public SampleRequestDTO() {
    }

    public SampleRequestDTO(String foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public SampleRequestDTO setFoo(String foo) {
        this.foo = foo;
        return this;
    }

    public SampleRequestDTO setBar(String bar) {
        this.bar = bar;
        return this;
    }

    public String getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

}