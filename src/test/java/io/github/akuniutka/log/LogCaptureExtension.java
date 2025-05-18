package io.github.akuniutka.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Slf4j
public class LogCaptureExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Object instance = getInstance(context);
        final Class<?> instanceClass = getInstanceClass(instance);
        final Class<?> loggedClass = getLoggedClass(instanceClass);
        final Field targetField = getTargetField(instanceClass);
        if (loggedClass == null || targetField == null) {
            return;
        }
        targetField.setAccessible(true);
        final LogCaptorImpl logCaptor = new LogCaptorImpl(loggedClass);
        targetField.set(instance, logCaptor);
        logCaptor.start();
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        final Object instance = getInstance(context);
        final Class<?> instanceClass = getInstanceClass(instance);
        final Field targetField = getTargetField(instanceClass);
        if (targetField == null) {
            return;
        }
        targetField.setAccessible(true);
        final Object value = targetField.get(instance);
        if (value instanceof LogCaptorImpl logCaptor) {
            logCaptor.stop();
        }
        targetField.set(instance, null);
    }

    private Object getInstance(final ExtensionContext context) {
        if (context == null || context.getTestInstance().isEmpty()) {
            return null;
        }
        return context.getTestInstance().get();
    }

    private Class<?> getInstanceClass(final Object instance) {
        if (instance == null) {
            return null;
        }
        return instance.getClass();
    }

    private Class<?> getLoggedClass(final Class<?> instanceClass) {
        if (instanceClass == null) {
            return null;
        }
        WithLogCapture annotation = instanceClass.getAnnotation(WithLogCapture.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value();
    }

    private Field getTargetField(final Class<?> instanceClass) {
        if (instanceClass == null) {
            return null;
        }
        final Field[] fields = instanceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectLogCaptor.class) && field.getType() == LogCaptor.class) {
                return field;
            }
        }
        return null;
    }

    private static class LogCaptorImpl implements LogCaptor {

        private final Logger logger;
        private final ListAppender<ILoggingEvent> appender;
        private boolean isRunning;

        LogCaptorImpl(final Class<?> loggedClass) {
            Objects.requireNonNull(loggedClass);
            this.logger = (Logger) LoggerFactory.getLogger(loggedClass);
            this.appender = new ListAppender<>();
            this.isRunning = false;
        }

        void start() {
            if (isRunning) {
                return;
            }
            appender.start();
            logger.addAppender(appender);
            isRunning = true;
        }

        void stop() {
            if (!isRunning) {
                return;
            }
            logger.detachAppender(appender);
            appender.stop();
            isRunning = false;
        }

        @Override
        public List<LogEvent> getEvents() {
            return appender.list.stream()
                    .map(entry -> new LogEvent(entry.getLevel().toString(), entry.getFormattedMessage()))
                    .toList();
        }
    }
}
