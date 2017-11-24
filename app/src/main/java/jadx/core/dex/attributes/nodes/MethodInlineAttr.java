package jadx.core.dex.attributes.nodes;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;

public class MethodInlineAttr implements IAttribute {

	private final InsnNode insn;

	public MethodInlineAttr(InsnNode insn) {
		this.insn = insn;
	}

	public InsnNode getInsn() {
		return insn;
	}

	@Override
	public AType<MethodInlineAttr> getType() {
		return AType.METHOD_INLINE;
	}

	@Override
	public String toString() {
		return "INLINE: " + insn;
	}
}
