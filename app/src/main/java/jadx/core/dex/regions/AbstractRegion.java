package jadx.core.dex.regions;

import org.slf4j.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;

public abstract class AbstractRegion extends AttrNode implements IRegion {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRegion.class);

	private IRegion parent;

	public AbstractRegion(IRegion parent) {
		this.parent = parent;
	}

	@Override
	public IRegion getParent() {
		return parent;
	}

	public void setParent(IRegion parent) {
		this.parent = parent;
	}

	@Override
	public boolean replaceSubBlock(IContainer oldBlock, IContainer newBlock) {
		LOG.warn("Replace sub block not supported for class \"{}\"", this.getClass());
		return false;
	}
}
