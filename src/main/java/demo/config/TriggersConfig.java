package demo.config;

import com.qfs.chunk.IArrayReader;
import com.qfs.chunk.IArrayWriter;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.store.IDatastore;
import com.qfs.store.record.IRecordFormat;
import com.qfs.store.selection.impl.Selection;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager.IUpdateWhereProcedure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TriggersConfig {

    /**
     * May not be needed, added to Proteus DRC recently to cope with 'adjustments' ie intraday
     * overrides to risk weights which need to update the cube. This trigger doesn't update any
     * fields, it merely logs the data in the Results store
     */
    @Bean
    public IUpdateWhereProcedure trigger(IDatastore datastore) throws DatastoreTransactionException {
        final IUpdateWhereProcedure procedure = new IUpdateWhereProcedure() {

            private int companyIndex;
            private int yearIndex;
            private int salesIndex;

            @Override
            public void init(IRecordFormat input, IRecordFormat output) {
                companyIndex = input.getFieldIndex(StoresConfig.COMPANY);
                yearIndex = input.getFieldIndex(StoresConfig.YEAR);
                salesIndex = input.getFieldIndex(StoresConfig.SALES);
            }

            @Override
            public void execute(IArrayReader reader, IArrayWriter writer) {
                System.out.printf(
                        "Company: %s Year: %s Sales: %s",
                        reader.read(companyIndex),
                        reader.read(yearIndex),
                        reader.read(salesIndex));
            }

        };

        datastore.getTransactionManager().registerCommitTimeUpdateWhereTrigger(
                "Test Trigger",
                0,
                new Selection(StoresConfig.RESULTS_STORE, StoresConfig.COMPANY, StoresConfig.YEAR, StoresConfig.SALES),
                BaseConditions.True(),
                procedure);

        return procedure;
    }

}