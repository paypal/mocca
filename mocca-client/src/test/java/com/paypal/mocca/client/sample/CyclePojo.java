package com.paypal.mocca.client.sample;

public class CyclePojo {

    private int number;
    private String color;
    private CyclePojo cyclePojo;

    public CyclePojo setNumber(int number) {
        this.number = number;
        return this;
    }

    public CyclePojo setColor(String color) {
        this.color = color;
        return this;
    }

    public CyclePojo setCyclePojo(CyclePojo cyclePojo) {
        this.cyclePojo = cyclePojo;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public String getColor() {
        return color;
    }

    public CyclePojo getCyclePojo() {
        return cyclePojo;
    }

}