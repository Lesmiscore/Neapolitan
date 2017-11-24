package jadx.core.utils;

import org.objectweb.asm.*;

import java.io.*;

public class AsmUtils {

	private AsmUtils() {
	}

	public static String getNameFromClassFile(File file) throws IOException {
		String className = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			ClassReader classReader = new ClassReader(in);
			className = classReader.getClassName();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return className;
	}

}
