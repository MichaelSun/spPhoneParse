package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by michael on 14-1-10.
 */
public class PhoneMap {

    public static final String PHONE_MAP_KEY = "-m";

    public static final class PhoneLocal {

        public String phonePrefix;

        public String local;

        @Override
        public String toString() {
            return "PhoneLocal{" +
                       "phonePrefix='" + phonePrefix + '\'' +
                       ", local='" + local + '\'' +
                       '}';
        }
    }

    public HashMap<String, String> mData = new HashMap<String, String>();

    public String mFileFullPath;

    public PhoneMap(String fileFullPath) {
        mFileFullPath = fileFullPath;
        parse();
    }

    public int count() {
        return mData.size();
    }

    private void parse() {
        if (mFileFullPath == null) return;

        File src = new File(mFileFullPath);
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
                    mData.put(dataSplitor[0], dataSplitor[1]);
                }
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
