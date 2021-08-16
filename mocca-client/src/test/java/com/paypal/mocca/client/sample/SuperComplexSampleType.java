package com.paypal.mocca.client.sample;

import java.util.List;

public class SuperComplexSampleType {

    public static class SuperComplexField {
        int innerIntVar;
        String innerStringVar;
        boolean innerBooleanVar;
        List<String> innerStringListVar;
        SuperComplexField innerComplexVar;
        List<SuperComplexField> innerComplexListVar;

        public SuperComplexField(int innerIntVar, String innerStringVar, boolean innerBooleanVar,
                                 List<String> innerStringListVar, SuperComplexField innerComplexVar,
                                 List<SuperComplexField> innerComplexListVar) {
            this.innerIntVar = innerIntVar;
            this.innerStringVar = innerStringVar;
            this.innerBooleanVar = innerBooleanVar;
            this.innerStringListVar = innerStringListVar;
            this.innerComplexVar = innerComplexVar;
            this.innerComplexListVar = innerComplexListVar;
        }

        public int getInnerIntVar() {
            return innerIntVar;
        }

        public SuperComplexField setInnerIntVar(int innerIntVar) {
            this.innerIntVar = innerIntVar;
            return this;
        }

        public String getInnerStringVar() {
            return innerStringVar;
        }

        public SuperComplexField setInnerStringVar(String innerStringVar) {
            this.innerStringVar = innerStringVar;
            return this;
        }

        public boolean isInnerBooleanVar() {
            return innerBooleanVar;
        }

        public SuperComplexField setInnerBooleanVar(boolean innerBooleanVar) {
            this.innerBooleanVar = innerBooleanVar;
            return this;
        }

        public List<String> getInnerStringListVar() {
            return innerStringListVar;
        }

        public void setInnerStringListVar(List<String> innerStringListVar) {
            this.innerStringListVar = innerStringListVar;
        }

        public SuperComplexField getInnerComplexVar() {
            return innerComplexVar;
        }

        public void setInnerComplexVar(SuperComplexField innerComplexVar) {
            this.innerComplexVar = innerComplexVar;
        }

        public List<SuperComplexField> getInnerComplexListVar() {
            return innerComplexListVar;
        }

        public void setInnerComplexListVar(List<SuperComplexField> innerComplexListVar) {
            this.innerComplexListVar = innerComplexListVar;
        }
    }

    private int intVar;
    private String stringVar;
    private boolean booleanVar;
    private SuperComplexField complexField;
    private List<SuperComplexField> complexListVar;
    private List<String> stringListVar;

    public SuperComplexSampleType(int intVar, String stringVar, boolean booleanVar, SuperComplexField complexField,
                                  List<SuperComplexField> complexListVar,
                                  List<String> stringListVar) {
        this.intVar = intVar;
        this.stringVar = stringVar;
        this.booleanVar = booleanVar;
        this.complexField = complexField;
        this.complexListVar = complexListVar;
        this.stringListVar = stringListVar;
    }

    public int getIntVar() {
        return intVar;
    }

    public SuperComplexSampleType setIntVar(int intVar) {
        this.intVar = intVar;
        return this;
    }

    public String getStringVar() {
        return stringVar;
    }

    public SuperComplexSampleType setStringVar(String stringVar) {
        this.stringVar = stringVar;
        return this;
    }

    public boolean isBooleanVar() {
        return booleanVar;
    }

    public SuperComplexSampleType setBooleanVar(boolean booleanVar) {
        this.booleanVar = booleanVar;
        return this;
    }

    public SuperComplexField getComplexField() {
        return complexField;
    }

    public SuperComplexSampleType setComplexField(SuperComplexField complexField) {
        this.complexField = complexField;
        return this;
    }

    public List<SuperComplexField> getComplexListVar() {
        return complexListVar;
    }

    public void setComplexListVar(List<SuperComplexField> complexListVar) {
        this.complexListVar = complexListVar;
    }

    public List<String> getStringListVar() {
        return stringListVar;
    }

    public void setStringListVar(List<String> stringListVar) {
        this.stringListVar = stringListVar;
    }
}