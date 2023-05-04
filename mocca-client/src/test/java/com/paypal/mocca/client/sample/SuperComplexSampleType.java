package com.paypal.mocca.client.sample;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SuperComplexField that = (SuperComplexField) o;

            if (innerIntVar != that.innerIntVar) return false;
            if (innerBooleanVar != that.innerBooleanVar) return false;
            if (innerStringVar != null ? !innerStringVar.equals(that.innerStringVar) : that.innerStringVar != null) return false;
            if (innerStringListVar != null ? !innerStringListVar.equals(that.innerStringListVar) : that.innerStringListVar != null) return false;
            if (innerComplexVar != null ? !innerComplexVar.equals(that.innerComplexVar) : that.innerComplexVar != null) return false;
            return innerComplexListVar != null ? innerComplexListVar.equals(that.innerComplexListVar) : that.innerComplexListVar == null;
        }

        @Override
        public int hashCode() {
            int result = innerIntVar;
            result = 31 * result + (innerStringVar != null ? innerStringVar.hashCode() : 0);
            result = 31 * result + (innerBooleanVar ? 1 : 0);
            result = 31 * result + (innerStringListVar != null ? innerStringListVar.hashCode() : 0);
            result = 31 * result + (innerComplexVar != null ? innerComplexVar.hashCode() : 0);
            result = 31 * result + (innerComplexListVar != null ? innerComplexListVar.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return new StringJoiner(",\n", SuperComplexField.class.getSimpleName() + "[\n", "]")
                    .add("innerIntVar=" + innerIntVar)
                    .add("innerStringVar='" + innerStringVar + "'")
                    .add("innerBooleanVar=" + innerBooleanVar)
                    .add("innerStringListVar=" + innerStringListVar)
                    .add("innerComplexVar=" + innerComplexVar)
                    .add("innerComplexListVar=" + innerComplexListVar)
                    .toString();
        }
    }

    private int intVar;
    private String stringVar;
    private boolean booleanVar;
    private SuperComplexField complexField;
    private List<SuperComplexField> complexListVar;
    private List<String> stringListVar;
    private Set<String> stringSetVar;
    private OffsetDateTime dateTime;
    private String optionalField;
    private Duration duration;
    private UUID uuid;

    public SuperComplexSampleType(int intVar, String stringVar, boolean booleanVar, SuperComplexField complexField,
                                  List<SuperComplexField> complexListVar, List<String> stringListVar,
                                  Set<String> stringSetVar) {
        this.intVar = intVar;
        this.stringVar = stringVar;
        this.booleanVar = booleanVar;
        this.complexField = complexField;
        this.complexListVar = complexListVar;
        this.stringListVar = stringListVar;
        this.stringSetVar = stringSetVar;
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

    public Set<String> getStringSetVar() {
        return stringSetVar;
    }

    public void setStringSetVar(Set<String> stringSetVar) {
        this.stringSetVar = stringSetVar;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public SuperComplexSampleType setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Optional<String> getOptionalField() {
        return Optional.ofNullable(optionalField);
    }

    public SuperComplexSampleType setOptionalField(String optionalField) {
        this.optionalField = optionalField;
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public SuperComplexSampleType setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public SuperComplexSampleType setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SuperComplexSampleType.class.getSimpleName() + "[", "]")
                .add("intVar=" + intVar)
                .add("stringVar='" + stringVar + "'")
                .add("booleanVar=" + booleanVar)
                .add("complexField=" + complexField)
                .add("complexListVar=" + complexListVar)
                .add("stringListVar=" + stringListVar)
                .add("dateTime=" + dateTime)
                .add("optionalField='" + optionalField + "'")
                .add("duration=" + duration)
                .add("uuid=" + uuid)
                .toString();
    }

}