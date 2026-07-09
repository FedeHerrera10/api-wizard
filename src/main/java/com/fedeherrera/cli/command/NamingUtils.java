package com.fedeherrera.cli.command;

final class NamingUtils {

    private NamingUtils() {}

    static String toPascalCase(String kebabCase) {
        StringBuilder sb = new StringBuilder();
        for (String part : kebabCase.split("-")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1));
                }
            }
        }
        return sb.toString();
    }

    static String toSnakeCase(String kebabCase) {
        return kebabCase.replace("-", "_");
    }

    static String extractGroupId(String basePackage) {
        int lastDot = basePackage.lastIndexOf('.');
        return lastDot > 0 ? basePackage.substring(0, lastDot) : basePackage;
    }
}
