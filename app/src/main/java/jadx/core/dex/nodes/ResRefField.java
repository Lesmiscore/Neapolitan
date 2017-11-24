package jadx.core.dex.nodes;

import com.android.dx.rop.code.*;

import jadx.core.dex.info.*;
import jadx.core.dex.instructions.args.*;

public class ResRefField extends FieldNode {

	public ResRefField(DexNode dex, String str) {
		super(dex.root().getAppResClass(),
				FieldInfo.from(dex, dex.root().getAppResClass().getClassInfo(), str, ArgType.INT),
				AccessFlags.ACC_PUBLIC);
	}
}
