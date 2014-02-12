package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by michael on 14-1-10.
 */
public class PhoneMap {

    public static final String PHONE_MAP_KEY = "-m";

    public static final String PHOME_MAP_DUMP_KEY = "--dump";

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

    public HashMap<String, HashSet<String>> mLocalMap = new HashMap<String, HashSet<String>>();

    public String mFileFullPath;

    public PhoneMap(String fileFullPath) {
        mFileFullPath = fileFullPath;
        parse();
    }

    public int count() {
        return mData.size();
    }

    public void dumpCityLocalMap(String dumpFileFullPath) {
        for (String p : mLocalMap.keySet()) {
            System.out.println("[[" + p + "]]");
            HashSet<String> local = mLocalMap.get(p);
            for (String localStr : local) {
                System.out.println(localStr);
            }
            System.out.println(" ");
            System.out.println(" ");
        }
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

                    if (dataSplitor[1].contains("-")) {
                        String[] citySplited = dataSplitor[1].split("-");
                        HashSet<String> local = mLocalMap.get(citySplited[0]);
                        if (local == null) {
                            local = new HashSet<String>();
                            mLocalMap.put(citySplited[0], local);
                        }
                        if (citySplited.length > 1) {
                            local.add(citySplited[1]);
                        }
                    }
                }
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
