package jadx.core.dex.visitors.regions;

import org.slf4j.*;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;
import jadx.core.dex.regions.loops.*;
import jadx.core.dex.visitors.*;
import jadx.core.utils.*;
import jadx.core.utils.exceptions.*;

public class CheckRegions extends AbstractVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(CheckRegions.class);

	@Override
	public void visit(MethodNode mth) throws JadxException {
		if (mth.isNoCode()
				|| mth.getBasicBlocks().isEmpty()
				|| mth.contains(AType.JADX_ERROR)) {
			return;
		}

		// check if all blocks included in regions
		final Set<BlockNode> blocksInRegions = new HashSet<BlockNode>();
		DepthRegionTraversal.traverse(mth, new AbstractRegionVisitor() {
			@Override
			public void processBlock(MethodNode mth, IBlock container) {
				if (!(container instanceof BlockNode)) {
					return;
				}
				BlockNode block = (BlockNode) container;
				if (blocksInRegions.add(block)) {
					return;
				}
				if (!block.contains(AFlag.RETURN)
						&& !block.contains(AFlag.SKIP)
						&& !block.contains(AFlag.SYNTHETIC)
						&& !block.getInstructions().isEmpty()) {
					// TODO
					// mth.add(AFlag.INCONSISTENT_CODE);
					LOG.debug(" Duplicated block: {} in {}", block, mth);
				}
			}
		});
		if (mth.getBasicBlocks().size() != blocksInRegions.size()) {
			for (BlockNode block : mth.getBasicBlocks()) {
				if (!blocksInRegions.contains(block)
						&& !block.getInstructions().isEmpty()
						&& !block.contains(AFlag.SKIP)) {
					mth.add(AFlag.INCONSISTENT_CODE);
					LOG.debug(" Missing block: {} in {}", block, mth);
				}
			}
		}

		// check loop conditions
		DepthRegionTraversal.traverse(mth, new AbstractRegionVisitor() {
			@Override
			public boolean enterRegion(MethodNode mth, IRegion region) {
				if (region instanceof LoopRegion) {
					BlockNode loopHeader = ((LoopRegion) region).getHeader();
					if (loopHeader != null && loopHeader.getInstructions().size() != 1) {
						ErrorsCounter.methodError(mth, "Incorrect condition in loop: " + loopHeader);
					}
				}
				return true;
			}
		});
	}
}
