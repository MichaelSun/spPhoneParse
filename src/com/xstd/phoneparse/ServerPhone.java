package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

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

    public LinkedList<PhoneInfo> phoneInfos = new LinkedList<PhoneInfo>();

    public HashMap<String, PhoneInfo> phone_InfoMap = new HashMap<String, PhoneInfo>();

    public ArrayList<String> errorLine = new ArrayList<String>();

    public String mFileFullPath;

    public ServerPhone(String fileFullPath) {
        mFileFullPath = fileFullPath;

        String[] paths = mFileFullPath.split(":");
        for (String path : paths) {
            parse(path);
        }

        for (PhoneInfo phoneInfo : phone_InfoMap.values()) {
            phoneInfos.add(phoneInfo);
        }
    }

    public int count() {
        return phoneInfos.size();
    }

    private void parse(String fileFullPath) {
        if (fileFullPath == null) return;

        File src = new File(fileFullPath);
        if (!src.exists()) {
            new IllegalArgumentException("解析文件不存在");
        }

        try {
            FileReader fr = new FileReader(fileFullPath);
            BufferedReader bufferedreader = new BufferedReader(fr);
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                line = line.trim();
                if (0 != line.length()) {

                    String[] dataSplitor = line.split(",");
                    if (dataSplitor == null) continue;

                    if (!isNumeric(dataSplitor[2])) {
//                        System.out.println("Find error NetType : " + dataSplitor[2] + " for phone : " + dataSplitor[1]);
                        errorLine.add(line);
                        continue;
                    }

                    PhoneInfo info = new PhoneInfo();
                    info.time = dataSplitor[0];
                    info.phone = dataSplitor[1];
                    info.netType = Integer.valueOf(dataSplitor[2]);
                    info.channel = dataSplitor[3];
                    info.phoneType = dataSplitor[4];

                    phone_InfoMap.put(info.phone, info);
                }
            }

            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
