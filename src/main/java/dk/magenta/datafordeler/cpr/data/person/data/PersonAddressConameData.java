package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.cpr.data.DetailData;

import java.util.Collections;
import java.util.Map;

/**
 * Created by lars on 22-06-17.
 */
public class PersonAddressConameData extends DetailData {

    private String coName;

    public String getCoName() {
        return this.coName;
    }

    public void setCoName(String coName) {
        this.coName = coName;
    }

    @Override
    public Map<String, Object> asMap() {
        return Collections.singletonMap("coName", this.coName);
    }
}
