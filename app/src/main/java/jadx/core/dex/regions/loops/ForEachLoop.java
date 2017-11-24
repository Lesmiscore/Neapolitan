package jadx.core.dex.regions.loops;

import jadx.core.dex.instructions.args.*;

public final class ForEachLoop extends LoopType {
	private final RegisterArg varArg;
	private final InsnArg iterableArg;

	public ForEachLoop(RegisterArg varArg, InsnArg iterableArg) {
		this.varArg = varArg;
		this.iterableArg = iterableArg;
	}

	public RegisterArg getVarArg() {
		return varArg;
	}

	public InsnArg getIterableArg() {
		return iterableArg;
	}
}
