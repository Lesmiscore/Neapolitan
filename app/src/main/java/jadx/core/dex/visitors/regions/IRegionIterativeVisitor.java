package jadx.core.dex.visitors.regions;

import jadx.core.dex.nodes.*;

public interface IRegionIterativeVisitor {

	/**
	 * If return 'true' traversal will be restarted.
	 */
	boolean visitRegion(MethodNode mth, IRegion region);

}
