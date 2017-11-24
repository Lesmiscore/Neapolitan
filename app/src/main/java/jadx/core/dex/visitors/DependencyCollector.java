package jadx.core.dex.visitors;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.info.*;
import jadx.core.dex.instructions.*;
import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

public class DependencyCollector extends AbstractVisitor {

	@Override
	public boolean visit(ClassNode cls) throws JadxException {
		DexNode dex = cls.dex();
		Set<ClassNode> depList = cls.getDependencies();
		processClass(cls, dex, depList);
		for (ClassNode inner : cls.getInnerClasses()) {
			processClass(inner, dex, depList);
		}
		depList.remove(cls);
		return false;
	}

	private static void processClass(ClassNode cls, DexNode dex, Set<ClassNode> depList) {
		addDep(dex, depList, cls.getSuperClass());
		for (ArgType iType : cls.getInterfaces()) {
			addDep(dex, depList, iType);
		}
		for (FieldNode fieldNode : cls.getFields()) {
			addDep(dex, depList, fieldNode.getType());
		}
		// TODO: process annotations and generics
		for (MethodNode methodNode : cls.getMethods()) {
			if (methodNode.isNoCode() || methodNode.contains(AType.JADX_ERROR)) {
				continue;
			}
			processMethod(dex, depList, methodNode);
		}
	}

	private static void processMethod(DexNode dex, Set<ClassNode> depList, MethodNode methodNode) {
		addDep(dex, depList, methodNode.getParentClass());
		addDep(dex, depList, methodNode.getReturnType());
		for (ArgType arg : methodNode.getMethodInfo().getArgumentsTypes()) {
			addDep(dex, depList, arg);
		}
		for (BlockNode block : methodNode.getBasicBlocks()) {
			for (InsnNode insnNode : block.getInstructions()) {
				processInsn(dex, depList, insnNode);
			}
		}
	}

	// TODO: add custom instructions processing
	private static void processInsn(DexNode dex, Set<ClassNode> depList, InsnNode insnNode) {
		RegisterArg result = insnNode.getResult();
		if (result != null) {
			addDep(dex, depList, result.getType());
		}
		for (InsnArg arg : insnNode.getArguments()) {
			if (arg.isInsnWrap()) {
				processInsn(dex, depList, ((InsnWrapArg) arg).getWrapInsn());
			} else {
				addDep(dex, depList, arg.getType());
			}
		}
		processCustomInsn(dex, depList, insnNode);
	}

	private static void processCustomInsn(DexNode dex, Set<ClassNode> depList, InsnNode insn) {
		if (insn instanceof IndexInsnNode) {
			Object index = ((IndexInsnNode) insn).getIndex();
			if (index instanceof FieldInfo) {
				addDep(dex, depList, ((FieldInfo) index).getDeclClass());
			} else if (index instanceof ArgType) {
				addDep(dex, depList, (ArgType) index);
			}
		} else if (insn instanceof InvokeNode) {
			ClassInfo declClass = ((InvokeNode) insn).getCallMth().getDeclClass();
			addDep(dex, depList, declClass);
		}
	}

	private static void addDep(DexNode dex, Set<ClassNode> depList, ArgType type) {
		if (type != null) {
			if (type.isObject()) {
				addDep(dex, depList, ClassInfo.fromName(dex, type.getObject()));
				ArgType[] genericTypes = type.getGenericTypes();
				if (type.isGeneric() && genericTypes != null) {
					for (ArgType argType : genericTypes) {
						addDep(dex, depList, argType);
					}
				}
			} else if (type.isArray()) {
				addDep(dex, depList, type.getArrayRootElement());
			}
		}
	}

	private static void addDep(DexNode dex, Set<ClassNode> depList, ClassInfo clsInfo) {
		if (clsInfo != null) {
			ClassNode node = dex.resolveClass(clsInfo);
			addDep(dex, depList, node);
		}
	}

	private static void addDep(DexNode dex, Set<ClassNode> depList, ClassNode clsNode) {
		if (clsNode != null) {
			// add only top classes
			depList.add(clsNode.getTopParentClass());
		}
	}
}
