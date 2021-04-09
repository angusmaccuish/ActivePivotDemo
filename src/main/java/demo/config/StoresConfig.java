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