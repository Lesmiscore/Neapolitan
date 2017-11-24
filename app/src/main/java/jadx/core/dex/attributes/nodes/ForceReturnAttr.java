package jadx.core.dex.attributes.nodes;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;

public class ForceReturnAttr implements IAttribute {

	private final InsnNode returnInsn;

	public ForceReturnAttr(InsnNode retInsn) {
		this.returnInsn = retInsn;
	}

	public InsnNode getReturnInsn() {
		return returnInsn;
	}

	@Override
	public AType<ForceReturnAttr> getType() {
		return AType.FORCE_RETURN;
	}

	@Override
	public String toString() {
		return "FORCE_RETURN " + Utils.listToString(returnInsn.getArguments());
	}

}
