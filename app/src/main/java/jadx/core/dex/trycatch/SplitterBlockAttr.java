package jadx.core.dex.trycatch;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;

public class SplitterBlockAttr implements IAttribute {

	private final BlockNode block;

	public SplitterBlockAttr(BlockNode block) {
		this.block = block;
	}

	public BlockNode getBlock() {
		return block;
	}

	@Override
	public AType<SplitterBlockAttr> getType() {
		return AType.SPLITTER_BLOCK;
	}

	@Override
	public String toString() {
		return "Splitter: " + block;
	}

}
