package jadx.core.dex.visitors.typeinference;

import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;

public class SelectTypeVisitor {

	private SelectTypeVisitor() {
	}

	public static void visit(DexNode dex, InsnNode insn) {
		InsnArg res = insn.getResult();
		if (res != null && !res.getType().isTypeKnown()) {
			selectType(dex, res);
		}
		for (InsnArg arg : insn.getArguments()) {
			if (!arg.getType().isTypeKnown()) {
				selectType(dex, arg);
			}
		}
	}

	private static void selectType(DexNode dex, InsnArg arg) {
		ArgType t = arg.getType();
		ArgType newType = ArgType.merge(dex, t, t.selectFirst());
		arg.setType(newType);
	}

}
