package dk.magenta.datafordeler.cpr.synchronization;

import dk.magenta.datafordeler.core.io.PluginSourceData;

public class CprSourceData implements PluginSourceData {

    private String schema;
    private String data;

    public CprSourceData(String schema, String data) {
        this.schema = schema;
        this.data = data;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public String getData() {
        return this.data;
    }

}
