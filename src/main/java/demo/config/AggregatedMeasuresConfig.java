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