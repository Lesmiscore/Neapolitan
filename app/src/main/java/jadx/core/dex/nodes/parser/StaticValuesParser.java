package jadx.core.dex.nodes.parser;

import com.android.dex.Dex.*;
import com.android.dex.*;

import java.util.*;

import jadx.core.dex.nodes.*;
import jadx.core.utils.exceptions.*;

public class StaticValuesParser extends EncValueParser {

	public StaticValuesParser(DexNode dex, Section in) {
		super(dex, in);
	}

	public int processFields(List<FieldNode> fields) throws DecodeException {
		int count = Leb128.readUnsignedLeb128(in);
		for (int i = 0; i < count; i++) {
			Object value = parseValue();
			if (i < fields.size()) {
				fields.get(i).addAttr(FieldInitAttr.constValue(value));
			}
		}
		return count;
	}
}
