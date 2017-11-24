package jadx.core.dex.instructions;

import org.jetbrains.annotations.*;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;
import jadx.core.utils.exceptions.*;

public final class PhiInsn extends InsnNode {

	private final Map<RegisterArg, BlockNode> blockBinds;

	public PhiInsn(int regNum, int predecessors) {
		super(InsnType.PHI, predecessors);
		this.blockBinds = new IdentityHashMap<RegisterArg, BlockNode>(predecessors);
		setResult(InsnArg.reg(regNum, ArgType.UNKNOWN));
		add(AFlag.DONT_INLINE);
	}

	public RegisterArg bindArg(BlockNode pred) {
		RegisterArg arg = InsnArg.reg(getResult().getRegNum(), getResult().getType());
		bindArg(arg, pred);
		return arg;
	}

	public void bindArg(RegisterArg arg, BlockNode pred) {
		if (blockBinds.containsValue(pred)) {
			throw new JadxRuntimeException("Duplicate predecessors in PHI insn: " + pred + ", " + this);
		}
		addArg(arg);
		blockBinds.put(arg, pred);
	}

	public BlockNode getBlockByArg(RegisterArg arg) {
		return blockBinds.get(arg);
	}

	public Map<RegisterArg, BlockNode> getBlockBinds() {
		return blockBinds;
	}

	@Override
	@NotNull
	public RegisterArg getArg(int n) {
		return (RegisterArg) super.getArg(n);
	}

	@Override
	public boolean removeArg(InsnArg arg) {
		if (!(arg instanceof RegisterArg)) {
			return false;
		}
		RegisterArg reg = (RegisterArg) arg;
		if (super.removeArg(reg)) {
			blockBinds.remove(reg);
			InstructionRemover.fixUsedInPhiFlag(reg);
			return true;
		}
		return false;
	}

	@Override
	public boolean replaceArg(InsnArg from, InsnArg to) {
		if (!(from instanceof RegisterArg) || !(to instanceof RegisterArg)) {
			return false;
		}
		BlockNode pred = getBlockByArg((RegisterArg) from);
		if (pred == null) {
			throw new JadxRuntimeException("Unknown predecessor block by arg " + from + " in PHI: " + this);
		}
		if (removeArg(from)) {
			bindArg((RegisterArg) to, pred);
		}
		return true;
	}

	@Override
	public void setArg(int n, InsnArg arg) {
		throw new JadxRuntimeException("Unsupported operation for PHI node");
	}

	@Override
	public String toString() {
		return "PHI: " + getResult() + " = " + Utils.listToString(getArguments())
				+ " binds: " + blockBinds;
	}
}
