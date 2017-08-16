package dk.magenta.datafordeler.cpr.data.person;

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
@RestController
@RequestMapping("/cpr/person/1/rest")
public class PersonEntityService extends FapiService<PersonEntity, PersonQuery> {

    @Autowired
    private CprPlugin cprPlugin;

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getServiceName() {
        return "person";
    }

    @Override
    protected Class<PersonEntity> getEntityClass() {
        return PersonEntity.class;
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
    protected PersonQuery getEmptyQuery() {
        return new PersonQuery();
    }

}
