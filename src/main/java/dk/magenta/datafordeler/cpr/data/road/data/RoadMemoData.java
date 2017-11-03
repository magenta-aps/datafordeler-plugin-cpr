package dk.magenta.datafordeler.cpr.data.road.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.cpr.data.DetailData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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

    @ManyToOne(targetEntity = RoadBaseData.class)
    private RoadBaseData roadBaseData;

    public RoadBaseData getRoadBaseData() {
        return this.roadBaseData;
    }

    public void setRoadBaseData(RoadBaseData roadBaseData) {
        this.roadBaseData = roadBaseData;
    }

    public static final String DB_FIELD_MEMONUMBER = "memoNumber";
    public static final String IO_FIELD_MEMONUMBER = "notatNummer";
    @Column(name = DB_FIELD_MEMONUMBER)
    @JsonProperty(value = IO_FIELD_MEMONUMBER)
    @XmlElement(name = IO_FIELD_MEMONUMBER)
    private int memoNumber;

    public int getMemoNumber() {
        return memoNumber;
    }

    public void setMemoNumber(int memoNumber) {
        this.memoNumber = memoNumber;
    }

    public static final String DB_FIELD_MEMOLINE = "memoText";
    public static final String IO_FIELD_MEMOLINE = "notatLinie";
    @Column(name = DB_FIELD_MEMOLINE)
    @JsonProperty(value = IO_FIELD_MEMOLINE)
    @XmlElement(name = IO_FIELD_MEMOLINE)
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


    @JsonIgnore
    public Map<String, Object> databaseFields() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DB_FIELD_MEMOLINE, this.memoText);
        map.put(DB_FIELD_MEMONUMBER, this.memoNumber);
        return map;
    }

}
