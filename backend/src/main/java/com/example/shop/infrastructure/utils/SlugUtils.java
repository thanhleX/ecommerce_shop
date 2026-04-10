package com.example.shop.infrastructure.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input) {
        if (input == null) return "";
        // Replace Vietnamese characters specific mapping if needed, 
        // Normalizer handles general acute/grave well but đ needs manual replace.
        String replaced = input.replaceAll("đ", "d").replaceAll("Đ", "D");
        String nowhitespace = WHITESPACE.matcher(replaced).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-");
        return EDGESDASHES.matcher(slug).replaceAll("");
    }
}
