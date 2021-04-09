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