package dk.magenta.datafordeler.cpr;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by lars on 12-01-17.
 */
@ComponentScan({
        "dk.magenta.datafordeler",
        "dk.magenta.datafordeler.core", "dk.magenta.datafordeler.core.database", "dk.magenta.datafordeler.core.util",
        "dk.magenta.datafordeler.cpr", "dk.magenta.datafordeler.cpr.data", "dk.magenta.datafordeler.cpr.data.person",
        "dk.magenta.datafordeler.cpr.parsers", "dk.magenta.datafordeler.cpr.records"
})
@EntityScan("dk.magenta.datafordeler")
@ServletComponentScan
@SpringBootApplication
public class TestConfig {

}
