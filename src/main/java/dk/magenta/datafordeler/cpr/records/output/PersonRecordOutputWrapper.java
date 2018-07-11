package dk.magenta.datafordeler.cpr.records.output;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.Entity;
import dk.magenta.datafordeler.core.fapi.Query;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.records.Bitemporality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A class for formatting a CompanyEntity to JSON, for FAPI output. The data hierarchy
 * under a Company is sorted into this format:
 * {
 *     "UUID": <company uuid>
 *     "cvrnummer": <company cvr number>
 *     "id": {
 *         "domaene": <company domain>
 *     },
 *     registreringer: [
 *          {
 *              "registreringFra": <registrationFrom>,
 *              "registreringTil": <registrationTo>,
 *              "navn": [
 *              {
 *                  "navn": <companyName1>
 *                  "virkningFra": <effectFrom1>
 *                  "virkningTil": <effectTo1>
 *              },
 *              {
 *                  "navn": <companyName2>
 *                  "virkningFra": <effectFrom2>
 *                  "virkningTil": <effectTo2>
 *              }
 *              ]
 *          }
 *     ]
 * }
 */
@Component
public class PersonRecordOutputWrapper extends CprRecordOutputWrapper<PersonEntity> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public Object wrapResult(PersonEntity record, Query query) {
        Bitemporality mustContain = new Bitemporality(query.getRegistrationFrom(), query.getRegistrationTo(), query.getEffectFrom(), query.getEffectTo());
        return this.asRVD(record, mustContain);
        //return this.asRecord(record);
    }

    private ObjectNode asRecord(PersonEntity record) {
        return objectMapper.valueToTree(record);
    }

    protected ObjectNode asRVD(PersonEntity record, Bitemporality mustContain) {
        ObjectNode root = this.getNode(record, mustContain);
        root.put(Entity.IO_FIELD_UUID, record.getIdentification().getUuid().toString());
        root.put(Entity.IO_FIELD_DOMAIN, record.getIdentification().getDomain());
        return root;
    }


    @Override
    protected void fillContainer(OutputContainer container, PersonEntity record) {

        container.addNontemporal(PersonEntity.IO_FIELD_CPR_NUMBER, record.getPersonnummer());

        container.addBitemporal(PersonEntity.IO_FIELD_ADDRESS_CONAME, record.getConame(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_ADDRESS, record.getAddress());
        container.addBitemporal(PersonEntity.IO_FIELD_ADDRESS_NAME, record.getAddressName());
        container.addBitemporal(PersonEntity.IO_FIELD_BIRTHPLACE, record.getBirthPlace());
        container.addBitemporal(PersonEntity.IO_FIELD_BIRTHPLACE_VERIFICATION, record.getBirthPlaceVerification(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_BIRTHTIME, record.getBirthTime());
        container.addBitemporal(PersonEntity.IO_FIELD_CHURCH, record.getChurchRelation(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_CHURCH_VERIFICATION, record.getChurchRelationVerification(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_CITIZENSHIP, record.getCitizenship(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_CITIZENSHIP_VERIFICATION, record.getCitizenshipVerification(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_CIVILSTATUS, record.getCivilstatus());
        container.addBitemporal(PersonEntity.IO_FIELD_CIVILSTATUS_AUTHORITYTEXT, record.getCivilstatusAuthorityText());
        container.addBitemporal(PersonEntity.IO_FIELD_CIVILSTATUS_VERIFICATION, record.getCivilstatusVerification());
        container.addBitemporal(PersonEntity.IO_FIELD_FOREIGN_ADDRESS, record.getForeignAddress());
        container.addBitemporal(PersonEntity.IO_FIELD_FOREIGN_ADDRESS_EMIGRATION, record.getEmigration(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_MOVE_MUNICIPALITY, record.getMunicipalityMove());
        container.addBitemporal(PersonEntity.IO_FIELD_NAME, record.getName());
        container.addBitemporal(PersonEntity.IO_FIELD_NAME_AUTHORITY_TEXT, record.getNameAuthorityText());
        container.addBitemporal(PersonEntity.IO_FIELD_NAME_VERIFICATION, record.getNameVerification());
        container.addBitemporal(PersonEntity.IO_FIELD_MOTHER, record.getMother());
        container.addBitemporal(PersonEntity.IO_FIELD_MOTHER_VERIFICATION, record.getMotherVerification());
        container.addBitemporal(PersonEntity.IO_FIELD_FATHER, record.getFather());
        container.addBitemporal(PersonEntity.IO_FIELD_FATHER_VERIFICATION, record.getFatherVerification());
        container.addBitemporal(PersonEntity.IO_FIELD_CORE, record.getCore(),true);
        container.addBitemporal(PersonEntity.IO_FIELD_PNR, record.getPersonNumber(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_POSITION, record.getPosition(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_STATUS, record.getStatus(), true);
        container.addBitemporal(PersonEntity.IO_FIELD_PROTECTION, record.getProtection());
    }

}
