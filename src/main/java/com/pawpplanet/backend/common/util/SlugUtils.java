package com.pawpplanet.backend.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating URL-friendly slugs from text.
 * Handles Vietnamese characters, special characters, and ensures proper formatting.
 */
@Slf4j
public class SlugUtils {

    private static final Map<String, String> VIETNAMESE_MAP = new HashMap<>();

    static {
        // Vowels with tones
        VIETNAMESE_MAP.put("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        VIETNAMESE_MAP.put("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        VIETNAMESE_MAP.put("ì|í|ị|ỉ|ĩ", "i");
        VIETNAMESE_MAP.put("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        VIETNAMESE_MAP.put("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        VIETNAMESE_MAP.put("ỳ|ý|ỵ|ỷ|ỹ", "y");
        VIETNAMESE_MAP.put("đ", "d");

        // Uppercase
        VIETNAMESE_MAP.put("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ", "a");
        VIETNAMESE_MAP.put("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ", "e");
        VIETNAMESE_MAP.put("Ì|Í|Ị|Ỉ|Ĩ", "i");
        VIETNAMESE_MAP.put("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ", "o");
        VIETNAMESE_MAP.put("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ", "u");
        VIETNAMESE_MAP.put("Ỳ|Ý|Ỵ|Ỷ|Ỹ", "y");
        VIETNAMESE_MAP.put("Đ", "d");
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SlugUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generate a URL-friendly slug from the given text.
     *
     * Examples:
     * - "Golden Retriever" -> "golden-retriever"
     * - "Chó Becgie Đức" -> "cho-becgie-duc"
     * - "Persian Cat!!!" -> "persian-cat"
     * - "  Multiple   Spaces  " -> "multiple-spaces"
     *
     * @param text The text to convert to slug
     * @return URL-friendly slug (lowercase, alphanumeric with hyphens)
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Attempted to generate slug from null or empty text");
            return "";
        }

        String slug = text.trim();

        // Convert Vietnamese characters to ASCII equivalents
        slug = removeVietnameseTones(slug);

        // Normalize to NFD (decomposed form) then remove diacritics
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", ""); // Remove all diacritic marks

        // Convert to lowercase
        slug = slug.toLowerCase();

        // Replace spaces and underscores with hyphens
        slug = slug.replaceAll("[\\s_]+", "-");

        // Remove all non-alphanumeric characters except hyphens
        slug = slug.replaceAll("[^a-z0-9-]", "");

        // Replace multiple consecutive hyphens with single hyphen
        slug = slug.replaceAll("-+", "-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        if (slug.isEmpty()) {
            log.warn("Generated slug is empty after processing text: {}", text);
        }

        log.debug("Generated slug '{}' from text '{}'", slug, text);
        return slug;
    }

    /**
     * Generate a unique slug by appending a number if the slug already exists.
     *
     * @param baseSlug The base slug to start from
     * @param existsChecker A function that checks if a slug already exists
     * @return A unique slug
     */
    public static String generateUniqueSlug(String baseSlug, java.util.function.Function<String, Boolean> existsChecker) {
        String slug = baseSlug;
        int counter = 1;

        while (existsChecker.apply(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;

            if (counter > 1000) {
                log.error("Failed to generate unique slug after 1000 attempts for base: {}", baseSlug);
                throw new IllegalStateException("Unable to generate unique slug");
            }
        }

        if (!slug.equals(baseSlug)) {
            log.debug("Generated unique slug '{}' from base '{}'", slug, baseSlug);
        }

        return slug;
    }

    /**
     * Remove Vietnamese tones and convert to ASCII.
     *
     * @param text The text containing Vietnamese characters
     * @return Text with Vietnamese characters replaced by ASCII equivalents
     */
    private static String removeVietnameseTones(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : VIETNAMESE_MAP.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Validate if a string is a valid slug format.
     *
     * @param slug The slug to validate
     * @return true if valid slug format, false otherwise
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        // Slug must be lowercase alphanumeric with hyphens
        // Cannot start or end with hyphen
        // Cannot have consecutive hyphens
        return slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$");
    }

    /**
     * Normalize an existing slug to ensure it follows proper format.
     * Useful for cleaning up user-provided slugs.
     *
     * @param slug The slug to normalize
     * @return Normalized slug
     */
    public static String normalizeSlug(String slug) {
        return generateSlug(slug);
    }
}

