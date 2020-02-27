package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.fapi.Query;
import dk.magenta.datafordeler.core.io.ImportInputStream;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.user.DafoUserManager;
import dk.magenta.datafordeler.core.util.LabeledSequenceInputStream;
import dk.magenta.datafordeler.cpr.configuration.CprConfiguration;
import dk.magenta.datafordeler.cpr.data.person.PersonEntity;
import dk.magenta.datafordeler.cpr.data.person.PersonEntityManager;
import dk.magenta.datafordeler.cpr.data.person.PersonRecordQuery;
import dk.magenta.datafordeler.cpr.records.output.PersonRecordOutputWrapper;
import dk.magenta.datafordeler.cpr.records.person.data.AddressDataRecord;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class cprLoadAndUnoPersonTest {


    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private PersonEntityManager personEntityManager;


    private static HashMap<String, String> schemaMap = new HashMap<>();
    static {
        schemaMap.put("person", PersonEntity.schema);
    }

    @SpyBean
    private DafoUserManager dafoUserManager;

    private void applyAccess(TestUserDetails testUserDetails) {
        when(dafoUserManager.getFallbackUser()).thenReturn(testUserDetails);
    }

    private void loadPerson(String resource, ImportMetadata importMetadata) throws DataFordelerException, IOException {
        InputStream testData = cprLoadAndmarkSameAddressAsSameas.class.getResourceAsStream(resource);
        LabeledSequenceInputStream ll = new LabeledSequenceInputStream("d190611.l534901", new ByteArrayInputStream("d190611.l534901".getBytes()), "d190611.l534901", testData);
        ImportInputStream inp = new ImportInputStream(ll);
        personEntityManager.parseData(inp, importMetadata);
        testData.close();
    }


    private void loadPersonWithOrigin(ImportMetadata importMetadata) throws DataFordelerException, IOException, URISyntaxException {

        /*CprConfiguration configuration = ((CprConfigurationManager) plugin.getConfigurationManager()).getConfiguration();
        configuration.setPersonRegisterDataCharset(CprConfiguration.Charset.UTF_8);*/
        personEntityManager.getRegisterManager().getConfigurationManager().getConfiguration().setPersonRegisterDataCharset(CprConfiguration.Charset.UTF_8);

        InputStream testData1 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst1.txt");
        LabeledSequenceInputStream ll1 = new LabeledSequenceInputStream("d190611.l5555", new ByteArrayInputStream("d190611.l534901".getBytes()), "d190611.l534901", testData1);
        ImportInputStream inp1 = new ImportInputStream(ll1);
        personEntityManager.parseData(inp1, importMetadata);
        testData1.close();

        InputStream testData2 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst2.txt");
        LabeledSequenceInputStream ll2 = new LabeledSequenceInputStream("d190912.l5555", new ByteArrayInputStream("d190612.l534901".getBytes()), "d190611.l534901", testData2);
        ImportInputStream inp2 = new ImportInputStream(ll2);
        personEntityManager.parseData(inp2, importMetadata);
        testData2.close();

        InputStream testData3 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst3.txt");
        LabeledSequenceInputStream ll3 = new LabeledSequenceInputStream("d190913.l5555", new ByteArrayInputStream("d190613.l534901".getBytes()), "d190611.l534901", testData3);
        ImportInputStream inp3 = new ImportInputStream(ll3);
        personEntityManager.parseData(inp3, importMetadata);
        testData3.close();

        InputStream testData4 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst4.txt");
        LabeledSequenceInputStream ll4 = new LabeledSequenceInputStream("d190914.l5555", new ByteArrayInputStream("d190614.l534901".getBytes()), "d190611.l534901", testData4);
        ImportInputStream inp4 = new ImportInputStream(ll4);
        personEntityManager.parseData(inp4, importMetadata);
        testData4.close();

        InputStream testData5 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst5.txt");
        LabeledSequenceInputStream ll5 = new LabeledSequenceInputStream("d190915.l5555", new ByteArrayInputStream("d190615.l534901".getBytes()), "d190611.l534901", testData5);
        ImportInputStream inp5 = new ImportInputStream(ll5);
        personEntityManager.parseData(inp5, importMetadata);
        testData5.close();

        InputStream testData6 = cprLoadAndUnoPersonTest.class.getResourceAsStream("/tst6.txt");
        LabeledSequenceInputStream ll6 = new LabeledSequenceInputStream("d190916.l5555", new ByteArrayInputStream("d190616.l534901".getBytes()), "d190611.l534901", testData6);
        ImportInputStream inp6 = new ImportInputStream(ll6);
        personEntityManager.parseData(inp6, importMetadata);
        testData6.close();
    }


    @After
    public void clean() {
        Session session = sessionManager.getSessionFactory().openSession();
        session.close();
    }


    @Test
    public void testUndoPersonLoadAndFilterOnSameAs1() throws DataFordelerException, IOException, URISyntaxException {

        try(Session session = sessionManager.getSessionFactory().openSession()) {
            ImportMetadata importMetadata = new ImportMetadata();
            importMetadata.setSession(session);
            this.loadPersonWithOrigin(importMetadata);
            session.close();
        }


        try(Session session = sessionManager.getSessionFactory().openSession()) {
            PersonRecordQuery query = new PersonRecordQuery();
            query.setPersonnummer("1111111111");
            OffsetDateTime now = Query.parseDateTime("2019-08-08");

            query.setEffectToAfter(now);
            query.setEffectFromBefore(now);

            query.setRegistrationToAfter(OffsetDateTime.now());
            query.setRegistrationFromBefore(OffsetDateTime.now());

            query.applyFilters(session);
            List<PersonEntity> persons = QueryManager.getAllEntities(session, query, PersonEntity.class);
            Assert.assertEquals(1, persons.size());



            Set<AddressDataRecord> adresses = persons.get(0).getAddress();


            System.out.println("============================================0");
            for(AddressDataRecord adress : adresses) {
                //System.out.println(adress);
                //System.out.println(adress.getRegistrationFrom()+" "+adress.getRegistrationTo()+" "+adress.getEffectFrom()+" "+adress.getEffectTo());
                System.out.println(adress.cnt+" "+adress.getEffectTo()+" "+ adress.isUndone()+" "+adress.line);
                //System.out.println(adress.isUndone());
            }
            System.out.println("ADSES");
            System.out.println(adresses.size());

            Set<AddressDataRecord> adresses2 = adresses.stream().filter( d -> !d.isUndone()).collect(Collectors.toSet());
            Assert.assertEquals(1, adresses2.size());
            Set<AddressDataRecord> adresses3 = adresses2.stream().filter( d -> d.getMunicipalityCode() == 956).collect(Collectors.toSet());
            Assert.assertEquals(1, adresses3.size());
        }
    }

}
