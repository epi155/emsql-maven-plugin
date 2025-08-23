package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import org.jetbrains.annotations.NotNull;

public class DocUtils {
    private DocUtils() {}

    public static void docResultList(@NotNull PrintModel ipw, String name) {
        ipw.printf("/**%n");
        ipw.printf(" * Execute the SELECT query and return the query results as a List.<br>%n");
        ipw.printf(" * See {@link #%s }%n", name);
        ipw.printf(" * %n");
        ipw.printf(" * @return result list%n");
        ipw.printf(" * @throws SQLException%n");
        ipw.printf(" */%n");
    }

    public static void docCursorOpen(PrintModel ipw, String name) {
        ipw.printf("/**%n");
        ipw.printf(" * Creates the cursor from the builder and opens it.<br>%n");
        ipw.printf(" * See {@link #%s }%n", name);
        ipw.printf(" * %n");
        ipw.printf(" * @return {@code SqlCursor<O>} instance%n");
        ipw.printf(" * @throws SQLException%n");
        ipw.printf(" */%n");
    }

    public static void docCursorForEach(PrintModel ipw, String name) {
        ipw.printf("/**%n");
        ipw.printf(" * Creates the cursor from the builder and loops through its elements.<br>%n");
        ipw.printf(" * See {@link #%s }%n", name);
        ipw.printf(" * %n");
        ipw.printf(" * @param co item consumer%n");
        ipw.printf(" * @throws SQLException%n");
        ipw.printf(" */%n");
    }

}
