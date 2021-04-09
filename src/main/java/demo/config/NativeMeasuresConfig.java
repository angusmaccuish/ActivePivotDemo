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