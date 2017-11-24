package jadx.core.dex.visitors;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;
import jadx.core.dex.trycatch.*;
import jadx.core.utils.exceptions.*;

public class FallbackModeVisitor extends AbstractVisitor {

	@Override
	public void visit(MethodNode mth) throws JadxException {
		if (mth.isNoCode()) {
			return;
		}
		for (InsnNode insn : mth.getInstructions()) {
			if (insn == null) {
				continue;
			}
			// remove 'exception catch' for instruction which don't throw any exceptions
			CatchAttr catchAttr = insn.get(AType.CATCH_BLOCK);
			if (catchAttr != null) {
				switch (insn.getType()) {
					case RETURN:
					case IF:
					case GOTO:
					case MOVE:
					case MOVE_EXCEPTION:
					case ARITH: // ??
					case NEG:
					case CONST:
					case CONST_STR:
					case CONST_CLASS:
					case CMP_L:
					case CMP_G:
						catchAttr.getTryBlock().removeInsn(mth, insn);
						break;

					default:
						break;
				}
			}
		}
	}
}
