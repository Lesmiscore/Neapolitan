package jadx.core.dex.visitors.regions;

import java.util.*;

import jadx.core.dex.nodes.*;

public abstract class TracedRegionVisitor implements IRegionVisitor {

	protected final Deque<IRegion> regionStack = new ArrayDeque<IRegion>();

	@Override
	public boolean enterRegion(MethodNode mth, IRegion region) {
		regionStack.push(region);
		return true;
	}

	@Override
	public void processBlock(MethodNode mth, IBlock container) {
		IRegion curRegion = regionStack.peek();
		processBlockTraced(mth, container, curRegion);
	}

	public abstract void processBlockTraced(MethodNode mth, IBlock container, IRegion currentRegion);

	@Override
	public void leaveRegion(MethodNode mth, IRegion region) {
		regionStack.pop();
	}
}
