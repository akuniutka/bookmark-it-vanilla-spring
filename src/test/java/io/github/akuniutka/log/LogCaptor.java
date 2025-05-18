package io.github.akuniutka.log;

import java.util.List;

public interface LogCaptor {

    List<LogEvent> getEvents();
}
