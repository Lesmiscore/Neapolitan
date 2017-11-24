package jadx.core.utils;

import com.android.dx.io.instructions.*;

import org.jetbrains.annotations.*;
import org.slf4j.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.info.*;
import jadx.core.dex.instructions.*;
import jadx.core.dex.nodes.*;
import jadx.core.dex.nodes.parser.*;
import jadx.core.utils.exceptions.*;

public class InsnUtils {

	private static final Logger LOG = LoggerFactory.getLogger(InsnUtils.class);

	private InsnUtils() {
	}

	public static int getArg(DecodedInstruction insn, int arg) {
		switch (arg) {
			case 0:
				return insn.getA();
			case 1:
				return insn.getB();
			case 2:
				return insn.getC();
			case 3:
				return insn.getD();
			case 4:
				return insn.getE();
		}
		throw new JadxRuntimeException("Wrong argument number: " + arg);
	}

	public static String formatOffset(int offset) {
		if (offset < 0) {
			return "?";
		} else {
			return String.format("0x%04x", offset);
		}
	}

	public static String insnTypeToString(InsnType type) {
		return type.toString() + "  ";
	}

	public static String indexToString(Object index) {
		if (index == null) {
			return "";
		}
		if (index instanceof String) {
			return "\"" + index + "\"";
		} else {
			return index.toString();
		}
	}

	/**
	 * Return constant value from insn or null if not constant.
	 *
	 * @return LiteralArg, String, ArgType or null
	 */
	@Nullable
	public static Object getConstValueByInsn(DexNode dex, InsnNode insn) {
		switch (insn.getType()) {
			case CONST:
				return insn.getArg(0);
			case CONST_STR:
				return ((ConstStringNode) insn).getString();
			case CONST_CLASS:
				return ((ConstClassNode) insn).getClsType();
			case SGET:
				FieldInfo f = (FieldInfo) ((IndexInsnNode) insn).getIndex();
				FieldNode fieldNode = dex.resolveField(f);
				if (fieldNode != null) {
					FieldInitAttr attr = fieldNode.get(AType.FIELD_INIT);
					if (attr != null) {
						return attr.getValue();
					}
				} else {
					LOG.warn("Field {} not found in dex {}", f, dex);
				}
				break;
		}
		return null;
	}
}
