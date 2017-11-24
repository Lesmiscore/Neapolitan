package jadx.core.xmlgen;

import java.util.*;

import jadx.core.utils.*;
import jadx.core.xmlgen.entry.*;

public class ResourceStorage {

	private static final Comparator<ResourceEntry> COMPARATOR = (a, b) -> Utils.compare(a.getId(), b.getId());

	private final List<ResourceEntry> list = new ArrayList<ResourceEntry>();
	private String appPackage;

	public Collection<ResourceEntry> getResources() {
		return list;
	}

	public void add(ResourceEntry ri) {
		list.add(ri);
	}

	public void finish() {
		Collections.sort(list, COMPARATOR);
	}

	public ResourceEntry getByRef(int refId) {
		ResourceEntry key = new ResourceEntry(refId);
		int index = Collections.binarySearch(list, key, COMPARATOR);
		if (index < 0) {
			return null;
		}
		return list.get(index);
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	public Map<Integer, String> getResourcesNames() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (ResourceEntry entry : list) {
			map.put(entry.getId(), entry.getTypeName() + "/" + entry.getKeyName());
		}
		return map;
	}
}
