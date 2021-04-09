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