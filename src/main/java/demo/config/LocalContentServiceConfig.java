package demo.config;

import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

    private IActivePivotManagerDescription manager;

    @Override
    public IActivePivotContentService activePivotContentService() {
        return new ActivePivotContentServiceBuilder()
                .withoutPersistence()
                .withoutCache()
                .needInitialization("ROLE_USER", "ROLE_USER")
                .withDescription(manager)
                .withContextValues("ROLE-INF")
                .build();
    }

    @Required
    @Autowired
    public void setManager(IActivePivotManagerDescription manager) {
        this.manager = manager;
    }

}
