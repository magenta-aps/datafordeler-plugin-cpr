package dk.magenta.datafordeler.cpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dk.magenta.datafordeler.core.fapi.ServletConfiguration;
import dk.magenta.datafordeler.core.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;

/**
 * Created by lars on 19-05-17.
 */
@Configuration
@EnableWs
public class CprServletConfiguration extends ServletConfiguration {

    @Autowired
    private CprPlugin cprPlugin;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private XmlMapper xmlMapper;

    @Override
    protected Plugin getPlugin() {
        return this.cprPlugin;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    protected XmlMapper getXmlMapper() {
        return this.xmlMapper;
    }

    @Override
    protected String getServiceOwner() {
        return "cpr";
    }
}

