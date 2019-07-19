package com.squarepolka.readyci.configuration.parameter;

public class ParsedParameter {
    private String parameterKey;
    private String parameterValue;

    public ParsedParameter(String parameterArgument) {
        String[] parsedParameter = parameterArgument.split("=");
        if (parsedParameter.length >= 2) {
            this.parameterKey = parsedParameter[0];
            this.parameterValue = parsedParameter[1];
        } else {
            throw new ParameterParseException(parameterArgument);
        }
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
