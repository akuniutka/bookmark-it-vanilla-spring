package io.github.akuniutka.log;

import java.util.ArrayList;
import java.util.List;

public class LogEvents {

    private LogEvents() {
        throw new AssertionError();
    }

    public static List<LogEvent> of(final String... values) {
        final List<LogEvent> events = new ArrayList<>();
        String level = null;
        for (String value : values) {
            if (level == null) {
                level = value;
            } else {
                events.add(new LogEvent(level, value));
                level = null;
            }
        }
        if (level != null) {
            events.add(new LogEvent(level, null));
        }
        return events;
    }
}
