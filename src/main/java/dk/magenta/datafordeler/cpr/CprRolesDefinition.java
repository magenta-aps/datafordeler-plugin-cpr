package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.plugin.RolesDefinition;
import dk.magenta.datafordeler.core.role.ReadServiceRole;
import dk.magenta.datafordeler.core.role.ReadServiceRoleVersion;
import dk.magenta.datafordeler.core.role.SystemRole;

import java.util.Collections;
import java.util.List;

/**
 * Created by jubk on 01-07-2017.
 */
public class CprRolesDefinition extends RolesDefinition {

    public static ReadServiceRole READ_CPR_ROLE = new ReadServiceRole(
            "Cpr",
            new ReadServiceRoleVersion(
                    1.0f,
                    "Role that gives access to all CPR data."
            )
    );

    @Override
    public List<SystemRole> getRoles() {
        return Collections.singletonList(READ_CPR_ROLE);
    }
}
