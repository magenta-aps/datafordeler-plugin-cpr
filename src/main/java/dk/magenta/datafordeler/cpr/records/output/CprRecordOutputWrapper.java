package dk.magenta.datafordeler.cpr.records.output;

import dk.magenta.datafordeler.cpr.data.CprEntity;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import java.util.Arrays;
import java.util.List;

public abstract class CprRecordOutputWrapper<E extends CprEntity> extends RecordOutputWrapper<E> {

    private final List<String> removeFieldNames = Arrays.asList(new String[]{
            CprBitemporalRecord.IO_FIELD_REGISTRATION_FROM,
            CprBitemporalRecord.IO_FIELD_REGISTRATION_TO,
            CprBitemporalRecord.IO_FIELD_EFFECT_FROM,
            CprBitemporalRecord.IO_FIELD_EFFECT_FROM_UNCERTAIN,
            CprBitemporalRecord.IO_FIELD_EFFECT_TO,
            CprBitemporalRecord.IO_FIELD_EFFECT_TO_UNCERTAIN
    });

    public List<String> getRemoveFieldNames() {
        return this.removeFieldNames;
    }

}
