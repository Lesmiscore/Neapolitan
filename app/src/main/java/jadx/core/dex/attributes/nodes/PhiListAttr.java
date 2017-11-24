package jadx.core.dex.attributes.nodes;

import java.util.*;

import jadx.core.dex.attributes.*;
import jadx.core.dex.instructions.*;

public class PhiListAttr implements IAttribute {

	private final List<PhiInsn> list = new LinkedList<PhiInsn>();

	@Override
	public AType<PhiListAttr> getType() {
		return AType.PHI_LIST;
	}

	public List<PhiInsn> getList() {
		return list;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PHI: ");
		for (PhiInsn phiInsn : list) {
			sb.append('r').append(phiInsn.getResult().getRegNum()).append(" ");
		}
		return sb.toString();
	}
}
