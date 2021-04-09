package demo.config;

import com.qfs.server.cfg.impl.ActivePivotConfig;
import com.qfs.server.cfg.impl.ActivePivotServicesConfig;
import com.qfs.server.cfg.impl.ActivePivotXmlaServletConfig;
import com.qfs.server.cfg.impl.DatastoreConfig;
import com.qfs.server.cfg.impl.FullAccessBranchPermissionsManagerConfig;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
@EnableMBeanExport
@Import(value = {
        // Active Pivot Config
        ActivePivotConfig.class,
        LocalContentServiceConfig.class,
        ActivePivotXmlaServletConfig.class,
        ActivePivotServicesConfig.class,
        FullAccessBranchPermissionsManagerConfig.class,
        DatastoreConfig.class,

        // Application Config
        DatastoreDescriptionConfig.class,
        SourceConfig.class,
        OlapConfig.class})
public class ApplicationConfig {

    static {
        // registry is automatically populated based on scanning for type/plugin annotations in the
        // listed packages (and whereby each package may override what was defined by 'previous' ones)
        Registry.setContributionProvider(new ClasspathContributionProvider("com.qfs", "com.quartetfs", "demo"));
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> startUp(IActivePivotManager activePivotManager) {
        return event -> {
            try {
                // Although it was still possible to populate the Datastore and run triggers,
                // it was not possible to run an MDX query via the XMLA service until the Active Pivot
                // manager is initialised and started
                activePivotManager.init(null);
                activePivotManager.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}