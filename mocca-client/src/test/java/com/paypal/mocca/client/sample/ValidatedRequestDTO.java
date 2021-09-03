package com.paypal.mocca.client.sample;

import javax.validation.constraints.NotNull;

public class ValidatedRequestDTO {

    @NotNull
    private String foo;

    private String bar;

    public ValidatedRequestDTO() {
    }

    public ValidatedRequestDTO(String foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public ValidatedRequestDTO setFoo(String foo) {
        this.foo = foo;
        return this;
    }

    public ValidatedRequestDTO setBar(String bar) {
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