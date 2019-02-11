package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.user.DafoUserManager;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import static org.mockito.Mockito.when;

@Component
public abstract class TestBase {

    @Autowired
    private CprPlugin plugin;

    public CprPlugin getPlugin() {
        return this.plugin;
    }

    @Autowired
    private SessionManager sessionManager;

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Autowired
    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Autowired
    private TestRestTemplate restTemplate;

    public TestRestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    private static HashSet<String> ignoreKeys = new HashSet<String>();
    static {
        ignoreKeys.add("sidstImporteret");
    }

    @SpyBean
    private DafoUserManager dafoUserManager;


    protected void applyAccess(TestUserDetails testUserDetails) {
        when(this.dafoUserManager.getFallbackUser()).thenReturn(testUserDetails);
    }
    protected void whitelistLocalhost() {
        when(this.dafoUserManager.getIpWhitelist()).thenReturn(Collections.singleton("127.0.0.1"));
    }

    protected ResponseEntity<String> restSearch(ParameterMap parameters, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/cpr/"+type+"/1/rest/search?" + parameters.asUrlParams(), HttpMethod.GET, httpEntity, String.class);
    }

    protected ResponseEntity<String> uuidSearch(String id, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/cpr/"+type+"/1/rest/" + id, HttpMethod.GET, httpEntity, String.class);
    }

    protected void assertJsonEquality(JsonNode node1, JsonNode node2, boolean ignoreArrayOrdering, boolean printDifference) {
        try {
            Assert.assertEquals(node1.isNull(), node2.isNull());
            Assert.assertEquals(node1.isArray(), node2.isArray());
            Assert.assertEquals(node1.isObject(), node2.isObject());
            Assert.assertEquals(node1.isLong(), node2.isLong());
            Assert.assertEquals(node1.isInt(), node2.isInt());
            Assert.assertEquals(node1.isShort(), node2.isShort());
            Assert.assertEquals(node1.isBoolean(), node2.isBoolean());
            Assert.assertEquals(node1.isTextual(), node2.isTextual());
            if (node1.isArray()) {
                Assert.assertEquals(node1.size(), node2.size());
                if (ignoreArrayOrdering) {
                    for (int i = 0; i < node1.size(); i++) {
                        boolean match = false;
                        for (int j = 0; j < node2.size(); j++) {
                            try {
                                assertJsonEquality(node1.get(i), node2.get(j), true, false);
                                match = true;
                            } catch (AssertionError e) {
                            }
                        }
                        if (!match) {
                            throw new AssertionError();
                        }
                    }
                } else {
                    for (int i = 0; i < node1.size(); i++) {
                        assertJsonEquality(node1.get(i), node2.get(i), false, printDifference);
                    }
                }
            } else if (node1.isObject()) {
                Assert.assertEquals(node1.size(), node2.size());
                Iterator<String> keys = node1.fieldNames();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Assert.assertNotNull(node2.get(key));
                    if (!ignoreKeys.contains(key)) {
                        assertJsonEquality(node1.get(key), node2.get(key), ignoreArrayOrdering, printDifference);
                    }
                }
            } else {
                Assert.assertEquals(node1.asText(), node2.asText());
            }
        } catch (AssertionError e) {
            if (printDifference) {
                try {
                    System.out.println("\n" + this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node1) + "\n != \n" + this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node2) + "\n\n\n");
                } catch (JsonProcessingException e1) {
                    System.out.println("\n" + node1.asText() + "\n != \n" + node2.asText() + "\n\n\n");
                }
            }
            throw e;
        }
    }
}
