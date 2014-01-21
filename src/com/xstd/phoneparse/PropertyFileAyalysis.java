package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.TreeMap;

/**
 * Created by michael on 14-1-14.
 */
public class PropertyFileAyalysis {

    public static final String KEY_PROPERTY_FILE = "--property";

    private String mFileFullPath;

    private TreeMap<String, String> mProerties;

    public PropertyFileAyalysis(String fileFullPath) {
        mFileFullPath = fileFullPath;
        mProerties = new TreeMap<String, String>();

        trim(fileFullPath);
    }

    private void trim(String fileFullPath) {
        if (fileFullPath == null) return;

        File src = new File(fileFullPath);
        if (!src.exists()) {
            new IllegalArgumentException("解析文件不存在");
        }

        try {
            FileReader fr = new FileReader(mFileFullPath);
            BufferedReader bufferedreader = new BufferedReader(fr);
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                line = line.trim();
                if (0 != line.length() && line.contains("=")) {
                    String[] dataSplitor = line.split("=");
                    if (dataSplitor == null) continue;

                    mProerties.put(dataSplitor[0].trim(), dataSplitor[1].trim());
                }
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpToFile(String outFullPath) {
        if (outFullPath == null) return;

        File out = new File(outFullPath);
        try {
            FileWriter writer = new FileWriter(out);
            for (String phone : mProerties.keySet()) {
                writer.write(phone + "=" + mProerties.get(phone));
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
