package jadx.core.dex.attributes.annotations;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.utils.*;

public class MethodParameters implements IAttribute {

	private final List<AnnotationsList> paramList;

	public MethodParameters(int paramCount) {
		paramList = new ArrayList<AnnotationsList>(paramCount);
	}

	public List<AnnotationsList> getParamList() {
		return paramList;
	}

	@Override
	public AType<MethodParameters> getType() {
		return AType.ANNOTATION_MTH_PARAMETERS;
	}

	@Override
	public String toString() {
		return Utils.listToString(paramList);
	}

}
