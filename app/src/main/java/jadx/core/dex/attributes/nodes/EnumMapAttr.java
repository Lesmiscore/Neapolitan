package jadx.core.dex.attributes.nodes;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.nodes.*;

public class EnumMapAttr implements IAttribute {

	public static class KeyValueMap {
		private final Map<Object, Object> map = new HashMap<Object, Object>();

		public Object get(Object key) {
			return map.get(key);
		}

		void put(Object key, Object value) {
			map.put(key, value);
		}
	}

	private final Map<FieldNode, KeyValueMap> fieldsMap = new HashMap<FieldNode, KeyValueMap>();

	public KeyValueMap getMap(FieldNode field) {
		return fieldsMap.get(field);
	}

	public void add(FieldNode field, Object key, Object value) {
		KeyValueMap map = getMap(field);
		if (map == null) {
			map = new KeyValueMap();
			fieldsMap.put(field, map);
		}
		map.put(key, value);
	}

	@Override
	public AType<EnumMapAttr> getType() {
		return AType.ENUM_MAP;
	}

	@Override
	public String toString() {
		return "Enum fields map: " + fieldsMap;
	}

}
