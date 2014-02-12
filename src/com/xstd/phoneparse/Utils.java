package com.xstd.phoneparse;

import com.xstd.mail.Mail;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by michael on 14-1-15.
 */
public class Utils {

    private static final String DEBUG_DATE_FORMAT = "yyyy-MM-dd";

    public static String formatTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEBUG_DATE_FORMAT);
        return dateFormat.format(time);
    }

    public static void sendMail(String attachPath) {
        File attchFile = new File(attachPath);
        String[] filenameList = attchFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if (s.startsWith(".")) {
                    return false;
                }

                return true;
            }
        });

        long curTime = System.currentTimeMillis();
        long yestoday = curTime - ((long) 24) * 60 * 60 * 1000;
        String time = formatTime(yestoday);

        ArrayList<String> toList = new ArrayList<String>();
        toList.add("buaalx@qq.com");
        toList.add("duheng1225@163.com");
        toList.add("michael.s.china@gmail.com");

        Mail mail = new Mail();
        for (String filename : filenameList) {
            mail.addAttachfile(attachPath + "/" + filename);
        }
        mail.send();
    }

    public static int dumpNoChannelFroServerPhone(String title, HashMap<String, Main.LocalPhoneInfo> map, String logfile) {
        try {
            if (map == null) return 0;
            int count = 0;
            for (Main.LocalPhoneInfo info : map.values()) {
                count += info.count;
            }
            Utils.log(new String("<<<<< 用手机号没找到通道的列表数量 : ".getBytes("utf-8")) + count
                          + new String("条 >>>>> ".getBytes("utf-8")) + title, logfile);
            for (String local : map.keySet()) {
                Main.LocalPhoneInfo info = map.get(local);
                Utils.log("---------- " + local + title + " ---------- [" + info.count + new String("条]".getBytes("utf-8")), logfile);
                for (ServerPhone.PhoneInfo phoneInfo : info.phoneList) {
                    Utils.log(phoneInfo.time + " : " + phoneInfo.phone + " : " + phoneInfo.netType + " : " + phoneInfo.phoneType, logfile);
                }
            }
            Utils.log(" ", logfile);
            Utils.log(" ", logfile);

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void dumpErrorLineForServerPhone(ArrayList<String> error, String logfile) {
        try {
            int count = error != null ? error.size() : 0;
            Utils.log(new String("<<<<< 用手机号获取通道列表中发现的错误信息数量 : ".getBytes("utf-8")) + count
                          + new String("条 >>>>>".getBytes("utf-8")), logfile);
            if (error != null) {
                for (String line : error) {
                    Utils.log(line, logfile);
                }
            }
            Utils.log(" ", logfile);
            Utils.log(" ", logfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String content, String logfile) {
        System.out.println(content);
        if (logfile != null) {
            writeLog(logfile, content);
        }
    }

    public static void writeLog(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(conent);
            out.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
