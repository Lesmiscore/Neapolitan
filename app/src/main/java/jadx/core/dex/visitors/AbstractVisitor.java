package jadx.core.dex.visitors;

import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

public class AbstractVisitor implements IDexTreeVisitor {

	@Override
	public void init(RootNode root) throws JadxException {
	}

	@Override
	public boolean visit(ClassNode cls) throws JadxException {
		return true;
	}

	@Override
	public void visit(MethodNode mth) throws JadxException {
	}

}
