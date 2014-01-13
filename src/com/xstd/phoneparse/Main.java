package com.xstd.phoneparse;

import java.io.*;
import java.util.*;

public class Main {

    private static String SRC_FILE = null;

    private static ServerPhone SERVER_PHONE = null;

    private static PhoneMap PHONE_MAP = null;

    private static final class MoneyInfo {

        public String local;

        public int count = 0;

        public HashSet<String> channelSet = new HashSet<String>();
    }

    private static final class CountObject {

        public int count = 0;

        public String str;
    }

    private static HashMap<String, HashMap<String, MoneyInfo>> MONEY_INFO = new HashMap<String, HashMap<String, MoneyInfo>>();
    private static HashMap<String, HashMap<String, Integer>> NO_CHANNEL_HAS_LOCAL_INFO = new HashMap<String, HashMap<String, Integer>>();

    private static HashMap<String, Integer> CHANNEL_SUCCESS_COUNT = new HashMap<String, Integer>();

    private static final String NO_CHANNEL_KEY = "No_Channel";
    private static final String NO_DO_MONEY = "No_Do_Money";

    private static final String NO_LOCAL_FILE_KEY = "-o";

    private static String OUT_PAHT = null;

    private static LinkedList<String> NO_LOCAL_PHONE_NUMBER = new LinkedList<String>();

    private static HashMap<String, String> plugin_log = new HashMap<String, String>();

    private static String LOG_FULL_PATH = null;

    private static int dumpInfoWithTitleForMoney(String title, HashMap<String, MoneyInfo> data) {
        try {

            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
            }

            int count = 0;
            if (data == null || data.size() == 0) {
                System.out.println(new String(title + 0 + new String("个".getBytes("utf-8"))));
                if (LOG_FULL_PATH != null) {
                    writeLog(LOG_FULL_PATH, new String(title + 0 + new String("个".getBytes("utf-8"))));
                }
                return 0;
            }
            LinkedList<MoneyInfo> dumpInfo = new LinkedList<MoneyInfo>();
            for (String local : data.keySet()) {
                MoneyInfo moneyInfo = data.get(local);
                moneyInfo.local = local;
                dumpInfo.add(moneyInfo);
                count += moneyInfo.count;
            }

            Collections.sort(dumpInfo, new Comparator<MoneyInfo>() {
                @Override
                public int compare(MoneyInfo moneyInfo, MoneyInfo moneyInfo2) {
                    if (moneyInfo.count > moneyInfo2.count) {
                        return -1;
                    } else if (moneyInfo.count < moneyInfo2.count) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            System.out.println(new String(title + count + new String("个".getBytes("utf-8"))));
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, new String(title + count + new String("个".getBytes("utf-8"))));
            }
            for (MoneyInfo moneyInfo : dumpInfo) {
                String log = moneyInfo.local + " : " + moneyInfo.count
                                 + new String(" 使用的扣费通道 : ".getBytes("utf-8"))
                                 + moneyInfo.channelSet.toString();
                System.out.println(log);
                if (LOG_FULL_PATH != null) {
                    writeLog(LOG_FULL_PATH, log);
                }
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int dumpInfoWithTitle(String title, HashMap<String, Integer> data) {
        try {

            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
            }

            int count = 0;
            if (data == null || data.size() == 0) {
                System.out.println(new String(title + 0 + new String("个".getBytes("utf-8"))));
                if (LOG_FULL_PATH != null) {
                    writeLog(LOG_FULL_PATH, new String(title + 0 + new String("个".getBytes("utf-8"))));
                }
                return 0;
            }

            LinkedList<CountObject> dumpList = new LinkedList<CountObject>();
            for (String local : data.keySet()) {
                CountObject obj = new CountObject();
                obj.count = data.get(local);
                obj.str = local;
                dumpList.add(obj);
                count += obj.count;
            }

            Collections.sort(dumpList, new Comparator<CountObject>() {
                @Override
                public int compare(CountObject countObject, CountObject countObject2) {
                    if (countObject.count > countObject2.count) {
                        return -1;
                    } else if (countObject.count < countObject2.count) {
                        return 1;
                    }

                    return 0;
                }
            });

            System.out.println(new String(title + count + new String("个".getBytes("utf-8"))));
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, new String(title + count + new String("个".getBytes("utf-8"))));
            }
            for (CountObject obj : dumpList) {
                String log = obj.str + " : " + obj.count;
                System.out.println(log);
                if (LOG_FULL_PATH != null) {
                    writeLog(LOG_FULL_PATH, log);
                }
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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

    public static void main(String[] args) {
        try {
            String no_channel = new String("无通道".getBytes("utf-8"));
            String no_money = new String("不下发".getBytes("utf-8"));
            String cnmnet = new String("【移动】".getBytes("utf-8"));
            String unicom = new String("【联通】".getBytes("utf-8"));
            String dianxin = new String("【电信】".getBytes("utf-8"));

            //debug
            String beijing_local = new String("北京".getBytes("utf-8"));


            //解析参数
            for (int index = 0; index < args.length; index++) {
                System.out.println(args[index]);

                if (args[index].equals(ServerPhone.SERVER_PHONE_KEY)) {
                    SERVER_PHONE = new ServerPhone(args[index + 1]);
                } else if (args[index].equals(PhoneMap.PHONE_MAP_KEY)) {
                    PHONE_MAP = new PhoneMap(args[index + 1]);
                } else if (args[index].equals(NO_LOCAL_FILE_KEY)) {
                    OUT_PAHT = args[index + 1];
                } else if (args[index].equals("--log")) {
                    LOG_FULL_PATH = args[index + 1];
                }
            }

            if (LOG_FULL_PATH != null) {
                File file = new File(LOG_FULL_PATH);
                file.delete();
            }

            //通过手机号没找到地址的数量
            int no_local_map_phone_number = 0;
            //扣费的数量
            int today_do_money = 0;

            if (SERVER_PHONE != null && PHONE_MAP != null) {
                HashMap<String, String> localList = PHONE_MAP.mData;
                LinkedList<ServerPhone.PhoneInfo> fetchList = SERVER_PHONE.phoneInfos;

                for (ServerPhone.PhoneInfo info : fetchList) {
                    String orgPhone = info.phone;
                    String phonePrefix = orgPhone.substring(0, 7);

                    if (localList.containsKey(phonePrefix)) {
                        //电话-地址映射表中有此前缀
                        if (!info.channel.equals(no_channel)
                                && !info.channel.equals(no_money)) {
                            String local = localList.get(phonePrefix);
                            String key = null;
                            switch (info.netType) {
                                case 1:
                                    key = cnmnet;
                                    break;
                                case 2:
                                    key = unicom;
                                    break;
                                case 3:
                                    key = dianxin;
                                    break;
                            }
                            HashMap<String, MoneyInfo> map = MONEY_INFO.get(key);
                            if (map == null) {
                                map = new HashMap<String, MoneyInfo>();
                                MONEY_INFO.put(key, map);
                            }

                            MoneyInfo moneyInfo = null;
                            if (map.containsKey(local)) {
                                moneyInfo = map.get(local);
                            } else {
                                moneyInfo = new MoneyInfo();
                            }
                            moneyInfo.count += 1;
                            moneyInfo.channelSet.add(info.channel);

                            map.put(local, moneyInfo);
//                            if (local.contains(beijing_local)) {
//                                System.out.println("find Beijng : " + moneyInfo.count + " : " + local);
//                            }

                            today_do_money++;
                        } else if (info.channel.equals(no_channel)) {
                            //无通道的，原因是因为没有配置通道
                            String local = localList.get(phonePrefix);
                            HashMap<String, Integer> map = null;
                            String key = null;
                            switch (info.netType) {
                                case 1:
                                    key = cnmnet;
                                    break;
                                case 2:
                                    key = unicom;
                                    break;
                                case 3:
                                    key = dianxin;
                                    break;
                            }
                            map = NO_CHANNEL_HAS_LOCAL_INFO.get(key);
                            if (map == null) {
                                map = new HashMap<String, Integer>();
                                NO_CHANNEL_HAS_LOCAL_INFO.put(key, map);
                            }

                            int count = 0;
                            if (map.containsKey(local)) {
                                count = map.get(local);
                            }
                            count++;
                            map.put(local, count);
                        }
                    } else {
                        //没发现手机映射的
                        no_local_map_phone_number++;
                        NO_LOCAL_PHONE_NUMBER.add(orgPhone);
                    }

                    //对通道的信息进行处理
                    if (info.channel.equals(no_channel)) {
                        if (localList.containsKey(phonePrefix)) {
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

//                        if (info.channel.contains(beijing_local)) {
//                            System.out.println("find Beijng Channel : " + count + " : " + info.channel + " : " + localList.get(phonePrefix) + " : " + phonePrefix);
//                        }
                    }
                }
            }

//            System.out.println(MONEY_INFO.toString());

            if (SERVER_PHONE != null) {
                String log = new String(">>>>> 今日使用手机号码获取通道数量 <<<<< : ".getBytes("utf-8")) + SERVER_PHONE.count();
                System.out.println(log);
                if (LOG_FULL_PATH != null) {
                    writeLog(LOG_FULL_PATH, log);
                }
            }

            System.out.println(new String(">>>>> 今日扣费手机地址 <<<<< : ".getBytes("utf-8")));
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, new String(">>>>> 今日扣费手机地址 <<<<< : ".getBytes("utf-8")));
            }
            int yidongCount = dumpInfoWithTitleForMoney(new String(">>>>> 【移动】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(cnmnet));
            int liantongCount = dumpInfoWithTitleForMoney(new String(">>>>> 【联通】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(unicom));
            int dianxinCount = dumpInfoWithTitleForMoney(new String(">>>>> 【电信】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(dianxin));

            int yidongNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(cnmnet));
            int liantongNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(unicom));
            int dianxinNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(dianxin));

            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
            }
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
            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, new String(">>>>>> 扣费通道信息 <<<<< : ".getBytes("utf-8")) + channelCount + new String("次".getBytes("utf-8")));
            }
            for (Integer count : dumpInfo1.keySet()) {
                System.out.println(dumpInfo1.get(count) + " : " + count);
            }
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(new String("【【汇总】】: ".getBytes("utf-8")));
            System.out.println(new String("今日使用有效手机号码获取通道数量 : ".getBytes("utf-8")) + SERVER_PHONE.count());
            System.out.println(" ");
            System.out.println(new String("今日扣费手机数量【移动】 : ".getBytes("utf-8")) + yidongCount);
            System.out.println(new String("今日扣费手机数量【联通】 : ".getBytes("utf-8")) + liantongCount);
            System.out.println(new String("今日扣费手机数量【电信】 : ".getBytes("utf-8")) + dianxinCount);
            System.out.println(" ");
            System.out.println(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】 : ".getBytes("utf-8")) + yidongNoCount);
            System.out.println(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】 : ".getBytes("utf-8")) + liantongNoCount);
            System.out.println(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】 : ".getBytes("utf-8")) + dianxinNoCount);
            System.out.println(" ");
            System.out.println(new String("没找到通道的手机数量".getBytes("utf-8")) + " : " + noChannelCount);
            System.out.println(new String("通过手机号没找到地址的数量".getBytes("utf-8")) + " : " + no_local_map_phone_number);
            System.out.println(new String("今天不扣费的数量".getBytes("utf-8")) + " : " + donotCount);
            System.out.println(new String("今日扣费数量".getBytes("utf-8")) + " : " + channelCount);

            if (LOG_FULL_PATH != null) {
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, new String("【【汇总】】: ".getBytes("utf-8")));
                writeLog(LOG_FULL_PATH, new String("今日使用有效手机号码获取通道数量 : ".getBytes("utf-8")) + SERVER_PHONE.count());
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, new String("今日扣费手机数量【移动】 : ".getBytes("utf-8")) + yidongCount);
                writeLog(LOG_FULL_PATH, new String("今日扣费手机数量【联通】 : ".getBytes("utf-8")) + liantongCount);
                writeLog(LOG_FULL_PATH, new String("今日扣费手机数量【电信】 : ".getBytes("utf-8")) + dianxinCount);
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】 : ".getBytes("utf-8")) + yidongNoCount);
                writeLog(LOG_FULL_PATH, new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】 : ".getBytes("utf-8")) + liantongNoCount);
                writeLog(LOG_FULL_PATH, new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】 : ".getBytes("utf-8")) + dianxinNoCount);
                writeLog(LOG_FULL_PATH, " ");
                writeLog(LOG_FULL_PATH, new String("没找到通道的手机数量".getBytes("utf-8")) + " : " + noChannelCount);
                writeLog(LOG_FULL_PATH, new String("通过手机号没找到地址的数量".getBytes("utf-8")) + " : " + no_local_map_phone_number);
                writeLog(LOG_FULL_PATH, new String("今天不扣费的数量".getBytes("utf-8")) + " : " + donotCount);
                writeLog(LOG_FULL_PATH, new String("今日扣费数量".getBytes("utf-8")) + " : " + channelCount);
            }

//            for (String key : plugin_log.keySet()) {
//                System.out.println(key + " : " + plugin_log.get(key));
//            }

//            if (OUT_PAHT == null) return;
//            File out = new File(OUT_PAHT);
//            try {
//                FileWriter writer = new FileWriter(out);
//                for (String phone : NO_LOCAL_PHONE_NUMBER) {
//                    writer.write(phone);
//                    writer.write("\n");
//                }
//                writer.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
