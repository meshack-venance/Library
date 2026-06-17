package com.shacky.library.common.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class NameFormatter {

    private NameFormatter() {
    }

    public static String fullName(String... parts) {
        return Arrays.stream(parts)
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(" "));
    }
}
