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