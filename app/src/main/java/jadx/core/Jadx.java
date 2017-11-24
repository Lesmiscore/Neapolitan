package jadx.core;

import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import jadx.api.*;
import jadx.core.dex.visitors.*;
import jadx.core.dex.visitors.blocksmaker.*;
import jadx.core.dex.visitors.regions.*;
import jadx.core.dex.visitors.ssa.*;
import jadx.core.dex.visitors.typeinference.*;

public class Jadx {
	private static final Logger LOG = LoggerFactory.getLogger(Jadx.class);

	static {
		if (Consts.DEBUG) {
			LOG.info("debug enabled");
		}
	}

	public static List<IDexTreeVisitor> getPassesList(IJadxArgs args, File outDir) {
		List<IDexTreeVisitor> passes = new ArrayList<IDexTreeVisitor>();
		if (args.isFallbackMode()) {
			passes.add(new FallbackModeVisitor());
		} else {
			passes.add(new BlockSplitter());
			passes.add(new BlockProcessor());
			passes.add(new BlockExceptionHandler());
			passes.add(new BlockFinallyExtract());
			passes.add(new BlockFinish());

			passes.add(new SSATransform());
			passes.add(new DebugInfoVisitor());
			passes.add(new TypeInference());

			if (args.isRawCFGOutput()) {
				passes.add(DotGraphVisitor.dumpRaw(outDir));
			}

			passes.add(new ConstInlineVisitor());
			passes.add(new FinishTypeInference());
			passes.add(new EliminatePhiNodes());

			passes.add(new ModVisitor());

			passes.add(new CodeShrinker());
			passes.add(new ReSugarCode());

			if (args.isCFGOutput()) {
				passes.add(DotGraphVisitor.dump(outDir));
			}

			passes.add(new RegionMakerVisitor());
			passes.add(new IfRegionVisitor());
			passes.add(new ReturnVisitor());

			passes.add(new CodeShrinker());
			passes.add(new SimplifyVisitor());
			passes.add(new CheckRegions());

			if (args.isCFGOutput()) {
				passes.add(DotGraphVisitor.dumpRegions(outDir));
			}

			passes.add(new MethodInlineVisitor());
			passes.add(new ExtractFieldInit());
			passes.add(new ClassModifier());
			passes.add(new EnumVisitor());
			passes.add(new PrepareForCodeGen());
			passes.add(new LoopRegionVisitor());
			passes.add(new ProcessVariables());

			passes.add(new DependencyCollector());

			passes.add(new RenameVisitor());
		}
		return passes;
	}

	public static String getVersion() {
		try {
			ClassLoader classLoader = Jadx.class.getClassLoader();
			if (classLoader != null) {
				Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
				while (resources.hasMoreElements()) {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					String ver = manifest.getMainAttributes().getValue("jadx-version");
					if (ver != null) {
						return ver;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Can't get manifest file", e);
		}
		return "dev";
	}
}
