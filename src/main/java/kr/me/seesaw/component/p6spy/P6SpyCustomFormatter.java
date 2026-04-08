package kr.me.seesaw.component.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

/**
 * P6Spy 로그 포맷터.
 */
public class P6SpyCustomFormatter implements MessageFormattingStrategy {

    private static boolean multiline = false;

    /**
     * 멀티라인 설정.
     *
     * @param multiline 멀티라인 여부
     */
    public static void setMultiline(boolean multiline) {
        P6SpyCustomFormatter.multiline = multiline;
    }

    /**
     * @see MessageFormattingStrategy#formatMessage(int, String, long, String, String, String, String)
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if ("statement".equals(category) && sql != null && !sql.trim().isEmpty()) {
            if (multiline) {
                return FormatStyle.BASIC.getFormatter().format("Executed SQL: " + sql + ";");
            }
            return String.format("Executed SQL: %s;", sql);
        }
        return now + " | " + elapsed + "ms | " + category + " | connection " + connectionId + " | " + sql;
    }

}
