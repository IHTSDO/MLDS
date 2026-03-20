package ca.intelliware.ihtsdo.mlds.service.util;

public final class TextSanitizer {

    private TextSanitizer() {}

    public static String stripHtml(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String cleaned = input.replaceAll("<[^>]*>", "");

        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }
}
