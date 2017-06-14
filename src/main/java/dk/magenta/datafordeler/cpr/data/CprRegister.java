package dk.magenta.datafordeler.cpr.data;

import dk.magenta.datafordeler.cpr.data.person.PersonRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 18-12-14.
 */
@Component
public class CprRegister extends LineRegister {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private PersonRegister personRegister;

    private List<CprSubRegister> subRegisters;

    public CprRegister() {
    }

    @PostConstruct
    protected void postConstruct() {
        this.subRegisters = new ArrayList<CprSubRegister>();
        this.subRegisters.add(this.personRegister);
    }

}
