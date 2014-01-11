package com.xstd.phoneparse;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class Main {

    private static String SRC_FILE = null;

    private static ServerPhone SERVER_PHONE = null;

    private static PhoneMap PHONE_MAP = null;

    private static HashMap<String, Integer> LOCAL_COUNT = new HashMap<String, Integer>();

    private static HashMap<String, Integer> CHANNEL_SUCCESS_COUNT = new HashMap<String, Integer>();

    private static HashMap<String, Integer> NO_CHANNEL_LOCAL_COUNT_YIDONG = new HashMap<String, Integer>();
    private static HashMap<String, Integer> NO_CHANNEL_LOCAL_COUNT_LIANTONG = new HashMap<String, Integer>();
    private static HashMap<String, Integer> NO_CHANNEL_LOCAL_COUNT_DIANXIN = new HashMap<String, Integer>();

    private static final String NO_CHANNEL_KEY = "No_Channel";
    private static final String NO_DO_MONEY = "No_Do_Money";

    private static final String NO_LOCAL_FILE_KEY = "-o";

    private static String OUT_PAHT = null;

    private static LinkedList<String> NO_LOCAL_PHONE_NUMBER = new LinkedList<String>();

    private static int dumpInfoWithTitle(String title, HashMap<String, Integer> data) {
        try {

            System.out.println(";");
            System.out.println(";");
            System.out.println(";");
            System.out.println(";");
            int count = 0;
            TreeMap<Integer, String> dumpInfo = new TreeMap<Integer, String>();
            for (String local : data.keySet()) {
                dumpInfo.put(data.get(local), local);
                count += data.get(local);
            }
            System.out.println(new String(title + count + new String("个".getBytes("utf-8"))));
            for (Integer c : dumpInfo.keySet()) {
                System.out.println(dumpInfo.get(c) + " : " + c);
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) {
        String no_channel = null;
        String no_money = null;
        try {
            no_channel = new String("无通道".getBytes("utf-8"));
            no_money = new String("不下发".getBytes("utf-8"));


            for (int index = 0; index < args.length; index++) {
                System.out.println(args[index]);

                if (args[index].equals(ServerPhone.SERVER_PHONE_KEY)) {
                    SERVER_PHONE = new ServerPhone(args[index + 1]);
                } else if (args[index].equals(PhoneMap.PHONE_MAP_KEY)) {
                    PHONE_MAP = new PhoneMap(args[index + 1]);
                } else if (args[index].equals(NO_LOCAL_FILE_KEY)) {
                    OUT_PAHT = args[index + 1];
                }
            }

            int no_local_map_phone_number = 0;

            if (SERVER_PHONE != null && PHONE_MAP != null) {
                HashMap<String, String> localList = PHONE_MAP.mData;
                LinkedList<ServerPhone.PhoneInfo> fetchList = SERVER_PHONE.mData;

                for (ServerPhone.PhoneInfo info : fetchList) {
                    String phone = info.phone;
                    if (phone.startsWith("+") && phone.length() == 14) {
                        phone = phone.substring(3);
                    } else if (phone.length() > 11) {
                        phone = phone.substring(phone.length() - 11);
                    }

                    String orgPhone = phone;
                    phone = phone.substring(0, 7);

                    if (localList.containsKey(phone)) {
                        if (!info.channel.equals(no_channel)
                                && !info.channel.equals(no_money)) {
                            String local = localList.get(phone);
                            int count = 0;
                            if (LOCAL_COUNT.containsKey(local)) {
                                count = LOCAL_COUNT.get(local);
                            }
                            count++;

                            LOCAL_COUNT.put(local, count);
                        } else if (info.channel.equals(no_channel)) {
                            if (info.netType == 1) {
                                //移动
                                String local = localList.get(phone);
                                int count = 0;
                                if (NO_CHANNEL_LOCAL_COUNT_YIDONG.containsKey(local)) {
                                    count = NO_CHANNEL_LOCAL_COUNT_YIDONG.get(local);
                                }
                                count++;

                                NO_CHANNEL_LOCAL_COUNT_YIDONG.put(local, count);
                            } else if (info.netType == 2) {
                                //联通
                                String local = localList.get(phone);
                                int count = 0;
                                if (NO_CHANNEL_LOCAL_COUNT_LIANTONG.containsKey(local)) {
                                    count = NO_CHANNEL_LOCAL_COUNT_LIANTONG.get(local);
                                }
                                count++;

                                NO_CHANNEL_LOCAL_COUNT_LIANTONG.put(local, count);
                            } else if (info.netType == 3) {
                                //电信
                                String local = localList.get(phone);
                                int count = 0;
                                if (NO_CHANNEL_LOCAL_COUNT_DIANXIN.containsKey(local)) {
                                    count = NO_CHANNEL_LOCAL_COUNT_DIANXIN.get(local);
                                }
                                count++;

                                NO_CHANNEL_LOCAL_COUNT_DIANXIN.put(local, count);
                            }
                        }
                    } else {
                        no_local_map_phone_number++;
                        NO_LOCAL_PHONE_NUMBER.add(orgPhone);
                    }

                    if (info.channel.equals(no_channel)) {
                        if (localList.containsKey(phone)) {
                            //没有配置通道
                            int count = 0;
                            if (CHANNEL_SUCCESS_COUNT.containsKey(NO_CHANNEL_KEY)) {
                                count = CHANNEL_SUCCESS_COUNT.get(NO_CHANNEL_KEY);
                            }
                            count++;
                            CHANNEL_SUCCESS_COUNT.put(NO_CHANNEL_KEY, count);
                        }
                    } else if (info.channel.equals(no_money)) {
                        //今日不扣费
                        int count = 0;
                        if (CHANNEL_SUCCESS_COUNT.containsKey(NO_DO_MONEY)) {
                            count = CHANNEL_SUCCESS_COUNT.get(NO_DO_MONEY);
                        }
                        count++;
                        CHANNEL_SUCCESS_COUNT.put(NO_DO_MONEY, count);
                    } else {
                        //扣费通道信息
                        int count = 0;
                        if (CHANNEL_SUCCESS_COUNT.containsKey(info.channel)) {
                            count = CHANNEL_SUCCESS_COUNT.get(info.channel);
                        }
                        count++;
                        CHANNEL_SUCCESS_COUNT.put(info.channel, count);
                    }
                }
            }

            if (SERVER_PHONE != null) {
                System.out.println("Device Count has get the Channel : " + SERVER_PHONE.count());
            }

            dumpInfoWithTitle(new String(">>>>> 今日扣费手机地址 <<<<< : ".getBytes("utf-8")), LOCAL_COUNT);

            dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_LOCAL_COUNT_YIDONG);
            dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_LOCAL_COUNT_LIANTONG);
            dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_LOCAL_COUNT_DIANXIN);

            System.out.println(";");
            System.out.println(";");
            System.out.println(";");
            System.out.println(";");
            int noChannelCount = CHANNEL_SUCCESS_COUNT.get(NO_CHANNEL_KEY) != null ? CHANNEL_SUCCESS_COUNT.get(NO_CHANNEL_KEY) : 0;
            int donotCount = CHANNEL_SUCCESS_COUNT.get(NO_DO_MONEY) != null ? CHANNEL_SUCCESS_COUNT.get(NO_DO_MONEY) : 0;
            CHANNEL_SUCCESS_COUNT.remove(NO_CHANNEL_KEY);
            CHANNEL_SUCCESS_COUNT.remove(NO_DO_MONEY);
            TreeMap<Integer, String> dumpInfo1 = new TreeMap<Integer, String>();
            int channelCount = 0;
            for (String local : CHANNEL_SUCCESS_COUNT.keySet()) {
                dumpInfo1.put(CHANNEL_SUCCESS_COUNT.get(local), local);
                channelCount += CHANNEL_SUCCESS_COUNT.get(local);
            }

            System.out.println(new String(">>>>>> 扣费通道信息 <<<<< : ".getBytes("utf-8")) + channelCount + new String("次".getBytes("utf-8")));
            for (Integer count : dumpInfo1.keySet()) {
                System.out.println(dumpInfo1.get(count) + " : " + count);
            }
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(new String("没找到通道的手机数量".getBytes("utf-8")) + " : " + noChannelCount);
            System.out.println(new String("通过手机号没找到地址的数量".getBytes("utf-8")) + " : " + no_local_map_phone_number);
            System.out.println(new String("今天不扣费的数量".getBytes("utf-8")) + " : " + donotCount);
            System.out.println(new String("今日扣费数量".getBytes("utf-8")) + " : " + channelCount);

            if (OUT_PAHT == null) return;
            File out = new File(OUT_PAHT);
            try {
                FileWriter writer = new FileWriter(out);
                for (String phone : NO_LOCAL_PHONE_NUMBER) {
                    writer.write(phone);
                    writer.write("\n");
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
