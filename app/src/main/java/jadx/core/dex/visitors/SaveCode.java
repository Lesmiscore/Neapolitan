package jadx.core.dex.visitors;

import java.io.*;

import jadx.api.*;
import jadx.core.codegen.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

public class SaveCode extends AbstractVisitor {
	private final File dir;
	private final IJadxArgs args;

	public SaveCode(File dir, IJadxArgs args) {
		this.args = args;
		this.dir = dir;
	}

	@Override
	public boolean visit(ClassNode cls) throws CodegenException {
		save(dir, args, cls);
		return false;
	}

	public static void save(File dir, IJadxArgs args, ClassNode cls) {
		CodeWriter clsCode = cls.getCode();
		String fileName = cls.getClassInfo().getFullPath() + ".java";
		if (args.isFallbackMode()) {
			fileName += ".jadx";
		}
		clsCode.save(dir, fileName);
	}
}
