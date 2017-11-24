package jadx.core.dex.attributes.nodes;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.instructions.args.*;
import jadx.core.utils.*;

/**
 * List of variables to be declared at region start.
 */
public class DeclareVariablesAttr implements IAttribute {

	private final List<RegisterArg> vars = new LinkedList<RegisterArg>();

	public Iterable<RegisterArg> getVars() {
		return vars;
	}

	public void addVar(RegisterArg arg) {
		vars.add(arg);
	}

	@Override
	public AType<DeclareVariablesAttr> getType() {
		return AType.DECLARE_VARIABLES;
	}

	@Override
	public String toString() {
		return "DECL_VAR: " + Utils.listToString(vars);
	}
}
