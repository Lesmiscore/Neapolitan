package jadx.core.dex.visitors;

import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

/**
 * Visitor interface for traverse dex tree
 */
public interface IDexTreeVisitor {

	/**
	 * Called after loading dex tree, but before visitor traversal.
	 */
	void init(RootNode root) throws JadxException;

	/**
	 * Visit class
	 *
	 * @return false for disable child methods and inner classes traversal
	 * @throws JadxException
	 */
	boolean visit(ClassNode cls) throws JadxException;

	/**
	 * Visit method
	 *
	 * @throws JadxException
	 */
	void visit(MethodNode mth) throws JadxException;
}
