package com.paypal.mocca.client.sample;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class SuperComplexResponseType {

    public static class SuperComplexResponseField {

        int innerIntVar;
        String innerStringVar;
        boolean innerBooleanVar;
        List<String> innerStringListVar;

        public SuperComplexResponseField setInnerIntVar(int innerIntVar) {
            this.innerIntVar = innerIntVar;
            return this;
        }

        public SuperComplexResponseField setInnerStringVar(String innerStringVar) {
            this.innerStringVar = innerStringVar;
            return this;
        }

        public SuperComplexResponseField setInnerBooleanVar(boolean innerBooleanVar) {
            this.innerBooleanVar = innerBooleanVar;
            return this;
        }

        public SuperComplexResponseField setInnerStringListVar(List<String> innerStringListVar) {
            this.innerStringListVar = innerStringListVar;
            return this;
        }

        public int getInnerIntVar() {
            return innerIntVar;
        }

        public String getInnerStringVar() {
            return innerStringVar;
        }

        public boolean isInnerBooleanVar() {
            return innerBooleanVar;
        }

        public List<String> getInnerStringListVar() {
            return innerStringListVar;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SuperComplexResponseField that = (SuperComplexResponseField) o;

            if (innerIntVar != that.innerIntVar) return false;
            if (innerBooleanVar != that.innerBooleanVar) return false;
            if (innerStringVar != null ? !innerStringVar.equals(that.innerStringVar) : that.innerStringVar != null) return false;
            return innerStringListVar != null ? innerStringListVar.equals(that.innerStringListVar) : that.innerStringListVar == null;
        }

        @Override
        public int hashCode() {
            int result = innerIntVar;
            result = 31 * result + (innerStringVar != null ? innerStringVar.hashCode() : 0);
            result = 31 * result + (innerBooleanVar ? 1 : 0);
            result = 31 * result + (innerStringListVar != null ? innerStringListVar.hashCode() : 0);
            return result;
        }
    }

    private int intVar;
    private String stringVar;
    private boolean booleanVar;
    private SuperComplexResponseField complexField;
    private List<SuperComplexResponseField> complexListVar;
    private List<String> stringListVar;
    private OffsetDateTime dateTime;
    private String optionalField;

    public int getIntVar() {
        return intVar;
    }

    public SuperComplexResponseType setIntVar(int intVar) {
        this.intVar = intVar;
        return this;
    }

    public String getStringVar() {
        return stringVar;
    }

    public SuperComplexResponseType setStringVar(String stringVar) {
        this.stringVar = stringVar;
        return this;
    }

    public boolean isBooleanVar() {
        return booleanVar;
    }

    public SuperComplexResponseType setBooleanVar(boolean booleanVar) {
        this.booleanVar = booleanVar;
        return this;
    }

    public SuperComplexResponseField getComplexField() {
        return complexField;
    }

    public SuperComplexResponseType setComplexField(SuperComplexResponseField complexField) {
        this.complexField = complexField;
        return this;
    }

    public List<SuperComplexResponseField> getComplexListVar() {
        return complexListVar;
    }

    public void setComplexListVar(List<SuperComplexResponseField> complexListVar) {
        this.complexListVar = complexListVar;
    }

    public List<String> getStringListVar() {
        return stringListVar;
    }

    public void setStringListVar(List<String> stringListVar) {
        this.stringListVar = stringListVar;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public SuperComplexResponseType setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Optional<String> getOptionalField() {
        return Optional.ofNullable(optionalField);
    }

    public SuperComplexResponseType setOptionalField(String optionalField) {
        this.optionalField = optionalField;
        return this;
    }

}