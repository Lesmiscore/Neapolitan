package com.njlabs.showjava.processor;

import android.graphics.*;

import com.google.firebase.crash.*;
import com.njlabs.showjava.utils.*;

import net.dongliu.apk.parser.*;

import org.apache.commons.io.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import jadx.api.*;

/**
 * Created by Niranjan on 30-05-2015.
 */
public class ResourcesExtractor extends ProcessServiceHelper {

    private final ApkParser apkParser;

    public ResourcesExtractor(ProcessService processService) {
        this.processService = processService;
        this.UIHandler = processService.UIHandler;
        this.packageFilePath = processService.packageFilePath;
        this.packageName = processService.packageName;
        this.exceptionHandler = processService.exceptionHandler;
        this.apkParser = processService.apkParser;
        this.sourceOutputDir = processService.sourceOutputDir;
        this.javaSourceOutputDir = processService.javaSourceOutputDir;
    }

    public void extract() {
        broadcastStatus("res");
        if(processService.decompilerToUse.equals("jadx")){
            extractResourcesWithJadx();
        } else {
            extractResourcesWithParser();
        }
    }

    private void extractResourcesWithJadx(){
        ThreadGroup group = new ThreadGroup("XML Extraction Group");
        Thread xmlExtractionThread = new Thread(group, () -> {
            try {
                File resDir = new File(sourceOutputDir);

                JadxDecompiler jadx = new JadxDecompiler();
                jadx.setOutputDir(resDir);
                jadx.loadFile(new File(packageFilePath));
                jadx.saveResources();

                ZipFile zipFile = new ZipFile(packageFilePath);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    if (!zipEntry.isDirectory() && (FilenameUtils.getExtension(zipEntry.getName()).equals("png") || FilenameUtils.getExtension(zipEntry.getName()).equals("jpg"))) {
                        broadcastStatus("progress_stream", zipEntry.getName());
                        writeFile(zipFile.getInputStream(zipEntry), zipEntry.getName());
                    }
                }
                zipFile.close();
                saveIcon();
                allDone();

            } catch (Exception | StackOverflowError e) {
                processService.publishProgress("start_activity_with_error");
            }
        }, "XML Extraction Thread", processService.STACK_SIZE);
        xmlExtractionThread.setPriority(Thread.MAX_PRIORITY);
        xmlExtractionThread.setUncaughtExceptionHandler(exceptionHandler);
        xmlExtractionThread.start();
    }

    private void extractResourcesWithParser(){
        ThreadGroup group = new ThreadGroup("XML Extraction Group");
        Thread xmlExtractionThread = new Thread(group, () -> {
            try {
                ZipFile zipFile = new ZipFile(packageFilePath);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    if (!zipEntry.isDirectory() && !zipEntry.getName().equals("AndroidManifest.xml") && FilenameUtils.getExtension(zipEntry.getName()).equals("xml")) {
                        broadcastStatus("progress_stream", zipEntry.getName());
                        writeXML(zipEntry.getName());
                    } else if (!zipEntry.isDirectory() && (FilenameUtils.getExtension(zipEntry.getName()).equals("png") || FilenameUtils.getExtension(zipEntry.getName()).equals("jpg"))) {
                        broadcastStatus("progress_stream", zipEntry.getName());
                        writeFile(zipFile.getInputStream(zipEntry), zipEntry.getName());
                    }
                }
                zipFile.close();
                writeManifest();
                saveIcon();
                allDone();
            } catch (Exception | StackOverflowError e) {
                processService.publishProgress("start_activity_with_error");
            }
        }, "XML Extraction Thread", processService.STACK_SIZE);
        xmlExtractionThread.setPriority(Thread.MAX_PRIORITY);
        xmlExtractionThread.setUncaughtExceptionHandler(exceptionHandler);
        xmlExtractionThread.start();
    }

    private void writeFile(InputStream fileStream, String path) {
        FileOutputStream outputStream = null;
        try {
            String fileFolderPath = sourceOutputDir + "/" + path.replace(FilenameUtils.getName(path), "");
            File fileFolder = new File(fileFolderPath);
            if (!fileFolder.exists() || !fileFolder.isDirectory()) {
                fileFolder.mkdirs();
            }

            outputStream = new FileOutputStream(new File(fileFolderPath + FilenameUtils.getName(path)));

            int read;
            byte[] bytes = new byte[1024];

            while ((read = fileStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            FirebaseCrash.report(e);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }
        }
    }

    private void writeXML(String path) {
        try {
            String xml = apkParser.transBinaryXml(path);
            String fileFolderPath = sourceOutputDir + "/" + path.replace(FilenameUtils.getName(path), "");
            File fileFolder = new File(fileFolderPath);
            if (!fileFolder.exists() || !fileFolder.isDirectory()) {
                fileFolder.mkdirs();
            }
            FileUtils.writeStringToFile(new File(fileFolderPath + FilenameUtils.getName(path)), xml);
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
    }

    private void allDone() {
        SourceInfo.setXmlSourceStatus(processService, true);
        processService.publishProgress("start_activity");
    }

    private void writeManifest() {
        try {
            String manifestXml = apkParser.getManifestXml();
            FileUtils.writeStringToFile(new File(sourceOutputDir + "/AndroidManifest.xml"), manifestXml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIcon() {
        try {
            byte[] icon = apkParser.getIconFile().getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(sourceOutputDir + "/icon.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                FirebaseCrash.report(e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
    }
}
