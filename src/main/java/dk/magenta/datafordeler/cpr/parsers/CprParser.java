package dk.magenta.datafordeler.cpr.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 18-12-14.
 */
@Component
public class CprParser {

    @Autowired
    private PersonParser personParser;

    // TODO: Add more parsers

    private List<CprSubParser> subParsers;

    public CprParser() {
    }

    @PostConstruct
    protected void postConstruct() {
        this.subParsers = new ArrayList<>();
        this.subParsers.add(this.personParser);
    }

}
