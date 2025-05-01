package io.github.akuniutka.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class LogListener {

    private final Logger logger;
    private final ListAppender<ILoggingEvent> appender;

    public LogListener(final Class<?> classToListen) {
        Objects.requireNonNull(classToListen);
        this.logger = (Logger) LoggerFactory.getLogger(classToListen);
        this.appender = new ListAppender<>();
    }

    public void startListen() {
        appender.start();
        logger.addAppender(appender);
    }

    public void stopListen() {
        logger.detachAppender(appender);
        appender.stop();
    }

    public List<Event> getEvents() {
        return appender.list.stream()
                .map(entry -> new Event(entry.getLevel().toString(), entry.getFormattedMessage()))
                .toList();
    }

    public void reset() {
        appender.list.clear();
    }

    public record Event(String level, String message) {

    }
}
