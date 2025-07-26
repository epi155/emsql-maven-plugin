package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;

@Getter
public class JdbcDynStatement extends JdbcStatement {
    private final String textPost;

    public JdbcDynStatement(String text, Map<Integer, SqlParam> iMap, Map<Integer, SqlParam> oMap, String post) {
        super(text, iMap, oMap);
        this.textPost = post;
    }
    public void writeQuery(String kPrg, PrintModel ipw) {
        ipw.printf("private static final String Q_%s_ANTE = \"%s\";%n", kPrg, StringEscapeUtils.escapeJava(getText()));
        ipw.printf("private static final String Q_%s_POST = \"%s\";%n", kPrg, StringEscapeUtils.escapeJava(textPost));
    }

}
