package demo.config;

import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.ICSVSource;
import com.qfs.msg.csv.ICSVTopic;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.filesystem.impl.SingleFileCSVTopic;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.IStoreMessageChannelFactory;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SourceConfig {

    /**
     * Source groups topics of the same type
     * e.g. Once source for CSV, one for Avro, one for Webservice, etc
     */
    @Bean
    public CSVSource<Path> csvSource(List<ICSVTopic<Path>> topics) {
        final CSVSource<Path> source = new CSVSource<>();
        topics.forEach(source::addTopic);
        return source;
    }

    /**
     * CSV Topic specifies file name, columns and if header line to be excluded
     */
    @Bean
    public SingleFileCSVTopic topic() {
        final CSVParserConfiguration configuration = new CSVParserConfiguration();
        configuration.setColumnCount(3);
        configuration.setColumns(CSVParserConfiguration.toMap(Arrays.asList(StoresConfig.COMPANY, StoresConfig.YEAR, StoresConfig.SALES)));
        configuration.setNumberSkippedLines(1);
        return new SingleFileCSVTopic("ResultsTopic", configuration, "target/classes/results.csv", 0);
    }

    /**
     * Channel Factory creates Channels which link Sources to Datastore
     */
    @Bean
    public CSVMessageChannelFactory<Path> channelFactory(ICSVSource<Path> source, IDatastore datastore) {
        return new CSVMessageChannelFactory<>(source, datastore);
    }

    /**
     * Channel links Topic to a specific store in the Datastore
     */
    @Bean
    public IMessageChannel<IFileInfo<Path>, ILineReader> channel(
            ICSVTopic<Path> topic,
            IStoreMessageChannelFactory<IFileInfo<Path>, ILineReader> factory) {

        final String topicName = topic.getName();
        return factory.createChannel(topicName, StoresConfig.RESULTS_STORE);
    }

    /**
     * Load CSV when application starts
     */
    @Bean
    public ApplicationListener<ContextRefreshedEvent> loadCsv(
            IDatastore datastore,
            ICSVSource<Path> source,
            List<IMessageChannel<IFileInfo<Path>, ILineReader>> channels) {

        return event -> {
            try {
                System.out.println("Fetching CSV data...");
                datastore.getTransactionManager().startTransaction();
                source.fetch(channels);
                datastore.getTransactionManager().commitTransaction();
                System.out.println("Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}