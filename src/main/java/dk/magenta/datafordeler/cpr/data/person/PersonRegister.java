package dk.magenta.datafordeler.cpr.data.person;

import dk.magenta.databroker.core.DataProviderConfiguration;
import dk.magenta.databroker.core.RegistreringInfo;
import dk.magenta.databroker.core.model.oio.VirkningEntity;
import dk.magenta.databroker.cprvejregister.dataproviders.records.CprRecord;
import dk.magenta.databroker.dawa.model.DawaModel;
import dk.magenta.databroker.dawa.model.RawVej;
import dk.magenta.databroker.register.RegisterRun;
import dk.magenta.databroker.register.records.Record;
import dk.magenta.databroker.util.objectcontainers.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Created by lars on 04-11-14.
 */
@Component
public class PersonRegister extends CprSubRegister {


    /*
    * Inner classes for parsed data
    * */

    public abstract class VejDataRecord extends CprRecord {
        public static final String RECORDTYPE_AKTVEJ = "001";
        public static final String RECORDTYPE_BOLIG = "002";
        public static final String RECORDTYPE_BYDISTRIKT = "003";
        public static final String RECORDTYPE_POSTDIST = "004";
        public static final String RECORDTYPE_NOTATVEJ = "005";
        public static final String RECORDTYPE_BYFORNYDIST = "006";
        public static final String RECORDTYPE_DIVDIST = "007";
        public static final String RECORDTYPE_EVAKUERDIST = "008";
        public static final String RECORDTYPE_KIRKEDIST = "009";
        public static final String RECORDTYPE_SKOLEDIST = "010";
        public static final String RECORDTYPE_BEFOLKDIST = "011";
        public static final String RECORDTYPE_SOCIALDIST = "012";
        public static final String RECORDTYPE_SOGNEDIST = "013";
        public static final String RECORDTYPE_VALGDIST = "014";
        public static final String RECORDTYPE_VARMEDIST = "015";
        public static final String RECORDTYPE_HISTORISKVEJ = "016";
        protected int getTimestampStart() {
            return 21;
        }

        public VejDataRecord(String line) throws ParseException {
            super(line);
            this.obtain("kommuneKode", 4, 4);
            this.obtain("vejKode", 8, 4);
            this.obtain("timestamp", this.getTimestampStart(), 12);
        }
    }

    public abstract class Distrikt extends VejDataRecord {
        protected int getDistriktsTekstStart() { return 35; }
        protected int getDistriktsTekstLength() {
            return 30;
        }

        public Distrikt(String line) throws ParseException {
            super(line);
            this.obtain("husNrFra", 12, 4);
            this.obtain("husNrTil", 16, 4);
            this.obtain("ligeUlige", 20, 1);
            this.obtain("distriktsTekst", this.getDistriktsTekstStart(), this.getDistriktsTekstLength());
        }
    }

    public class AktivVej extends VejDataRecord {
        public String getRecordType() {
            return RECORDTYPE_AKTVEJ;
        }
        protected int getTimestampStart() {
            return 12;
        }

        private ArrayList<AktivVej> connections;
        public void addConnection(AktivVej connection) {
            this.connections.add(connection);
        }
        public List<AktivVej> getConnections() {
            return (List<AktivVej>) this.connections;
        }

        public AktivVej(String line) throws ParseException {
            super(line);
            this.obtain("tilKommuneKode", 24, 4);
            this.obtain("tilVejKode", 28, 4);
            this.obtain("fraKommuneKode", 32, 4);
            this.obtain("fraVejKode", 36, 4);
            this.obtain("startDato", 40, 12);
            this.obtain("vejAdresseringsnavn", 52, 20);
            this.obtain("vejNavn", 72, 40);
            this.connections = new ArrayList<AktivVej>();
            this.clean();
        }
    }

    public class Bolig extends VejDataRecord {
        public String getRecordType() {
            return RECORDTYPE_BOLIG;
        }
        protected int getTimestampStart() {
            return 22;
        }

        public Bolig(String line) throws ParseException {
            super(line);
            this.obtain("husNr", 12, 4);
            this.obtain("etage", 16, 2);
            this.obtain("sidedoer", 18, 4);
            this.obtain("startDato", 35, 12);
            this.obtain("lokalitet", 59, 34);
            this.clean();
        }
    }

    public class ByDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_BYDISTRIKT;
        }
        protected int getDistriktsTekstStart() {
            return 33;
        }
        protected int getDistriktsTekstLength() {
            return 34;
        }
        public ByDistrikt(String line) throws ParseException {
            super(line);
            this.put("bynavn", this.get("distriktsTekst"));
            this.clean();
        }
    }

    public class PostDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_POSTDIST;
        }
        protected int getDistriktsTekstStart() {
            return 37;
        }
        protected int getDistriktsTekstLength() {
            return 20;
        }
        public PostDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("postNr", 33, 4);
            this.clean();
        }
    }

    public class NotatVej extends VejDataRecord {
        public String getRecordType() {
            return RECORDTYPE_NOTATVEJ;
        }
        protected int getTimestampStart() {
            return 54;
        }
        public NotatVej(String line) throws ParseException {
            super(line);
            this.obtain("notatNr", 12, 2);
            this.obtain("notatLinie", 14, 40);
            this.obtain("startDato", 66, 12);
            this.clean();
        }
    }

    public class ByfornyelsesDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_BYFORNYDIST;
        }
        protected int getDistriktsTekstStart() {
            return 39;
        }
        public ByfornyelsesDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("byfornyKode", 33, 6);
            this.clean();
        }
    }

    public class DiverseDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_DIVDIST;
        }
        protected int getDistriktsTekstStart() {
            return 39;
        }
        public DiverseDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("distriktType", 33, 2);
            this.obtain("diverseDistriktsKode", 35, 4);
            this.clean();
        }
    }

    public class EvakueringsDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_EVAKUERDIST;
        }
        protected int getDistriktsTekstStart() {
            return 34;
        }
        public EvakueringsDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("evakueringsKode", 33, 1);
            this.clean();
        }
    }

    public class KirkeDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_KIRKEDIST;
        }
        public KirkeDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("kirkeKode", 33, 2);
            this.clean();
        }
    }

    public class SkoleDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_SKOLEDIST;
        }
        public SkoleDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("skoleKode", 33, 2);
            this.clean();
        }
    }

    public class BefolkningsDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_BEFOLKDIST;
        }
        protected int getDistriktsTekstStart() {
            return 37;
        }
        public BefolkningsDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("befolkningsKode", 33, 4);
            this.clean();
        }
    }

    public class SocialDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_SOCIALDIST;
        }
        public SocialDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("socialKode", 33, 2);
            this.clean();
        }
    }

    public class SogneDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_SOGNEDIST;
        }
        protected int getDistriktsTekstStart() {
            return 37;
        }
        protected int getDistriktsTekstLength() {
            return 20;
        }
        public SogneDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("myndighedsKode", 33, 4);
            this.put("myndighedsNavn", this.get("distriktsTekst"));
            this.clean();
        }
    }

    public class ValgDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_VALGDIST;
        }
        public ValgDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("valgKode", 33, 2);
            this.clean();
        }
    }

    public class VarmeDistrikt extends Distrikt {
        public String getRecordType() {
            return RECORDTYPE_VARMEDIST;
        }
        protected int getDistriktsTekstStart() {
            return 37;
        }
        public VarmeDistrikt(String line) throws ParseException {
            super(line);
            this.obtain("varmeKode", 33, 4);
        }
    }

    public class HistoriskVej extends VejDataRecord {
        public String getRecordType() {
            return RECORDTYPE_HISTORISKVEJ;
        }
        protected int getTimestampStart() {
            return 12;
        }
        public HistoriskVej(String line) throws ParseException {
            super(line);
            this.obtain("startDato", 24, 12);
            this.obtain("slutDato", 36, 12);
            this.obtain("vejAdresseringsnavn", 48, 20);
            this.obtain("vejNavn", 68, 40);
        }
    }


    /*
    * RegisterRun inner class
    * */

    public class VejRegisterRun extends RegisterRun {

        private Level2Container<AktivVej> aktiveVeje;
        private ArrayList<PostDistrikt> postDistrikter;

        private ArrayList<Bolig> boliger;
        private ArrayList<ByDistrikt> byDistrikter;

        public VejRegisterRun() {
            super();
            this.aktiveVeje = new Level2Container<AktivVej>();

            this.boliger = new ArrayList<Bolig>();
            this.byDistrikter = new ArrayList<ByDistrikt>();
        }

        public boolean add(Record record) {
            if (record.getRecordType().equals(VejDataRecord.RECORDTYPE_AKTVEJ)) {
                AktivVej vej = (AktivVej) record;
                int vejKode = vej.getInt("vejKode");
                int kommuneKode = vej.getInt("kommuneKode");
                if (!aktiveVeje.put(kommuneKode, vejKode, vej, true)) {
                    VejRegister.this.log.warn("Collision on road " + kommuneKode + ":" + vejKode + " (present twice in input, named " + aktiveVeje.get(kommuneKode, vejKode).get("vejNavn") + " and " + vej.get("vejNavn") + ")");
                }
                super.add(vej);
            }

            if (record.getRecordType().equals(VejDataRecord.RECORDTYPE_BOLIG)) {
                this.boliger.add((Bolig)record);
            }


            if (record.getRecordType().equals(VejDataRecord.RECORDTYPE_BYDISTRIKT)) {
                this.byDistrikter.add((ByDistrikt)record);
            }

            return false;
        }

        public Level2Container<AktivVej> getAktiveVeje() {
            return aktiveVeje;
        }

        public ArrayList<PostDistrikt> getPostDistrikter() {
            return postDistrikter;
        }

        public ArrayList<Bolig> getBoliger() {
            return boliger;
        }

        public ArrayList<ByDistrikt> getByDistrikter() {
            return this.byDistrikter;
        }

    }

    protected RegisterRun createRun() {
        return new VejRegisterRun();
    }

    //------------------------------------------------------------------------------------------------------------------

    /*
    * Constructors
    * */

    public PersonRegister() {
    }



    private static Logger log = Logger.getLogger(VejRegister.class);

    /*
    * Data source spec
    * */

    @Autowired
    private ConfigurableApplicationContext ctx;

    public Resource getRecordResource() {
        return this.ctx.getResource("classpath:/data/cprVejregister.zip");
    }


    /*
    * Parse definition
    * */

    protected CprRecord parseTrimmedLine(String recordType, String line) {
        CprRecord r = super.parseTrimmedLine(recordType, line);
        if (r != null) {
            return r;
        }
        try {
            if (recordType.equals(VejDataRecord.RECORDTYPE_AKTVEJ)) {
                return new AktivVej(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_BOLIG)) {
                return new Bolig(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_BYDISTRIKT)) {
                return new ByDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_POSTDIST)) {
                //return new PostDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_NOTATVEJ)) {
                //return new NotatVej(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_BYFORNYDIST)) {
                //return new ByfornyelsesDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_DIVDIST)) {
                //return new DiverseDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_EVAKUERDIST)) {
                //return new EvakueringsDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_KIRKEDIST)) {
                //return new KirkeDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_SKOLEDIST)) {
                //return new SkoleDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_BEFOLKDIST)) {
                //return new BefolkningsDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_SOCIALDIST)) {
                //return new SocialDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_SOGNEDIST)) {
                //return new SogneDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_VALGDIST)) {
                //return new ValgDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_VARMEDIST)) {
                //return new VarmeDistrikt(line);
            }
            if (recordType.equals(VejDataRecord.RECORDTYPE_HISTORISKVEJ)) {
                return new HistoriskVej(line);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    * Repositories
    * */

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private DawaModel model;

    /*
    * Database save
    * */

    protected void saveRunToDatabase(RegisterRun run, RegistreringInfo registreringInfo) {
        double time;
        VejRegisterRun vrun = (VejRegisterRun) run;
        int count = 0;

        this.log.info("Preparatory linking");
        time = this.indepTic();
        Level2Container<AktivVej> aktiveVeje = vrun.getAktiveVeje();
        vrun.startInputProcessing();

        // Add connections to AktivVej objects
        for (AktivVej vej : aktiveVeje.getList()) {
            String[] dir = {"fra","til"};
            for (String d : dir) {
                int otherKommuneKode = vej.getInt(d + "KommuneKode");
                int otherVejKode = vej.getInt(d + "VejKode");
                if (otherKommuneKode > 0 && otherVejKode > 0) {
                    AktivVej otherVej = aktiveVeje.get(otherKommuneKode, otherVejKode);
                    if (otherVej != null) {
                        vej.addConnection(otherVej);
                        otherVej.addConnection(vej);
                        count++;
                    }
                }
            }
        }
        ArrayList<AktivVej> orderedList = new ArrayList<AktivVej>();
        for (AktivVej vej : aktiveVeje.getList()) {
            this.recursiveSortRoads(vej, orderedList);
        }
        Level2Container<HashSet<RawVej>> lokalitetData = new Level2Container<HashSet<RawVej>>();

        for (ByDistrikt byDistrikt : vrun.getByDistrikter()) {
            int kommuneKode = byDistrikt.getInt("kommuneKode");
            int vejKode = byDistrikt.getInt("vejKode");
            String byNavn = byDistrikt.get("bynavn");
            HashSet<RawVej> veje = lokalitetData.get(kommuneKode, byNavn);
            if (veje == null) {
                veje = new HashSet<RawVej>();
                lokalitetData.put(kommuneKode, byNavn, veje);
            }
            RawVej vej = new RawVej(kommuneKode, vejKode);

            boolean contains = false;
            for (RawVej v : veje) {
                if (vej.equals(v)) {
                    contains = true;
                }
            }
            if (!contains) {
                veje.add(vej);
                count++;
            }
        }
        time = this.toc(time);
        this.log.info(count + " links created in " + time + " ms (avg " + (time / (double) count) + " ms)");

        // Process each AktivVej object, creating database entries
        // We do this in the VejRegisterRun instance because there is some state information
        // that we don't want to pollute our VejRegister instance with

        this.log.info("Storing VejstykkeEntities in database");
        time = this.indepTic();
        ModelUpdateCounter counter = new ModelUpdateCounter();
        counter.setLog(this.log);

        for (AktivVej vej : orderedList) {
            this.model.setVejstykke(
                    vej.getInt("kommuneKode"), vej.getInt("vejKode"), vej.get("vejNavn"),
                    vej.get("vejAddresseringsnavn"),
                    registreringInfo
            );
            counter.countEntryProcessed();
        }
        //this.model.flush();
        counter.printFinalEntriesProcessed();
        count = counter.getCount();
        time = this.toc(time);
        this.log.info(count + " VejstykkeEntities stored in " + time + " ms (avg " + (time / (double) count) + " ms)");



        this.log.info("Storing AdresseEntities in database");
        time = this.indepTic();
        counter.reset();
        for (Bolig bolig : vrun.getBoliger()) {
            this.model.setAdresse(
                    bolig.getInt("kommuneKode"), bolig.getInt("vejKode"), bolig.get("husNr"), null, bolig.get("etage"), bolig.get("sidedoer"), bolig.get("lokalitet"),
                    registreringInfo, new ArrayList<VirkningEntity>(), false, false
            );
            counter.countEntryProcessed();
        }
        //this.model.flush();
        counter.printFinalEntriesProcessed();
        count = counter.getCount();
        time = this.toc(time);
        this.log.info(count + " AdresseEntities stored in " + time + " ms (avg " + (time / (double) count) + " ms)");



        this.log.info("Storing LokalitetEntities in database");
        time = this.indepTic();
        counter.reset();

        for (int kommuneKode : lokalitetData.intKeySet()) {
            for (String lokalitetsNavn : lokalitetData.get(kommuneKode).keySet()) {
                HashSet<RawVej> veje = lokalitetData.get(kommuneKode, lokalitetsNavn);
                this.model.setLokalitet(kommuneKode, lokalitetsNavn, veje, registreringInfo);
                counter.countEntryProcessed();
            }
        }
        //this.model.flush();
        counter.printFinalEntriesProcessed();
        count = counter.getCount();
        time = this.toc(time);
        this.log.info(count + " LokalitetEntities stored in "+time+" ms (avg " + (time / (double) count) + " ms)");
        registreringInfo.logProcess(this.log);
    }

    private void recursiveSortRoads(AktivVej vej, ArrayList<AktivVej> list) {
        if (!vej.getVisited()) {
            list.add(vej);
            vej.setVisited(true);
            for (AktivVej otherVej : vej.getConnections()) {
                recursiveSortRoads(otherVej, list);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getUploadPartName() {
        return "vejSourceUpload";
    }

    @Override
    public String getSourceTypeFieldName() {
        return "vejSourceType";
    }
    
    @Override
    public String getSourceUrlFieldName() {
        return "vejSourceUrl";
    }

    @Override
    public DataProviderConfiguration getDefaultConfiguration() {
        JSONObject config = new JSONObject();
        config.put(this.getSourceUrlFieldName(),"https://cpr.dk/media/152096/vejregister_hele_landet_pr_150101.zip");
        return new DataProviderConfiguration(config);
    }

    @Override
    public Resource getCorrectionSeed() {
        return this.ctx.getResource("classpath:/data/corrections/vejCorrections.json");
    }
}
