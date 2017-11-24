package jadx.api;

import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import jadx.api.ResourceFile.*;
import jadx.core.codegen.*;
import jadx.core.utils.*;
import jadx.core.utils.exceptions.*;
import jadx.core.utils.files.*;
import jadx.core.xmlgen.*;

// TODO: move to core package
public final class ResourcesLoader {
	private static final Logger LOG = LoggerFactory.getLogger(ResourcesLoader.class);

	private static final int READ_BUFFER_SIZE = 8 * 1024;
	private static final int LOAD_SIZE_LIMIT = 10 * 1024 * 1024;

	private final JadxDecompiler jadxRef;

	ResourcesLoader(JadxDecompiler jadxRef) {
		this.jadxRef = jadxRef;
	}

	List<ResourceFile> load(List<InputFile> inputFiles) {
		List<ResourceFile> list = new ArrayList<ResourceFile>(inputFiles.size());
		for (InputFile file : inputFiles) {
			loadFile(list, file.getFile());
		}
		return list;
	}

	public interface ResourceDecoder {
		ResContainer decode(long size, InputStream is) throws IOException;
	}

	public static ResContainer decodeStream(ResourceFile rf, ResourceDecoder decoder) throws JadxException {
		ZipRef zipRef = rf.getZipRef();
		if (zipRef == null) {
			return null;
		}
		ZipFile zipFile = null;
		InputStream inputStream = null;
		ResContainer result = null;
		try {
			zipFile = new ZipFile(zipRef.getZipFile());
			ZipEntry entry = zipFile.getEntry(zipRef.getEntryName());
			if (entry == null) {
				throw new IOException("Zip entry not found: " + zipRef);
			}
			inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
			result = decoder.decode(entry.getSize(), inputStream);
		} catch (Exception e) {
			throw new JadxException("Error decode: " + zipRef.getEntryName(), e);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				LOG.debug("Error close zip file: {}", zipRef, e);
			}
		}
		return result;
	}

	static ResContainer loadContent(final JadxDecompiler jadxRef, final ResourceFile rf) {
		try {
			return decodeStream(rf, (size, is) -> {
                if (size > LOAD_SIZE_LIMIT) {
                    return ResContainer.singleFile(rf.getName(),
                            new CodeWriter().add("File too big, size: "
                                    + String.format("%.2f KB", size / 1024.)));
                }
                return loadContent(jadxRef, rf, is);
            });
		} catch (JadxException e) {
			LOG.error("Decode error", e);
			CodeWriter cw = new CodeWriter();
			cw.add("Error decode ").add(rf.getType().toString().toLowerCase());
			cw.startLine(Utils.getStackTrace(e.getCause()));
			return ResContainer.singleFile(rf.getName(), cw);
		}
	}

	private static ResContainer loadContent(JadxDecompiler jadxRef, ResourceFile rf,
			InputStream inputStream) throws IOException {
		switch (rf.getType()) {
			case MANIFEST:
			case XML:
				return ResContainer.singleFile(rf.getName(),
						jadxRef.getXmlParser().parse(inputStream));

			case ARSC:
				return new ResTableParser().decodeFiles(inputStream);
		}
		return ResContainer.singleFile(rf.getName(), loadToCodeWriter(inputStream));
	}

	private void loadFile(List<ResourceFile> list, File file) {
		if (file == null) {
			return;
		}
		ZipFile zip = null;
		try {
			zip = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				addEntry(list, file, entry);
			}
		} catch (IOException e) {
			LOG.debug("Not a zip file: {}", file.getAbsolutePath());
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (Exception e) {
					LOG.error("Zip file close error: {}", file.getAbsolutePath(), e);
				}
			}
		}
	}

	private void addEntry(List<ResourceFile> list, File zipFile, ZipEntry entry) {
		if (entry.isDirectory()) {
			return;
		}
		String name = entry.getName();
		ResourceType type = ResourceType.getFileType(name);
		ResourceFile rf = new ResourceFile(jadxRef, name, type);
		rf.setZipRef(new ZipRef(zipFile, name));
		list.add(rf);
		// LOG.debug("Add resource entry: {}, size: {}", name, entry.getSize());
	}

	public static CodeWriter loadToCodeWriter(InputStream is) throws IOException {
		CodeWriter cw = new CodeWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(READ_BUFFER_SIZE);
		byte[] buffer = new byte[READ_BUFFER_SIZE];
		int count;
		try {
			while ((count = is.read(buffer)) != -1) {
				baos.write(buffer, 0, count);
			}
		} finally {
			try {
				is.close();
			} catch (Exception ignore) {
			}
		}
		cw.add(baos.toString("UTF-8"));
		return cw;
	}
}
