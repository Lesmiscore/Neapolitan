package jadx.core.xmlgen;

import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import jadx.core.codegen.*;

public class ResContainer implements Comparable<ResContainer> {

	private final String name;
	@Nullable
	private CodeWriter content;

	private final List<ResContainer> subFiles;

	private ResContainer(String name, @Nullable CodeWriter content, List<ResContainer> subFiles) {
		this.name = name;
		this.content = content;
		this.subFiles = subFiles;
	}

	public static ResContainer singleFile(String name, CodeWriter content) {
		return new ResContainer(name, content, Collections.<ResContainer>emptyList());
	}

	public static ResContainer multiFile(String name) {
		return new ResContainer(name, null, new ArrayList<ResContainer>());
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return name.replace("/", File.separator);
	}

	@Nullable
	public CodeWriter getContent() {
		return content;
	}

	public void setContent(@Nullable CodeWriter content) {
		this.content = content;
	}

	public List<ResContainer> getSubFiles() {
		return subFiles;
	}

	@Override
	public int compareTo(ResContainer o) {
		return name.compareTo(o.name);
	}

	@Override
	public String toString() {
		return "ResContainer{" +
				"name='" + name + "'" +
				", content=" + content +
				", subFiles=" + subFiles +
				"}";
	}
}
