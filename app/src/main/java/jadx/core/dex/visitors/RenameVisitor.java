package jadx.core.dex.visitors;

import org.apache.commons.io.*;

import java.io.*;
import java.util.*;

import jadx.api.*;
import jadx.core.*;
import jadx.core.codegen.*;
import jadx.core.deobf.*;
import jadx.core.dex.attributes.*;
import jadx.core.dex.info.*;
import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

public class RenameVisitor extends AbstractVisitor {

	private static final boolean CASE_SENSITIVE_FS = IOCase.SYSTEM.isCaseSensitive();

	private Deobfuscator deobfuscator;

	@Override
	public void init(RootNode root) {
		IJadxArgs args = root.getArgs();

		final String firstInputFileName = root.getDexNodes().get(0).getInputFile().getFile().getAbsolutePath();
		final String inputPath = org.apache.commons.io.FilenameUtils.getFullPathNoEndSeparator(
				firstInputFileName);
		final String inputName = org.apache.commons.io.FilenameUtils.getBaseName(firstInputFileName);

		File deobfMapFile = new File(inputPath, inputName + ".jobf");
		deobfuscator = new Deobfuscator(args, root.getDexNodes(), deobfMapFile);
		boolean deobfuscationOn = args.isDeobfuscationOn();
		if (deobfuscationOn) {
			deobfuscator.execute();
		}
		checkClasses(root);
	}

	@Override
	public boolean visit(ClassNode cls) throws JadxException {
		checkFields(cls);
		checkMethods(cls);
		for (ClassNode inner : cls.getInnerClasses()) {
			visit(inner);
		}
		return false;
	}

	private void checkClasses(RootNode root) {
		Set<String> clsNames = new HashSet<String>();
		for (ClassNode cls : root.getClasses(true)) {
			checkClassName(cls);
			if (!CASE_SENSITIVE_FS) {
				ClassInfo classInfo = cls.getClassInfo();
				String clsFileName = classInfo.getAlias().getFullPath();
				if (!clsNames.add(clsFileName.toLowerCase())) {
					String newShortName = deobfuscator.getClsAlias(cls);
					String newFullName = classInfo.makeFullClsName(newShortName, true);
					classInfo.rename(cls.dex(), newFullName);
					clsNames.add(classInfo.getAlias().getFullPath().toLowerCase());
				}
			}
		}
	}

	private void checkClassName(ClassNode cls) {
		ClassInfo classInfo = cls.getClassInfo();
		String clsName = classInfo.getAlias().getShortName();
		String newShortName = null;
		char firstChar = clsName.charAt(0);
		if (Character.isDigit(firstChar)) {
			newShortName = Consts.ANONYMOUS_CLASS_PREFIX + clsName;
		} else if (firstChar == '$') {
			newShortName = "C" + clsName;
		}
		if (newShortName != null) {
			classInfo.rename(cls.dex(), classInfo.makeFullClsName(newShortName, true));
		}
		if (classInfo.getAlias().getPackage().isEmpty()) {
			String fullName = classInfo.makeFullClsName(classInfo.getAlias().getShortName(), true);
			String newFullName = Consts.DEFAULT_PACKAGE_NAME + "." + fullName;
			classInfo.rename(cls.dex(), newFullName);
		}
	}

	private void checkFields(ClassNode cls) {
		Set<String> names = new HashSet<String>();
		for (FieldNode field : cls.getFields()) {
			FieldInfo fieldInfo = field.getFieldInfo();
			if (!names.add(fieldInfo.getAlias())) {
				fieldInfo.setAlias(deobfuscator.makeFieldAlias(field));
			}
		}
	}

	private void checkMethods(ClassNode cls) {
		Set<String> names = new HashSet<String>();
		for (MethodNode mth : cls.getMethods()) {
			if (mth.contains(AFlag.DONT_GENERATE)) {
				continue;
			}
			MethodInfo methodInfo = mth.getMethodInfo();
			String signature = makeMethodSignature(methodInfo);
			if (!names.add(signature)) {
				methodInfo.setAlias(deobfuscator.makeMethodAlias(mth));
			}
		}
	}

	private static String makeMethodSignature(MethodInfo methodInfo) {
		StringBuilder signature = new StringBuilder();
		signature.append(methodInfo.getAlias());
		signature.append('(');
		for (ArgType arg : methodInfo.getArgumentsTypes()) {
			signature.append(TypeGen.signature(arg));
		}
		signature.append(')');
		return signature.toString();
	}
}
