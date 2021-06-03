package com.paypal.mocca.client.sample;

public class ComplexSampleType {

    public static class ComplexField {
        int innerIntVar;
        String innerStringVar;
        boolean innerBooleanVar;

        public ComplexField(int innerIntVar, String innerStringVar, boolean innerBooleanVar) {
            this.innerIntVar = innerIntVar;
            this.innerStringVar = innerStringVar;
            this.innerBooleanVar = innerBooleanVar;
        }

        public int getInnerIntVar() {
            return innerIntVar;
        }

        public ComplexField setInnerIntVar(int innerIntVar) {
            this.innerIntVar = innerIntVar;
            return this;
        }

        public String getInnerStringVar() {
            return innerStringVar;
        }

        public ComplexField setInnerStringVar(String innerStringVar) {
            this.innerStringVar = innerStringVar;
            return this;
        }

        public boolean isInnerBooleanVar() {
            return innerBooleanVar;
        }

        public ComplexField setInnerBooleanVar(boolean innerBooleanVar) {
            this.innerBooleanVar = innerBooleanVar;
            return this;
        }
    }

    private int intVar;
    private String stringVar;
    private boolean booleanVar;
    private ComplexField complexField;

    public ComplexSampleType(int intVar, String stringVar, boolean booleanVar, ComplexField complexField) {
        this.intVar = intVar;
        this.stringVar = stringVar;
        this.booleanVar = booleanVar;
        this.complexField = complexField;
    }

    public int getIntVar() {
        return intVar;
    }

    public ComplexSampleType setIntVar(int intVar) {
        this.intVar = intVar;
        return this;
    }

    public String getStringVar() {
        return stringVar;
    }

    public ComplexSampleType setStringVar(String stringVar) {
        this.stringVar = stringVar;
        return this;
    }

    public boolean isBooleanVar() {
        return booleanVar;
    }

    public ComplexSampleType setBooleanVar(boolean booleanVar) {
        this.booleanVar = booleanVar;
        return this;
    }

    public ComplexField getComplexField() {
        return complexField;
    }

    public ComplexSampleType setComplexField(ComplexField complexField) {
        this.complexField = complexField;
        return this;
    }
}