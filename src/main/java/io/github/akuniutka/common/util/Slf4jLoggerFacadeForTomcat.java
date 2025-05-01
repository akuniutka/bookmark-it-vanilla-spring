package io.github.akuniutka.common.util;

import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SLF4J facade which provides Tomcat with JULI interface for logging. Inspired by aventurin's
 * <a href="https://stackoverflow.com/a/49266815">post</a> at Stackoverflow.
 *
 * @author Andrei Kuniutka
 * @version 1.0
 */
public class Slf4jLoggerFacadeForTomcat implements Log {

    private final Logger logger;

    // ServiceLoader, which will provide this facade to Tomcat, requires that class has a default constructor
    public Slf4jLoggerFacadeForTomcat() {
        logger = null;
    }

    public Slf4jLoggerFacadeForTomcat(final String loggerName) {
        logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void trace(final Object message) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(String.valueOf(message));
        }
    }

    @Override
    public void trace(final Object message, final Throwable throwable) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(String.valueOf(message), throwable);
        }
    }

    @Override
    public void debug(final Object message) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(String.valueOf(message));
        }
    }

    @Override
    public void debug(final Object message, final Throwable throwable) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(String.valueOf(message), throwable);
        }
    }

    @Override
    public void info(final Object message) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(String.valueOf(message));
        }
    }

    @Override
    public void info(final Object message, final Throwable throwable) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(String.valueOf(message), throwable);
        }
    }

    @Override
    public void warn(final Object message) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(String.valueOf(message));
        }
    }

    @Override
    public void warn(final Object message, final Throwable throwable) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(String.valueOf(message), throwable);
        }
    }

    @Override
    public void error(final Object message) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(String.valueOf(message));
        }
    }

    @Override
    public void error(final Object message, final Throwable throwable) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(String.valueOf(message), throwable);
        }
    }

    @Override
    public void fatal(final Object message) {
        error(message);
    }

    @Override
    public void fatal(final Object message, final Throwable throwable) {
        error(message, throwable);
    }
}
