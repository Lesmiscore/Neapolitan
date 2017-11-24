package com.njlabs.showjava.processor;

import android.util.*;

import com.google.firebase.crash.*;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.ir.*;
import com.googlecode.dex2jar.reader.*;
import com.googlecode.dex2jar.v3.*;
import com.njlabs.showjava.utils.*;
import com.njlabs.showjava.utils.logging.*;

import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.immutable.*;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

/**
 * Created by Niranjan on 29-05-2015.
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
public class JarExtractor extends ProcessServiceHelper {

    private final ArrayList<String> ignoredLibs;

    public JarExtractor(ProcessService processService) {
        this.processService = processService;
        this.UIHandler = processService.UIHandler;
        this.packageFilePath = processService.packageFilePath;
        this.packageName = processService.packageName;
        this.exceptionHandler = processService.exceptionHandler;
        this.sourceOutputDir = processService.sourceOutputDir;
        this.javaSourceOutputDir = processService.javaSourceOutputDir;
        ignoredLibs = new ArrayList<>();

        //////
        printStream = new PrintStream(new ProgressStream());
        System.setErr(printStream);
        System.setOut(printStream);
        //////
    }

    public void extract() {
        ThreadGroup group = new ThreadGroup("DEX TO JAR EXTRACTION");
        broadcastStatus("optimise_dex_start");
        Runnable runProcess = () -> {
            loadIgnoredLibs();
            apkToDex();
            if(!processService.decompilerToUse.equals("jadx")){
                dexToJar();
            }
            startJavaExtractor();
        };
        Thread extractionThread = new Thread(group, runProcess, "DEX TO JAR EXTRACTION", processService.STACK_SIZE);
        extractionThread.setPriority(Thread.MAX_PRIORITY);
        extractionThread.setUncaughtExceptionHandler(exceptionHandler);
        extractionThread.start();
    }

    private void apkToDex() {
        DexFile dexFile = null;
        try {
            dexFile = DexFileFactory.loadDexFile(packageFilePath, 19);
        } catch (Exception e) {
            broadcastStatus("exit");
            UIHandler.post(new ToastRunnable("The app you selected cannot be decompiled. Please select another app."));
        }
        List<ClassDef> classes = new ArrayList<>();
        broadcastStatus("optimising", "");

        for (ClassDef classDef : dexFile.getClasses()) {
            if (!isIgnored(classDef.getType())) {
                final String CurrentClass = classDef.getType();
                broadcastStatus("optimising_class", CurrentClass.replaceAll("Processing ", ""));
                classes.add(classDef);
            }
        }
        broadcastStatus("optimise_dex_finish");

        File PerAppWorkingDirectory = new File(processService.sourceOutputDir);
        PerAppWorkingDirectory.mkdirs();

        Log.d("DEBUGGER", "Prepare Writing");

        broadcastStatus("merging_classes");

        dexFile = new ImmutableDexFile(classes);

        try {
            Log.d("DEBUGGER", "Start Writing");
            DexFileFactory.writeDexFile(PerAppWorkingDirectory + "/optimised_classes.dex", dexFile);
            Log.d("DEBUGGER", "Writing done!");
        } catch (Exception e) {
            broadcastStatus("exit");
            UIHandler.post(new ToastRunnable("The app you selected cannot be decompiled. Please select another app."));
        }


    }

    private void dexToJar() {
        Log.i("STATUS", "Jar Extraction Started");

        broadcastStatus("dex2jar");

        // DEX 2 JAR CONFIGS
        boolean reuseReg = false; // reuse register while generate java .class file
        boolean topologicalSort1 = false; // same with --topological-sort/-ts
        boolean topologicalSort = false; // sort block by topological, that will generate more readable code
        boolean verbose = true; // show progress
        boolean debugInfo = false; // translate debug info
        boolean printIR = false; // print ir to System.out
        boolean optimizeSynchronized = true; // Optimise-synchronised

        File PerAppWorkingDirectory = new File(sourceOutputDir);
        File file = new File(PerAppWorkingDirectory + "/" + packageName + ".jar");

        File dexFile = new File(PerAppWorkingDirectory + "/optimised_classes.dex");

        if (dexFile.exists() && dexFile.isFile()) {
            DexExceptionHandlerMod dexExceptionHandlerMod = new DexExceptionHandlerMod();
            try {
                DexFileReader reader = new DexFileReader(dexFile);
                Dex2jar dex2jar = Dex2jar.from(reader).reUseReg(reuseReg).topoLogicalSort(topologicalSort || topologicalSort1).skipDebug(!debugInfo)
                        .optimizeSynchronized(optimizeSynchronized).printIR(printIR).verbose(verbose);
                dex2jar.setExceptionHandler(dexExceptionHandlerMod);
                dex2jar.to(file);
            } catch (Exception e) {
                broadcastStatus("exit_process_on_error");
            }

            Log.i("STATUS", "Clearing cache");
            File ClassDex = new File(PerAppWorkingDirectory + "/optimised_classes.dex");
            ClassDex.delete();
        }

    }

    private void startJavaExtractor() {
        JavaExtractor javaExtractor = new JavaExtractor(processService);
        javaExtractor.extract();
    }

    private void loadIgnoredLibs() {
        String ignoredList = (processService.IGNORE_LIBS ? "ignored.list":"ignored_basic.list");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(processService.getAssets().open(ignoredList)));
            String mLine = reader.readLine().trim();
            while (mLine != null) {
                mLine = mLine.trim();
                if (mLine.length() != 0) {
                    ignoredLibs.add(StringUtils.toClassName(mLine));
                }
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            FirebaseCrash.report(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }
        }
    }

    private boolean isIgnored(String className) {
        for (String ignoredClass : ignoredLibs) {
            if (className.startsWith(ignoredClass)) {
                return true;
            }
        }
        return false;
    }

    private class DexExceptionHandlerMod implements DexExceptionHandler {
        @Override
        public void handleFileException(Exception e) {
            Ln.d("Dex2Jar Exception " + e);
        }

        @Override
        public void handleMethodTranslateException(Method method, IrMethod irMethod, MethodNode methodNode, Exception e) {
            Ln.d("Dex2Jar Exception " + e);
        }
    }


}
