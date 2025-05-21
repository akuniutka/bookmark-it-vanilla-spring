package io.github.akuniutka.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.NonNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.SearchOption;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class LogCaptureExtension implements TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("LogCaptor");
    private static final String STORE_KEY = "Captor";

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context)
            throws IllegalAccessException {
        final Class<?> loggedClass = getLoggedClass(testInstance.getClass());
        final List<Field> targetFields = getTargetFields(testInstance.getClass());
        if (loggedClass == null || targetFields.isEmpty()) {
            return;
        }
        final LogCaptorImpl captor = new LogCaptorImpl(loggedClass);
        for (Field field : targetFields) {
            field.setAccessible(true);
            field.set(testInstance, captor);
        }
        context.getStore(NAMESPACE).put(STORE_KEY, captor);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        while (context != null) {
            LogCaptorImpl captor = context.getStore(NAMESPACE).get(STORE_KEY, LogCaptorImpl.class);
            if (captor != null) {
                captor.start();
            }
            context = context.getParent().orElse(null);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        while (context != null) {
            LogCaptorImpl captor = context.getStore(NAMESPACE).get(STORE_KEY, LogCaptorImpl.class);
            if (captor != null) {
                captor.stop();
            }
            context = context.getParent().orElse(null);
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
