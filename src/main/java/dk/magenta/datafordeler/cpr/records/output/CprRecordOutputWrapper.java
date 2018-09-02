package dk.magenta.datafordeler.cpr.records.output;

import dk.magenta.datafordeler.cpr.data.CprEntity;
import dk.magenta.datafordeler.cpr.records.CprBitemporalRecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CprRecordOutputWrapper<E extends CprEntity> extends RecordOutputWrapper<E> {

    private final Set<String> removeFieldNames = new HashSet<>(Arrays.asList(new String[]{
            CprBitemporalRecord.IO_FIELD_REGISTRATION_FROM,
            CprBitemporalRecord.IO_FIELD_REGISTRATION_TO,
            CprBitemporalRecord.IO_FIELD_EFFECT_FROM,
            CprBitemporalRecord.IO_FIELD_EFFECT_FROM_UNCERTAIN,
            CprBitemporalRecord.IO_FIELD_EFFECT_TO,
            CprBitemporalRecord.IO_FIELD_EFFECT_TO_UNCERTAIN
    }));

    @Override
    public Set<String> getRemoveFieldNames() {
        return this.removeFieldNames;
    }

}
