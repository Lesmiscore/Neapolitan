package jadx.core.dex.attributes;

import jadx.core.dex.attributes.annotations.*;
import jadx.core.dex.attributes.nodes.*;
import jadx.core.dex.nodes.parser.*;
import jadx.core.dex.trycatch.*;

/**
 * Attribute types enumeration,
 * uses generic type for omit cast after 'AttributeStorage.get' method
 *
 * @param <T> attribute class implementation
 */
public class AType<T extends IAttribute> {

	public static final AType<AttrList<JumpInfo>> JUMP = new AType<AttrList<JumpInfo>>();
	public static final AType<AttrList<LoopInfo>> LOOP = new AType<AttrList<LoopInfo>>();
	public static final AType<AttrList<EdgeInsnAttr>> EDGE_INSN = new AType<AttrList<EdgeInsnAttr>>();

	public static final AType<ExcHandlerAttr> EXC_HANDLER = new AType<ExcHandlerAttr>();
	public static final AType<CatchAttr> CATCH_BLOCK = new AType<CatchAttr>();
	public static final AType<SplitterBlockAttr> SPLITTER_BLOCK = new AType<SplitterBlockAttr>();
	public static final AType<ForceReturnAttr> FORCE_RETURN = new AType<ForceReturnAttr>();
	public static final AType<FieldInitAttr> FIELD_INIT = new AType<FieldInitAttr>();
	public static final AType<FieldReplaceAttr> FIELD_REPLACE = new AType<FieldReplaceAttr>();
	public static final AType<JadxErrorAttr> JADX_ERROR = new AType<JadxErrorAttr>();
	public static final AType<MethodInlineAttr> METHOD_INLINE = new AType<MethodInlineAttr>();
	public static final AType<EnumClassAttr> ENUM_CLASS = new AType<EnumClassAttr>();
	public static final AType<EnumMapAttr> ENUM_MAP = new AType<EnumMapAttr>();
	public static final AType<AnnotationsList> ANNOTATION_LIST = new AType<AnnotationsList>();
	public static final AType<MethodParameters> ANNOTATION_MTH_PARAMETERS = new AType<MethodParameters>();
	public static final AType<PhiListAttr> PHI_LIST = new AType<PhiListAttr>();
	public static final AType<SourceFileAttr> SOURCE_FILE = new AType<SourceFileAttr>();
	public static final AType<DeclareVariablesAttr> DECLARE_VARIABLES = new AType<DeclareVariablesAttr>();
	public static final AType<LoopLabelAttr> LOOP_LABEL = new AType<LoopLabelAttr>();
	public static final AType<IgnoreEdgeAttr> IGNORE_EDGE = new AType<IgnoreEdgeAttr>();
}
