To be unpacked:

pom.xml

<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.nomura.rcit.monju</groupId>
    <artifactId>active-pivot-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>active-pivot-demo</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <activepivot.server.version>5.8.16-jdk8</activepivot.server.version>
        <javax.servlet.api.version>3.1.0</javax.servlet.api.version>
        <junit.version>4.11</junit.version>
        <activepivot.license/>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- import dependencies specifications related to core product ActivePivot Server POM -->
            <dependency>
                <groupId>com.activeviam.activepivot</groupId>
                <artifactId>activepivot-server-spring</artifactId>
                <version>${activepivot.server.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- ActiveViam dependencies -->
            <dependency>
                <groupId>com.activeviam.tech</groupId>
                <artifactId>composer-impl</artifactId>
            </dependency>
            <dependency>
                <groupId>com.activeviam.tech</groupId>
                <artifactId>composer-api</artifactId>
            </dependency>
            <dependency>
                <groupId>com.activeviam.tech</groupId>
                <artifactId>content-server-storage</artifactId>
            </dependency>
            <dependency>
                <groupId>com.activeviam.web</groupId>
                <artifactId>activeviam-web-spring</artifactId>
            </dependency>

            <dependency>
                <!-- required to avoid pollution by concrete logging frameworks transitively
                    pulled from library dependency -->
                <groupId>quartetfs.biz.pivot</groupId>
                <artifactId>activepivot-server-spring</artifactId>
                <version>${activepivot.server.version}</version>
                <exclusions>
                    <exclusion>
                        <groupId>commons-logging</groupId>
                        <artifactId>commons-logging</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>

            <!-- required for compilation here, but ultimately provided by servlet container at runtime -->
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>javax.servlet-api</artifactId>
                <version>${javax.servlet.api.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- Runtime -->
            <dependency>
                <groupId>com.nomura.fid.core</groupId>
                <artifactId>jetty-runner</artifactId>
                <version>1.2.0</version>
            </dependency>

            <!-- Testing -->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>${junit.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>com.activeviam.activepivot</groupId>
            <artifactId>activepivot-server-spring</artifactId>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
        </dependency>
        <dependency>
            <groupId>com.nomura.fid.core</groupId>
            <artifactId>jetty-runner</artifactId>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </dependency>
    </dependencies>

    <build>
        <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
            <plugins>
                <plugin>
                    <artifactId>maven-clean-plugin</artifactId>
                    <version>3.0.0</version>
                </plugin>
                <!-- see http://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_jar_packaging -->
                <plugin>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>3.0.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.7.0</version>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>2.20.1</version>
                </plugin>
                <plugin>
                    <artifactId>maven-jar-plugin</artifactId>
                    <version>3.0.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-install-plugin</artifactId>
                    <version>2.5.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <version>2.8.2</version>
                </plugin>
                <!-- WAR packaging (we tell Maven not to bother about web.xml not being present) -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-war-plugin</artifactId>
                    <configuration>
                        <failOnMissingWebXml>false</failOnMissingWebXml>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>exec-maven-plugin</artifactId>
                    <version>1.6.0</version>
                </plugin>
            </plugins>
        </pluginManagement>

        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>java</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <mainClass>demo.Application</mainClass>
                    <systemProperties>
                        <systemProperty>
                            <key>activepivot.license</key>
                            <value>${activepivot.license}</value>
                        </systemProperty>
                    </systemProperties>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

-----------------------------------------------------------------------------------------------------

src/main/java/demo/Application.java

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

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/AggregatedMeasuresConfig.java

package demo.config;

import com.quartetfs.biz.pivot.definitions.impl.AggregatedMeasureDescription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static demo.config.StoresConfig.SALES;

@Configuration
public class AggregatedMeasuresConfig {

    /**
     * Add any simple aggregated measures here (use postprocessors to combine these and enhance)
     * For example, this sales measures can be referenced in MDX as [Measures].[Sales.SUM]
     */
    @Bean
    @Qualifier("DefaultMeasure")
    public AggregatedMeasureDescription totalSales() {
        return new AggregatedMeasureDescription(SALES, "SUM");
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/ApplicationConfig.java

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

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/ContextConfig.java

package demo.config;

import com.quartetfs.biz.pivot.context.impl.MdxContext;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Miscellaneous context configuration e.g. timeouts etc
 */
@Configuration
public class ContextConfig {

    @Bean
    public QueriesTimeLimit queryTimeLimit(@Value("${query.timeout.sec:30}") int seconds) {
        return QueriesTimeLimit.of(seconds, TimeUnit.SECONDS);
    }

    @Bean
    public MdxContext defaultMdxContext() {
        final MdxContext mdxContext = new MdxContext();
        mdxContext.setAggressiveFormulaEvaluation(true);
        return mdxContext;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/CubeConfig.java

package demo.config;

import com.quartetfs.biz.pivot.context.IContextValue;
import com.quartetfs.biz.pivot.definitions.IActivePivotDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IAggregatedMeasureDescription;
import com.quartetfs.biz.pivot.definitions.IAggregatesCacheDescription;
import com.quartetfs.biz.pivot.definitions.IAxisDimensionDescription;
import com.quartetfs.biz.pivot.definitions.IAxisDimensionsDescription;
import com.quartetfs.biz.pivot.definitions.IContextValuesDescription;
import com.quartetfs.biz.pivot.definitions.IMeasureMemberDescription;
import com.quartetfs.biz.pivot.definitions.IMeasuresDescription;
import com.quartetfs.biz.pivot.definitions.INativeMeasureDescription;
import com.quartetfs.biz.pivot.definitions.IPostProcessorDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotSchemaDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotSchemaInstanceDescription;
import com.quartetfs.biz.pivot.definitions.impl.AggregatesCacheDescription;
import com.quartetfs.biz.pivot.definitions.impl.AxisDimensionsDescription;
import com.quartetfs.biz.pivot.definitions.impl.ContextValuesDescription;
import com.quartetfs.biz.pivot.definitions.impl.MeasuresDescription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;

/**
 * No need to change this file, it pulls in beans from imported config
 * (unless you want to add a new cubes/schemas, for example)
 */
@Configuration
@Import(value = {
        FactsConfig.class,
        DimensionsConfig.class,
        AggregatedMeasuresConfig.class,
        PostProcessorConfig.class,
        NativeMeasuresConfig.class,
        ContextConfig.class})
public class CubeConfig {

    private static final String DEMO_SCHEMA_ID = "DEMO";
    private static final String DEMO_CUBE_ID = "DEMO";

    @Bean
    public ActivePivotSchemaInstanceDescription drcSchema(ISelectionDescription facts,
                                                          List<? extends IActivePivotInstanceDescription> cubes) {

        final ActivePivotSchemaDescription schema = new ActivePivotSchemaDescription();
        schema.setDatastoreSelection(facts);
        schema.setActivePivotInstanceDescriptions(cubes);

        return new ActivePivotSchemaInstanceDescription(DEMO_SCHEMA_ID, schema);
    }

    @Bean
    public ActivePivotInstanceDescription drcCube(IAxisDimensionsDescription dimensions,
                                                  IMeasuresDescription measures,
                                                  IContextValuesDescription sharedContexts,
                                                  IAggregatesCacheDescription aggregatesCache) {

        final IActivePivotDescription activePivotDescription = new ActivePivotDescription();
        activePivotDescription.setAxisDimensions(dimensions);
        activePivotDescription.setMeasuresDescription(measures);
        activePivotDescription.setSharedContexts(sharedContexts);
        activePivotDescription.setAutoFactlessHierarchies(true);
        activePivotDescription.setAggregatesCacheDescription(aggregatesCache);

        return new ActivePivotInstanceDescription(DEMO_CUBE_ID, activePivotDescription);
    }

    @Bean
    public IAxisDimensionsDescription axisDimensionsDescription(List<IAxisDimensionDescription> dimensions) {
        return new AxisDimensionsDescription(dimensions);
    }

    @Bean
    public MeasuresDescription measures(List<IAggregatedMeasureDescription> aggregationMeasures,
                                        List<IPostProcessorDescription> postProcessors,
                                        List<INativeMeasureDescription> nativeMeasures,
                                        @Qualifier("DefaultMeasure") List<IMeasureMemberDescription> defaultMeasures) {

        final MeasuresDescription measuresDescription = new MeasuresDescription();
        measuresDescription.setAggregatedMeasuresDescription(aggregationMeasures);
        measuresDescription.setPostProcessorsDescription(postProcessors);
        measuresDescription.setNativeMeasures(nativeMeasures);

        if (Objects.nonNull(defaultMeasures)) {
            measuresDescription.setDefaultMeasures(
                    defaultMeasures.stream()
                            .map(IMeasureMemberDescription::getName)
                            .toArray(String[]::new));
        }

        return measuresDescription;
    }

    @Bean
    public ContextValuesDescription sharedContexts(List<IContextValue> contextValues) {
        return new ContextValuesDescription(contextValues);
    }

    @Bean
    public AggregatesCacheDescription aggregateCache(
            @Value("${aggregates.cache.size:0}") int aggregatesCacheSizeInMillion) {

        return aggregatesCacheSizeInMillion > 0 ?
                new AggregatesCacheDescription(aggregatesCacheSizeInMillion * 1_000_000, true, null, null) :
                new AggregatesCacheDescription();
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/DatastoreDescriptionConfig.java

package demo.config;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.server.cfg.IDatastoreDescriptionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * No need to change this file, it pulls in beans from imported configs
 */
@Configuration
@Import({StoresConfig.class, TriggersConfig.class})
public class DatastoreDescriptionConfig implements IDatastoreDescriptionConfig {

    private List<IStoreDescription> stores;
    private List<IReferenceDescription> references;

    @Bean
    @Override
    public IDatastoreSchemaDescription schemaDescription() {
        return new DatastoreSchemaDescription(stores, references);
    }

    @Autowired
    public void setStores(List<IStoreDescription> stores) {
        this.stores = stores;
    }

    @Autowired
    public void setReferences(List<IReferenceDescription> references) {
        this.references = references;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/DimensionsConfig.java

package demo.config;

import com.quartetfs.biz.pivot.definitions.IAxisDimensionDescription;
import com.quartetfs.biz.pivot.definitions.impl.AxisDimensionDescription;
import com.quartetfs.biz.pivot.definitions.impl.AxisHierarchyDescription;
import com.quartetfs.biz.pivot.definitions.impl.AxisLevelDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Dimensions, which has a name and a list of hierarchies, which in turn have a name and list of levels
 * MDX axis: [Dimension].[Hierarchy].[Level]
 */
@SuppressWarnings("Duplicates")
@Configuration
public class DimensionsConfig {

    /**
     * [Company].[Company].[Company]
     */
    @Bean
    public IAxisDimensionDescription company() {
        final AxisDimensionDescription dimension = new AxisDimensionDescription();
        dimension.setName(StoresConfig.COMPANY);

        final AxisHierarchyDescription hierarchy = new AxisHierarchyDescription();
        hierarchy.setName(StoresConfig.COMPANY);
        hierarchy.setLevels(Collections.singletonList(new AxisLevelDescription(StoresConfig.COMPANY)));

        dimension.setHierarchies(Collections.singletonList(hierarchy));

        return dimension;
    }

    /**
     * [Year].[Year].[Year]
     */
    @Bean
    public IAxisDimensionDescription year() {
        final AxisDimensionDescription dimension = new AxisDimensionDescription();
        dimension.setName(StoresConfig.YEAR);

        final AxisHierarchyDescription hierarchy = new AxisHierarchyDescription();
        hierarchy.setName(StoresConfig.YEAR);
        hierarchy.setLevels(Collections.singletonList(new AxisLevelDescription(StoresConfig.YEAR)));

        dimension.setHierarchies(Collections.singletonList(hierarchy));

        return dimension;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/FactsConfig.java

package demo.config;

import com.qfs.store.selection.impl.SelectionField;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import com.quartetfs.biz.pivot.definitions.impl.SelectionDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class FactsConfig {

    /**
     * "Facts" - collection of fields from a store
     */
    @Bean
    public ISelectionDescription resultsFacts() {
        return new SelectionDescription(
                StoresConfig.RESULTS_STORE,
                Arrays.asList(
                        new SelectionField(StoresConfig.COMPANY),
                        new SelectionField(StoresConfig.YEAR),
                        new SelectionField(StoresConfig.SALES)
                )
        );
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/LocalContentServiceConfig.java

package demo.config;

import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

    private IActivePivotManagerDescription manager;

    @Override
    public IActivePivotContentService activePivotContentService() {
        return new ActivePivotContentServiceBuilder()
                .withoutPersistence()
                .withoutCache()
                .needInitialization("ROLE_USER", "ROLE_USER")
                .withDescription(manager)
                .withContextValues("ROLE-INF")
                .build();
    }

    @Required
    @Autowired
    public void setManager(IActivePivotManagerDescription manager) {
        this.manager = manager;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/NativeMeasuresConfig.java

package demo.config;

import com.quartetfs.biz.pivot.definitions.impl.NativeMeasureDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.quartetfs.biz.pivot.cube.hierarchy.measures.impl.AggregatedMeasure.NATIVE_MEASURES_NAMES;

@Configuration
public class NativeMeasuresConfig {

    /**
     * [Measures].[contributors.COUNT]
     */
    @Bean
    public NativeMeasureDescription contributorsCount() {
        final NativeMeasureDescription measure = new NativeMeasureDescription();
        measure.setName(NATIVE_MEASURES_NAMES.get(0));
        measure.setFormatter("INT[#,###]");
        return measure;
    }

    /**
     * [Measures].[update.TIMESTAMP]
     */
    @Bean
    public NativeMeasureDescription updateTimestamp() {
        final NativeMeasureDescription measure = new NativeMeasureDescription();
        measure.setName(NATIVE_MEASURES_NAMES.get(1));
        measure.setFormatter("DATE[HH:mm:ss]");
        return measure;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/OlapConfig.java

package demo.config;

import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotSchemaInstanceDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.impl.CatalogDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * No need to change this file, it pulls in beans from imported config
 * (unless you want to add a new catalog, for example - this example has one cube in one schema in one catalog)
 */
@Configuration
@Import(CubeConfig.class)
public class OlapConfig implements IActivePivotManagerDescriptionConfig {

    private static final String CATALOG_ID = "DemoCatalog";

    private List<? extends IActivePivotSchemaInstanceDescription> schemas;

    /**
     * Elsewhere in the configuration, we have created Active Pivot schema instance(s), which we now
     * register with the manager. Assume a single catalog, which contains all cube ids across schema(s).
     * NB there is no reason to ever change this file, unless there is a need to introduce separate
     * catalogs to group cubes.
     */
    @Bean
    @Override
    public IActivePivotManagerDescription managerDescription() {
        // Extract all cube id's and place in the Catalog
        final List<String> cubeIds = getCubeIds();
        final CatalogDescription catalog = new CatalogDescription(CATALOG_ID, cubeIds);

        // Manager encapsulates the Catalog and the Schema instance(s)
        final ActivePivotManagerDescription manager = new ActivePivotManagerDescription();
        manager.setCatalogs(Collections.singletonList(catalog));
        manager.setSchemas(schemas);
        return manager;
    }

    @Required
    @Autowired
    public void setSchemas(List<? extends IActivePivotSchemaInstanceDescription> schemas) {
        this.schemas = schemas;
    }

    private List<String> getCubeIds() {
        return schemas.stream()
                .map(IActivePivotSchemaInstanceDescription::getActivePivotSchemaDescription)
                .flatMap(o -> o.getActivePivotInstanceDescriptions().stream())
                .map(IActivePivotInstanceDescription::getId)
                .collect(toList());
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/PostProcessorConfig.java

package demo.config;

import com.quartetfs.biz.pivot.definitions.IPostProcessorDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class PostProcessorConfig {

    /**
     * In the absence of any specific PostProcessor beans, include this emply List bean for Autowiring into schema.
     */
    @Bean
    public List<IPostProcessorDescription> none() {
        return Collections.emptyList();
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/SourceConfig.java

package demo.config;

import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.ICSVSource;
import com.qfs.msg.csv.ICSVTopic;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.filesystem.impl.SingleFileCSVTopic;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.IStoreMessageChannelFactory;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SourceConfig {

    /**
     * Source groups topics of the same type
     * e.g. Once source for CSV, one for Avro, one for Webservice, etc
     */
    @Bean
    public CSVSource<Path> csvSource(List<ICSVTopic<Path>> topics) {
        final CSVSource<Path> source = new CSVSource<>();
        topics.forEach(source::addTopic);
        return source;
    }

    /**
     * CSV Topic specifies file name, columns and if header line to be excluded
     */
    @Bean
    public SingleFileCSVTopic topic() {
        final CSVParserConfiguration configuration = new CSVParserConfiguration();
        configuration.setColumnCount(3);
        configuration.setColumns(CSVParserConfiguration.toMap(Arrays.asList(StoresConfig.COMPANY, StoresConfig.YEAR, StoresConfig.SALES)));
        configuration.setNumberSkippedLines(1);
        return new SingleFileCSVTopic("ResultsTopic", configuration, "target/classes/results.csv", 0);
    }

    /**
     * Channel Factory creates Channels which link Sources to Datastore
     */
    @Bean
    public CSVMessageChannelFactory<Path> channelFactory(ICSVSource<Path> source, IDatastore datastore) {
        return new CSVMessageChannelFactory<>(source, datastore);
    }

    /**
     * Channel links Topic to a specific store in the Datastore
     */
    @Bean
    public IMessageChannel<IFileInfo<Path>, ILineReader> channel(
            ICSVTopic<Path> topic,
            IStoreMessageChannelFactory<IFileInfo<Path>, ILineReader> factory) {

        final String topicName = topic.getName();
        return factory.createChannel(topicName, StoresConfig.RESULTS_STORE);
    }

    /**
     * Load CSV when application starts
     */
    @Bean
    public ApplicationListener<ContextRefreshedEvent> loadCsv(
            IDatastore datastore,
            ICSVSource<Path> source,
            List<IMessageChannel<IFileInfo<Path>, ILineReader>> channels) {

        return event -> {
            try {
                System.out.println("Fetching CSV data...");
                datastore.getTransactionManager().startTransaction();
                source.fetch(channels);
                datastore.getTransactionManager().commitTransaction();
                System.out.println("Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/StoresConfig.java

package demo.config;

import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

import static com.qfs.literal.ILiteralType.*;

@SuppressWarnings("WeakerAccess")
@Configuration
public class StoresConfig {

    public static final String RESULTS_STORE = "Results";
    public static final String COMPANY = "Company";
    public static final String YEAR = "Year";
    public static final String SALES = "Sales";

    /**
     * Basic store, columns/types with key fields specified.
     * Can add partitioning etc for performance.
     */
    @Bean
    public IStoreDescription resultsStore() {
        return new StoreDescriptionBuilder()
                .withStoreName(RESULTS_STORE)
                .withField(COMPANY, STRING).asKeyField()
                .withField(YEAR, INT).asKeyField()
                .withField(SALES, DOUBLE)
                .build();
    }

    /**
     * In the absence of any reference beans, to make config work simple have an empty list of references,
     * which will be autowired into the parent config
     */
    @Bean
    public List<IReferenceDescription> references() {
        return Collections.emptyList();
    }

}

-----------------------------------------------------------------------------------------------------

src/main/java/demo/config/TriggersConfig.java

package demo.config;

import com.qfs.chunk.IArrayReader;
import com.qfs.chunk.IArrayWriter;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.store.IDatastore;
import com.qfs.store.record.IRecordFormat;
import com.qfs.store.selection.impl.Selection;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager.IUpdateWhereProcedure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TriggersConfig {

    /**
     * May not be needed, added to Proteus DRC recently to cope with 'adjustments' ie intraday
     * overrides to risk weights which need to update the cube. This trigger doesn't update any
     * fields, it merely logs the data in the Results store
     */
    @Bean
    public IUpdateWhereProcedure trigger(IDatastore datastore) throws DatastoreTransactionException {
        final IUpdateWhereProcedure procedure = new IUpdateWhereProcedure() {

            private int companyIndex;
            private int yearIndex;
            private int salesIndex;

            @Override
            public void init(IRecordFormat input, IRecordFormat output) {
                companyIndex = input.getFieldIndex(StoresConfig.COMPANY);
                yearIndex = input.getFieldIndex(StoresConfig.YEAR);
                salesIndex = input.getFieldIndex(StoresConfig.SALES);
            }

            @Override
            public void execute(IArrayReader reader, IArrayWriter writer) {
                System.out.printf(
                        "Company: %s Year: %s Sales: %s",
                        reader.read(companyIndex),
                        reader.read(yearIndex),
                        reader.read(salesIndex));
            }

        };

        datastore.getTransactionManager().registerCommitTimeUpdateWhereTrigger(
                "Test Trigger",
                0,
                new Selection(StoresConfig.RESULTS_STORE, StoresConfig.COMPANY, StoresConfig.YEAR, StoresConfig.SALES),
                BaseConditions.True(),
                procedure);

        return procedure;
    }

}

-----------------------------------------------------------------------------------------------------

src/main/resources/results.csv

Company,Year,Sales
ACME,2016,1000000
ACME,2017,2000000
ACME,2018,3000000
IBM,2017,10000000
IBM,2018,20000000
-----------------------------------------------------------------------------------------------------

src/main/resources/ROLE-INF/ROLE_ADMIN/global.xml

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<contexts xmlns="http://www.quartetfs.com"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.quartetfs.com ../../activepivot-types.xsd">

	<!-- NOTICE - whatever is defined in this global.xml file will apply to ANY pivot (for the associated role) -->

	<subCubeProperties isAccessGranted="true">
		<grantedMeasures />
	</subCubeProperties>
	
</contexts>
-----------------------------------------------------------------------------------------------------

src/main/resources/serializer.jaxb.cfg.xml

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">

<!-- Declare all the JAXB mapping resources needed by the application -->
<properties>
	<!-- core product resources -->
	<entry key="Composer">composer-jaxb-mapping.xml</entry>
	<entry key="Streaming">streaming-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-Filtering">pivot.filtering-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-ContextValues">pivot.contextvalues-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-Definitions">pivot.definitions-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-Security">pivot.security-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-Queries">pivot.queries-jaxb-mapping.xml</entry>
	<entry key="ActivePivot-Streaming">pivot.streaming-jaxb-mapping.xml</entry>

	<!-- custom resources -->
	<!--<entry key="ActivePivot-Custom">JAXB-INF/custom-jaxb-mapping.xml</entry>-->
</properties>
-----------------------------------------------------------------------------------------------------

src/test/resources/mdx/Sales.mdx

SELECT
    [Company].[Company].[Company] ON ROWS,
    [Year].[Year].[Year] ON COLUMNS
FROM
    [DEMO]
WHERE (
    [Measures].[Sales.SUM]
)
-----------------------------------------------------------------------------------------------------

