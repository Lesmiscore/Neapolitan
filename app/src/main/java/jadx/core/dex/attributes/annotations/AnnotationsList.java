package jadx.core.dex.attributes.annotations;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.utils.*;

public class AnnotationsList implements IAttribute {

	public static final AnnotationsList EMPTY = new AnnotationsList(Collections.<Annotation>emptyList());

	private final Map<String, Annotation> map;

	public AnnotationsList(List<Annotation> anList) {
		map = new HashMap<String, Annotation>(anList.size());
		for (Annotation a : anList) {
			map.put(a.getAnnotationClass(), a);
		}
	}

	public Annotation get(String className) {
		return map.get(className);
	}

	public Collection<Annotation> getAll() {
		return map.values();
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public AType<AnnotationsList> getType() {
		return AType.ANNOTATION_LIST;
	}

	@Override
	public String toString() {
		return Utils.listToString(map.values());
	}

}
