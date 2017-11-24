package jadx.core.codegen;

import jadx.api.*;
import jadx.core.dex.nodes.*;
import jadx.core.dex.visitors.*;
import jadx.core.utils.exceptions.*;

public class CodeGen extends AbstractVisitor {

	private final IJadxArgs args;

	public CodeGen(IJadxArgs args) {
		this.args = args;
	}

	@Override
	public boolean visit(ClassNode cls) throws CodegenException {
		ClassGen clsGen = new ClassGen(cls, args);
		CodeWriter clsCode = clsGen.makeClass();
		clsCode.finish();
		cls.setCode(clsCode);
		return false;
	}

}
