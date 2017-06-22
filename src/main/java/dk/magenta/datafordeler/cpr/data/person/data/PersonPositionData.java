package dk.magenta.datafordeler.cpr.data.person.data;

import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 21-06-17.
 */
@Entity
@Table(name = "cpr_person_position")
public class PersonPositionData extends DetailData {

    @Column
    private int authority;

    public int getAuthority() {
        return this.authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @Column
    private String position;

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authority", this.authority);
        map.put("position", this.position);
        return map;
    }
}
