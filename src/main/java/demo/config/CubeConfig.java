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