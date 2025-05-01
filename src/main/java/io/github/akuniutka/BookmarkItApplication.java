package io.github.akuniutka;

import io.github.akuniutka.config.ApplicationConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class BookmarkItApplication {

    private static final int PORT = 8080;

    public static void main(final String[] args) throws LifecycleException {
        final Tomcat tomcat = new Tomcat();
        tomcat.getConnector().setPort(PORT);

        /*
         * Create context in terms of Tomcat (a set of servlets that can communicate with each other and all together
         * form a web application). Each web application has base path (here it is an empty string).
         */
        final Context tomcatContext = tomcat.addContext("", null);

        final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.setServletContext(tomcatContext.getServletContext());
        applicationContext.register(ApplicationConfig.class);
        applicationContext.refresh();

        final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        final Wrapper dispatcherWrapper = Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet);
        dispatcherWrapper.addMapping("/");
        dispatcherWrapper.setLoadOnStartup(1);

        tomcat.start();
    }
}
