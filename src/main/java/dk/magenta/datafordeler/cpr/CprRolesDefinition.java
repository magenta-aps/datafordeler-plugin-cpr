package dk.magenta.datafordeler.cpr;

import dk.magenta.datafordeler.core.plugin.RolesDefinition;
import dk.magenta.datafordeler.core.role.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    public static ExecuteCommandRole EXECUTE_CPR_PULL_ROLE = new ExecuteCommandRole(
            "pull",
            new HashMap<String, Object>() {{
                put("plugin", "cpr");
            }},
            new ExecuteCommandRoleVersion(
                    1.0f,
                    "Role that gives access to start and stop the PULL command for CPR data"
            )
    );

    @Override
    public List<SystemRole> getRoles() {
        ArrayList<SystemRole> roles = new ArrayList<>();
        roles.add(READ_CPR_ROLE);
        roles.add(EXECUTE_CPR_PULL_ROLE);
        return roles;
    }
}
