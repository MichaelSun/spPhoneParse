package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * Created by michael on 14-1-10.
 */
public class ServerPhone {

    public static final String SERVER_PHONE_KEY = "-f";

    public static final class PhoneInfo {

        public String time;

        public String phone;

        public int netType;

        public String channel;

        public String phoneType;

        @Override
        public String toString() {
            return "PhoneInfo{" +
                       "time='" + time + '\'' +
                       ", phone='" + phone + '\'' +
                       ", netType=" + netType +
                       ", channel='" + channel + '\'' +
                       ", phoneType='" + phoneType + '\'' +
                       '}';
        }
    }

    public LinkedList<PhoneInfo> mData = new LinkedList<PhoneInfo>();

    public String mFileFullPath;

    public ServerPhone(String fileFullPath) {
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
                if (0 != line.length()) {

                    String[] dataSplitor = line.split(",");
                    if (dataSplitor == null) continue;

                    char first = dataSplitor[1].charAt(0);
                    if (first == '+' || (first >= '0' && first <= '9')) {
                        PhoneInfo info = new PhoneInfo();
                        info.time = dataSplitor[0];
                        info.phone = dataSplitor[1];
                        info.netType = Integer.valueOf(dataSplitor[2]);
                        info.channel = dataSplitor[3];
                        info.phoneType = dataSplitor[4];
                        mData.add(info);

//                        System.out.println(info.toString());
                    }
                }
            }
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
