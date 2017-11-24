package jadx.core.codegen;

import java.util.*;
import java.util.Map.*;

import jadx.core.*;
import jadx.core.dex.attributes.*;
import jadx.core.dex.attributes.annotations.*;
import jadx.core.dex.info.*;
import jadx.core.dex.instructions.args.*;
import jadx.core.dex.nodes.*;
import jadx.core.utils.*;
import jadx.core.utils.exceptions.*;

public class AnnotationGen {

	private final ClassNode cls;
	private final ClassGen classGen;

	public AnnotationGen(ClassNode cls, ClassGen classGen) {
		this.cls = cls;
		this.classGen = classGen;
	}

	public void addForClass(CodeWriter code) {
		add(cls, code);
	}

	public void addForMethod(CodeWriter code, MethodNode mth) {
		add(mth, code);
	}

	public void addForField(CodeWriter code, FieldNode field) {
		add(field, code);
	}

	public void addForParameter(CodeWriter code, MethodParameters paramsAnnotations, int n) {
		List<AnnotationsList> paramList = paramsAnnotations.getParamList();
		if (n >= paramList.size()) {
			return;
		}
		AnnotationsList aList = paramList.get(n);
		if (aList == null || aList.isEmpty()) {
			return;
		}
		for (Annotation a : aList.getAll()) {
			formatAnnotation(code, a);
			code.add(' ');
		}
	}

	private void add(IAttributeNode node, CodeWriter code) {
		AnnotationsList aList = node.get(AType.ANNOTATION_LIST);
		if (aList == null || aList.isEmpty()) {
			return;
		}
		for (Annotation a : aList.getAll()) {
			String aCls = a.getAnnotationClass();
			if (aCls.startsWith(Consts.DALVIK_ANNOTATION_PKG)) {
				// skip
				if (Consts.DEBUG) {
					code.startLine("// " + a);
				}
			} else {
				code.startLine();
				formatAnnotation(code, a);
			}
		}
	}

	private void formatAnnotation(CodeWriter code, Annotation a) {
		code.add('@');
		classGen.useType(code, a.getType());
		Map<String, Object> vl = a.getValues();
		if (!vl.isEmpty()) {
			code.add('(');
			if (vl.size() == 1 && vl.containsKey("value")) {
				encodeValue(code, vl.get("value"));
			} else {
				for (Iterator<Entry<String, Object>> it = vl.entrySet().iterator(); it.hasNext(); ) {
					Entry<String, Object> e = it.next();
					code.add(e.getKey());
					code.add(" = ");
					encodeValue(code, e.getValue());
					if (it.hasNext()) {
						code.add(", ");
					}
				}
			}
			code.add(')');
		}
	}

	@SuppressWarnings("unchecked")
	public void addThrows(MethodNode mth, CodeWriter code) {
		Annotation an = mth.getAnnotation(Consts.DALVIK_THROWS);
		if (an != null) {
			Object exs = an.getDefaultValue();
			code.add(" throws ");
			for (Iterator<ArgType> it = ((List<ArgType>) exs).iterator(); it.hasNext(); ) {
				ArgType ex = it.next();
				classGen.useType(code, ex);
				if (it.hasNext()) {
					code.add(", ");
				}
			}
		}
	}

	public Object getAnnotationDefaultValue(String name) {
		Annotation an = cls.getAnnotation(Consts.DALVIK_ANNOTATION_DEFAULT);
		if (an != null) {
			Annotation defAnnotation = (Annotation) an.getDefaultValue();
			return defAnnotation.getValues().get(name);
		}
		return null;
	}

	// TODO: refactor this boilerplate code
	public void encodeValue(CodeWriter code, Object val) {
		if (val == null) {
			code.add("null");
			return;
		}
		if (val instanceof String) {
			code.add(StringUtils.unescapeString((String) val));
		} else if (val instanceof Integer) {
			code.add(TypeGen.formatInteger((Integer) val));
		} else if (val instanceof Character) {
			code.add(StringUtils.unescapeChar((Character) val));
		} else if (val instanceof Boolean) {
			code.add(Boolean.TRUE.equals(val) ? "true" : "false");
		} else if (val instanceof Float) {
			code.add(TypeGen.formatFloat((Float) val));
		} else if (val instanceof Double) {
			code.add(TypeGen.formatDouble((Double) val));
		} else if (val instanceof Long) {
			code.add(TypeGen.formatLong((Long) val));
		} else if (val instanceof Short) {
			code.add(TypeGen.formatShort((Short) val));
		} else if (val instanceof Byte) {
			code.add(TypeGen.formatByte((Byte) val));
		} else if (val instanceof ArgType) {
			classGen.useType(code, (ArgType) val);
			code.add(".class");
		} else if (val instanceof FieldInfo) {
			// must be a static field
			FieldInfo field = (FieldInfo) val;
			InsnGen.makeStaticFieldAccess(code, field, classGen);
		} else if (val instanceof Iterable) {
			code.add('{');
			Iterator<?> it = ((Iterable) val).iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				encodeValue(code, obj);
				if (it.hasNext()) {
					code.add(", ");
				}
			}
			code.add('}');
		} else if (val instanceof Annotation) {
			formatAnnotation(code, (Annotation) val);
		} else {
			// TODO: also can be method values
			throw new JadxRuntimeException("Can't decode value: " + val + " (" + val.getClass() + ")");
		}
	}
}
