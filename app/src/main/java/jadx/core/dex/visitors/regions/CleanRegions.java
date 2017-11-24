package jadx.core.dex.visitors.regions;

import org.slf4j.*;

import java.util.*;

import jadx.core.dex.nodes.*;
import jadx.core.dex.regions.*;

public class CleanRegions {
	private static final Logger LOG = LoggerFactory.getLogger(CleanRegions.class);

	private CleanRegions() {
	}

	public static void process(MethodNode mth) {
		if (mth.isNoCode() || mth.getBasicBlocks().isEmpty()) {
			return;
		}
		IRegionVisitor removeEmptyBlocks = new AbstractRegionVisitor() {
			@Override
			public boolean enterRegion(MethodNode mth, IRegion region) {
				if (!(region instanceof Region)) {
					return true;
				}

				for (Iterator<IContainer> it = region.getSubBlocks().iterator(); it.hasNext(); ) {
					IContainer container = it.next();
					if (container instanceof BlockNode) {
						BlockNode block = (BlockNode) container;
						if (block.getInstructions().isEmpty()) {
							try {
								it.remove();
							} catch (UnsupportedOperationException e) {
								LOG.warn("Can't remove block: {} from: {}, mth: {}", block, region, mth);
							}
						}
					}

				}
				return true;
			}
		};
		DepthRegionTraversal.traverse(mth, removeEmptyBlocks);
	}
}
