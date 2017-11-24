package jadx.core.dex.visitors;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;

public class DepthTraversal {

	public static void visit(IDexTreeVisitor visitor, ClassNode cls) {
		try {
			if (visitor.visit(cls)) {
				for (ClassNode inCls : cls.getInnerClasses()) {
					visit(visitor, inCls);
				}
				for (MethodNode mth : cls.getMethods()) {
					visit(visitor, mth);
				}
			}
		} catch (Throwable e) {
			ErrorsCounter.classError(cls,
					e.getClass().getSimpleName() + " in pass: " + visitor.getClass().getSimpleName(), e);
		}
	}

	public static void visit(IDexTreeVisitor visitor, MethodNode mth) {
		if (mth.contains(AType.JADX_ERROR)) {
			return;
		}
		try {
			visitor.visit(mth);
		} catch (Throwable e) {
			ErrorsCounter.methodError(mth,
					e.getClass().getSimpleName() + " in pass: " + visitor.getClass().getSimpleName(), e);
		}
	}
}
