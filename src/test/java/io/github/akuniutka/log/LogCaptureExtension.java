package io.github.akuniutka.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.SearchOption;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Slf4j
public class LogCaptureExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(@NonNull final ExtensionContext context) throws Exception {
        if (context.getTestInstance().isEmpty() || context.getTestClass().isEmpty()) {
            return;
        }
        final Object testInstance = context.getTestInstance().get();
        final Class<?> testClass = context.getTestClass().get();
        final Class<?> loggedClass = getLoggedClass(testClass);
        final List<Field> targetFields = getTargetFields(testClass);
        if (loggedClass == null || targetFields.isEmpty()) {
            return;
        }
        final LogCaptorImpl logCaptor = new LogCaptorImpl(loggedClass);
        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            targetField.set(testInstance, logCaptor);
        }
        logCaptor.start();
    }

    @Override
    public void afterEach(@NonNull final ExtensionContext context) throws Exception {
        if (context.getTestInstance().isEmpty() || context.getTestClass().isEmpty()) {
            return;
        }
        final Object testInstance = context.getTestInstance().get();
        final Class<?> testClass = context.getTestClass().get();
        final List<Field> targetFields = getTargetFields(testClass);
        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            final Object value = targetField.get(testInstance);
            if (value instanceof LogCaptorImpl logCaptor) {
                logCaptor.stop();
            }
            targetField.set(testInstance, null);
        }
    }

    private Class<?> getLoggedClass(final Class<?> instanceClass) {
        return AnnotationSupport.findAnnotation(instanceClass, WithLogCapture.class,
                SearchOption.INCLUDE_ENCLOSING_CLASSES).map(WithLogCapture::value).orElse(null);
    }

    private List<Field> getTargetFields(@NonNull final Class<?> instanceClass) {
        return AnnotationSupport.findAnnotatedFields(instanceClass, InjectLogCaptor.class,
                field -> field.getType() == LogCaptor.class, HierarchyTraversalMode.TOP_DOWN);
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
