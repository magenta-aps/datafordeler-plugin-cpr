package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.datafordeler.core.arearestriction.AreaRestriction;
import dk.magenta.datafordeler.core.arearestriction.AreaRestrictionType;
import dk.magenta.datafordeler.core.database.DataItem;
import dk.magenta.datafordeler.core.exception.AccessDeniedException;
import dk.magenta.datafordeler.core.exception.AccessRequiredException;
import dk.magenta.datafordeler.core.exception.InvalidClientInputException;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.core.plugin.AreaRestrictionDefinition;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.user.DafoUserDetails;
import dk.magenta.datafordeler.cpr.CprAccessChecker;
import dk.magenta.datafordeler.cpr.CprAreaRestrictionDefinition;
import dk.magenta.datafordeler.cpr.CprPlugin;
import dk.magenta.datafordeler.cpr.CprRolesDefinition;
import dk.magenta.datafordeler.cpr.data.person.data.PersonBaseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


/**
 * Created by lars on 19-05-17.
 */
@RestController
@RequestMapping("/cpr/person/1/rest")
public class PersonEntityService extends FapiService<PersonEntity, PersonQuery> {

    @Autowired
    private CprPlugin cprPlugin;

    public PersonEntityService() {
        super();
        this.setOutputWrapper(new PersonOutputWrapper());
    }

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
    protected Class<? extends DataItem> getDataClass() {
        return PersonBaseData.class;
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

    @Override
    protected void applyAreaRestrictionsToQuery(PersonQuery query, DafoUserDetails user) throws InvalidClientInputException {
        System.out.println("Applying restrictions to query");
        Collection<AreaRestriction> restrictions = user.getAreaRestrictionsForRole(CprRolesDefinition.READ_CPR_ROLE);
        AreaRestrictionDefinition areaRestrictionDefinition = this.cprPlugin.getAreaRestrictionDefinition();
        AreaRestrictionType municipalityType = areaRestrictionDefinition.getAreaRestrictionTypeByName(CprAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER);
        for (AreaRestriction restriction : restrictions) {
            System.out.println("type: "+restriction.getType());
            if (restriction.getType() == municipalityType) {
                System.out.println("restriction.getValue(): "+restriction.getValue());
                query.addKommunekode(restriction.getValue());
            }
        }
    }

}
