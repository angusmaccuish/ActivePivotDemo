package demo;

import demo.config.ApplicationConfig;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ALL")
public class Application implements WebApplicationInitializer {

    /**
     * If running using Jetty Runner:
     * Main-class: com.nomura.fid.core.web.JettyRunner
     * VM options: -Dactivepivot.license=[Path to licence file]
     * Program args: -webapp target\active-pivot-demo-1.0-SNAPSHOT.war -port 8080 -context /demo
     */
    @Override
    public void onStartup(ServletContext servletContext) {
        final AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
        rootAppContext.register(ApplicationConfig.class);

        final DispatcherServlet servlet = new DispatcherServlet(rootAppContext);
        servlet.setDispatchOptionsRequest(true);
        final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("springDispatcherServlet", servlet);
        dispatcher.addMapping("/*");
        dispatcher.setLoadOnStartup(1);
    }

    /**
     * Command line, embedded Jetty server
     * Args: -Dactivepivot.license=[Path to licence file]
     */
    public static void main(String[] args) throws Exception {
        final Server server = new Server(8082);
        server.setStopAtShutdown(true);
        final WebAppContext handler = new WebAppContext();
        handler.setContextPath("/demo");
        handler.setParentLoaderPriority(true);
        handler.setConfigurationClasses(new String[]{Configuration.class.getName()});
        server.setHandler(handler);
        server.start();
        server.join();
    }

    /**
     * Required for Embedded Jetty Server usage.
     * Jetty AnnotationConfiguration only scans jar files, to pick up project classes we
     * need to override preConfigure (as in Proteus)
     * <p>
     * See https://stackoverflow.com/questions/13222071/spring-3-1-webapplicationinitializer-embedded-jetty-8-annotationconfiguration
     */
    public static class Configuration extends AnnotationConfiguration {

        @Override
        public void preConfigure(WebAppContext context) throws Exception {
            final ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
            final ConcurrentHashMap<String, ConcurrentHashSet<String>> map = new AnnotationConfiguration.ClassInheritanceMap();
            set.add(Application.class.getName());
            map.put(WebApplicationInitializer.class.getName(), set);
            context.setAttribute(CLASS_INHERITANCE_MAP, map);
            this._classInheritanceHandler = new ClassInheritanceHandler(map);
        }
    }

}