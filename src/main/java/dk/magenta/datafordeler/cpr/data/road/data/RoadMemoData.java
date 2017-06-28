package dk.magenta.datafordeler.cpr.data.road.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lars on 16-05-17.
 */
@Entity
@Table(name="cpr_road_memo")
public class RoadMemoData extends DetailData {


    @Column
    @JsonProperty(value = "notatNummer")
    @XmlElement(name = "notatNummer")
    private int memoNumber;

    public int getMemoNumber() {
        return memoNumber;
    }

    public void setMemoNumber(int memoNumber) {
        this.memoNumber = memoNumber;
    }

    @Column
    @JsonProperty(value = "notatLinie")
    @XmlElement(name = "notatLinje")
    private String memoText;

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }


    /**
     * Return a map of attributes, including those from the superclass
     * @return
     */
    @Override
    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.memoNumber != 0) {
            map.put("memoNumber", this.memoNumber);
        }
        if (this.memoText != null) {
            map.put("memoText", this.memoText);
        }
        return map;
    }

}
