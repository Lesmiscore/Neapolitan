package jadx.core.dex.visitors.typeinference;

import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;

public class CheckTypeVisitor {

	public static void visit(MethodNode mth, InsnNode insn) {
		if (insn.getResult() != null
				&& !insn.getResult().getType().isTypeKnown()) {
			error("Wrong return type", mth, insn);
			return;
		}

		for (InsnArg arg : insn.getArguments()) {
			if (!arg.getType().isTypeKnown()) {
				error("Wrong type", mth, insn);
				return;
			}
		}
	}

	private static void error(String msg, MethodNode mth, InsnNode insn) {
		ErrorsCounter.methodError(mth, msg + ": " + insn);
	}
}
