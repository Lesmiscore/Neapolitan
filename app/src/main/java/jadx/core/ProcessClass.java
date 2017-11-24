package jadx.core;

import org.jetbrains.annotations.*;
import org.slf4j.*;

import java.util.*;

import jadx.core.codegen.*;
import jadx.core.dex.nodes.*;
import jadx.core.dex.visitors.*;
import jadx.core.utils.*;

import static jadx.core.dex.nodes.ProcessState.*;

public final class ProcessClass {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessClass.class);

	private ProcessClass() {
	}

	public static void process(ClassNode cls, List<IDexTreeVisitor> passes, @Nullable CodeGen codeGen) {
		if (codeGen == null && cls.getState() == PROCESSED) {
			return;
		}
		synchronized (cls) {
			try {
				if (cls.getState() == NOT_LOADED) {
					cls.load();
					cls.setState(STARTED);
					for (IDexTreeVisitor visitor : passes) {
						DepthTraversal.visit(visitor, cls);
					}
					cls.setState(PROCESSED);
				}
				if (cls.getState() == PROCESSED && codeGen != null) {
					processDependencies(cls, passes);
					codeGen.visit(cls);
					cls.setState(GENERATED);
				}
			} catch (Exception e) {
				ErrorsCounter.classError(cls, e.getClass().getSimpleName(), e);
			} finally {
				if (cls.getState() == GENERATED) {
					cls.unload();
					cls.setState(UNLOADED);
				}
			}
		}
	}

	static void processDependencies(ClassNode cls, List<IDexTreeVisitor> passes) {
		for (ClassNode depCls : cls.getDependencies()) {
			process(depCls, passes, null);
		}
	}
}
