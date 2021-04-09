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