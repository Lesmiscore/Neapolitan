package jadx.core.dex.visitors.regions;

import jadx.core.dex.nodes.*;

public interface IRegionVisitor {

	void processBlock(MethodNode mth, IBlock container);

	/**
	 * @return true for traverse sub-blocks, false otherwise.
	 */
	boolean enterRegion(MethodNode mth, IRegion region);

	void leaveRegion(MethodNode mth, IRegion region);

}
