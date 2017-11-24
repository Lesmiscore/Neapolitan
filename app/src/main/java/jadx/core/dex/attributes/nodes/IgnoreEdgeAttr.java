package jadx.core.dex.attributes.nodes;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;

public class IgnoreEdgeAttr implements IAttribute {

	private final Set<BlockNode> blocks = new HashSet<BlockNode>(3);

	public Set<BlockNode> getBlocks() {
		return blocks;
	}

	public boolean contains(BlockNode block) {
		return blocks.contains(block);
	}

	@Override
	public AType<IgnoreEdgeAttr> getType() {
		return AType.IGNORE_EDGE;
	}

	@Override
	public String toString() {
		return "IGNORE_EDGES: " + Utils.listToString(blocks);
	}
}
