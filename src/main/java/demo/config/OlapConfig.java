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