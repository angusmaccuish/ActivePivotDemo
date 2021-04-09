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