package jadx.core.dex.nodes;

import java.util.*;

public interface IBranchRegion extends IRegion {

	/**
	 * Return list of branches in this region.
	 * NOTE: Contains 'null' elements for indicate empty branches.
	 */
	List<IContainer> getBranches();

}
