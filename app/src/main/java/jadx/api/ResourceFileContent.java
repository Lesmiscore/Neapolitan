package jadx.api;

import jadx.core.codegen.*;
import jadx.core.xmlgen.*;

public class ResourceFileContent extends ResourceFile {

	private final CodeWriter content;

	public ResourceFileContent(String name, ResourceType type, CodeWriter content) {
		super(null, name, type);
		this.content = content;
	}

	@Override
	public ResContainer getContent() {
		return ResContainer.singleFile(getName(), content);
	}
}
