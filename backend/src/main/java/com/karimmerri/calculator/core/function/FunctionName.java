package com.karimmerri.calculator.core.function;

import java.util.Locale;
import java.util.Optional;

public enum FunctionName {
    SQRT("sqrt");

    private final String identifier;

    FunctionName(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public static Optional<FunctionName> fromIdentifier(String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }

        String normalized = identifier.toLowerCase(Locale.ROOT);

        for (FunctionName function : values()) {
            if (function.identifier.equals(normalized)) {
                return Optional.of(function);
            }
        }

        return Optional.empty();
    }
}
