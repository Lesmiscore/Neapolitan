package jadx.core.dex.visitors.regions;

import jadx.core.dex.nodes.*;

public abstract class AbstractRegionVisitor implements IRegionVisitor {

	@Override
	public boolean enterRegion(MethodNode mth, IRegion region) {
		return true;
	}

	@Override
	public void processBlock(MethodNode mth, IBlock container) {
	}

	@Override
	public void leaveRegion(MethodNode mth, IRegion region) {
	}

}
