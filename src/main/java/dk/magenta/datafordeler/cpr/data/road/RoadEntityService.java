package dk.magenta.datafordeler.cpr.data.road;

import dk.magenta.datafordeler.core.exception.AccessDeniedException;
import dk.magenta.datafordeler.core.exception.AccessRequiredException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.user.DafoUserDetails;
import dk.magenta.datafordeler.cpr.CprAccessChecker;
import dk.magenta.datafordeler.cpr.CprPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lars on 19-05-17.
 */
@RestController("CprRoadEntityService")
@RequestMapping("/cpr/road/1/rest")
public class RoadEntityService extends FapiService<RoadEntity, RoadQuery> {

    @Autowired
    private CprPlugin cprPlugin;

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getServiceName() {
        return "road";
    }

    @Override
    protected Class<RoadEntity> getEntityClass() {
        return RoadEntity.class;
    }

    @Override
    public Plugin getPlugin() {
        return this.cprPlugin;
    }

    @Override
    protected void checkAccess(DafoUserDetails dafoUserDetails) throws AccessDeniedException, AccessRequiredException {
        CprAccessChecker.checkAccess(dafoUserDetails);
    }

    @Override
    protected RoadQuery getEmptyQuery() {
        return new RoadQuery();
    }

}
