package com.xstd.phoneparse;

import com.xstd.chart.BarChartUtils;
import com.xstd.chart.PieChartUtils;

import java.io.File;
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

    public static final class LocalPhoneInfo {

        public String local;

        public int count;

        public LinkedList<ServerPhone.PhoneInfo> phoneList = new LinkedList<ServerPhone.PhoneInfo>();
    }

    private static HashMap<String, HashMap<String, MoneyInfo>> MONEY_INFO = new HashMap<String, HashMap<String, MoneyInfo>>();
    private static HashMap<String, HashMap<String, LocalPhoneInfo>> NO_CHANNEL_HAS_LOCAL_INFO = new HashMap<String, HashMap<String, LocalPhoneInfo>>();

    private static HashMap<String, Integer> CHANNEL_SUCCESS_COUNT = new HashMap<String, Integer>();

    private static final String NO_CHANNEL_KEY = "No_Channel";
    private static final String NO_DO_MONEY = "No_Do_Money";

    private static final String NO_LOCAL_FILE_KEY = "-o";

    private static String OUT_PAHT = null;

    private static LinkedList<String> NO_LOCAL_PHONE_NUMBER = new LinkedList<String>();

    private static HashMap<String, String> plugin_log = new HashMap<String, String>();

    private static String LOG_DIR_PATH = null;

    private static String LOG_REPORT_FULL_PATH = null;

    private static String LOG_ERROR_FULL_PATH = null;

    private static String PROPERTY_FILE = null;

    private static boolean IF_MAIL = false;

    private static int dumpInfoWithTitleForMoney(String title, HashMap<String, MoneyInfo> data) {
        try {
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);

            int count = 0;
            if (data == null || data.size() == 0) {
                Utils.log(new String(title + 0 + new String("个".getBytes("utf-8"))), LOG_REPORT_FULL_PATH);
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

            Utils.log(new String(title + count + new String("个".getBytes("utf-8"))), LOG_REPORT_FULL_PATH);
            for (MoneyInfo moneyInfo : dumpInfo) {
                String log = moneyInfo.local + " : " + moneyInfo.count
                                 + new String(" 使用的扣费通道 : ".getBytes("utf-8"))
                                 + moneyInfo.channelSet.toString();

                Utils.log(log, LOG_REPORT_FULL_PATH);
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int dumpInfoWithTitle(String title, HashMap<String, LocalPhoneInfo> data) {
        try {
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);

            int count = 0;
            if (data == null || data.size() == 0) {
                Utils.log(new String(title + 0 + new String("个".getBytes("utf-8"))), LOG_REPORT_FULL_PATH);
                return 0;
            }

            LinkedList<CountObject> dumpList = new LinkedList<CountObject>();
            for (String local : data.keySet()) {
                CountObject obj = new CountObject();
                obj.count = data.get(local).count;
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

            Utils.log(new String(title + count + new String("个".getBytes("utf-8"))), LOG_REPORT_FULL_PATH);
            for (CountObject obj : dumpList) {
                String log = obj.str + " : " + obj.count;
                Utils.log(log, LOG_REPORT_FULL_PATH);
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) {
        try {
            String no_channel = new String("无通道".getBytes("utf-8"));
            String no_money = new String("不下发".getBytes("utf-8"));
            String cnmnet = new String("【移动】".getBytes("utf-8"));
            String unicom = new String("【联通】".getBytes("utf-8"));
            String dianxin = new String("【电信】".getBytes("utf-8"));

            String cnmnetColum = new String("移动".getBytes("utf-8"));
            String unicomColum = new String("联通".getBytes("utf-8"));
            String dianxinColum = new String("电信".getBytes("utf-8"));

            String day = Utils.formatTime(System.currentTimeMillis());

            //debug
            String beijing_local = new String("北京".getBytes("utf-8"));

            boolean justDumpLocalMap = false;

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
                    LOG_DIR_PATH = args[index + 1];
                    File file = new File(LOG_DIR_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    LOG_REPORT_FULL_PATH = LOG_DIR_PATH + "/log_" + day + ".txt";
                    LOG_ERROR_FULL_PATH = LOG_DIR_PATH + "/error_" + day + ".txt";
                } else if (args[index].equals(PropertyFileAyalysis.KEY_PROPERTY_FILE)) {
                    PROPERTY_FILE = args[index + 1];
                } else if (args[index].equals("--mail")) {
                    IF_MAIL = true;
                } else if (args[index].equals(PHONE_MAP.PHOME_MAP_DUMP_KEY)) {
                    justDumpLocalMap = true;
                }
            }

            //如果是处理property的话，直接处理property，然后返回
            if (PROPERTY_FILE != null) {
                PropertyFileAyalysis ayalysis = new PropertyFileAyalysis(PROPERTY_FILE);
                if (OUT_PAHT != null) {
                    ayalysis.dumpToFile(OUT_PAHT);
                }

                System.out.println(new String("分析文件 : ".getBytes("utf-8")) + PROPERTY_FILE + " dump to : " + OUT_PAHT);
                return;
            }

            //只做local分析
            if (justDumpLocalMap) {
                PHONE_MAP.dumpCityLocalMap(null);
                return;
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
                            today_do_money++;
                        } else if (info.channel.equals(no_channel)) {
                            //无通道的，原因是因为没有配置通道
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
                            HashMap<String, LocalPhoneInfo> map = NO_CHANNEL_HAS_LOCAL_INFO.get(key);
                            if (map == null) {
                                map = new HashMap<String, LocalPhoneInfo>();
                                NO_CHANNEL_HAS_LOCAL_INFO.put(key, map);
                            }

                            LocalPhoneInfo localPhoneInfo = null;
                            if (map.containsKey(local)) {
                                localPhoneInfo = map.get(local);
                            } else {
                                localPhoneInfo = new LocalPhoneInfo();
                                localPhoneInfo.local = local;
                            }
                            localPhoneInfo.count++;
                            localPhoneInfo.phoneList.add(info);
                            map.put(local, localPhoneInfo);
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
                    }
                }
            }


            if (LOG_REPORT_FULL_PATH != null) {
                File file = new File(LOG_REPORT_FULL_PATH);
                file.delete();
            }
            if (LOG_ERROR_FULL_PATH != null) {
                File file = new File(LOG_ERROR_FULL_PATH);
                file.delete();
            }

            Utils.log("[[ " + day + new String(" 数据报表 ]]".getBytes("utf-8")), LOG_REPORT_FULL_PATH);
            if (SERVER_PHONE != null) {
                Utils.log(new String(">>>>> 今日使用手机号码获取通道数量 <<<<< : ".getBytes("utf-8")) + SERVER_PHONE.count(), LOG_REPORT_FULL_PATH);
                Utils.log(" ", LOG_REPORT_FULL_PATH);
                Utils.log(" ", LOG_REPORT_FULL_PATH);
            }

            Utils.log(new String(">>>>> 今日扣费手机地址 <<<<< : ".getBytes("utf-8")), LOG_REPORT_FULL_PATH);
            int yidongCount = dumpInfoWithTitleForMoney(new String(">>>>> 【移动】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(cnmnet));
            int liantongCount = dumpInfoWithTitleForMoney(new String(">>>>> 【联通】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(unicom));
            int dianxinCount = dumpInfoWithTitleForMoney(new String(">>>>> 【电信】 <<<<< : ".getBytes("utf-8")), MONEY_INFO.get(dianxin));

            int yidongNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(cnmnet));
            int liantongNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(unicom));
            int dianxinNoCount = dumpInfoWithTitle(new String(">>>>> 今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】<<<<< : ".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(dianxin));

            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
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

            Utils.log(new String(">>>>>> 扣费通道信息 <<<<< : ".getBytes("utf-8")) + channelCount + new String("次".getBytes("utf-8")), LOG_REPORT_FULL_PATH);
            for (Integer count : dumpInfo1.keySet()) {
                Utils.log(dumpInfo1.get(count) + " : " + count, LOG_REPORT_FULL_PATH);
            }
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(new String("【【汇总】】: ".getBytes("utf-8")), LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日使用有效手机号码获取通道数量 : ".getBytes("utf-8")) + SERVER_PHONE.count(), LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日扣费手机数量【移动】 : ".getBytes("utf-8")) + yidongCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日扣费手机数量【联通】 : ".getBytes("utf-8")) + liantongCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日扣费手机数量【电信】 : ".getBytes("utf-8")) + dianxinCount, LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【移动】 : ".getBytes("utf-8")) + yidongNoCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【联通】 : ".getBytes("utf-8")) + liantongNoCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日没找到通道的手机(能通过手机号找到地址的手机) 【电信】 : ".getBytes("utf-8")) + dianxinNoCount, LOG_REPORT_FULL_PATH);
            Utils.log(" ", LOG_REPORT_FULL_PATH);
            Utils.log(new String("没找到通道的手机数量".getBytes("utf-8")) + " : " + noChannelCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("通过手机号没找到地址的数量".getBytes("utf-8")) + " : " + no_local_map_phone_number, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今天不扣费的数量".getBytes("utf-8")) + " : " + donotCount, LOG_REPORT_FULL_PATH);
            Utils.log(new String("今日扣费数量".getBytes("utf-8")) + " : " + channelCount, LOG_REPORT_FULL_PATH);


            //dump错误信息
            Utils.log("[[ " + day + new String(" 错误数据报表 ]]".getBytes("utf-8")), LOG_ERROR_FULL_PATH);
            Utils.log(" ", LOG_ERROR_FULL_PATH);
            Utils.log(" ", LOG_ERROR_FULL_PATH);
            Utils.dumpErrorLineForServerPhone(SERVER_PHONE.errorLine, LOG_ERROR_FULL_PATH);
            Utils.dumpNoChannelFroServerPhone(new String("【移动】".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(cnmnet), LOG_ERROR_FULL_PATH);
            Utils.dumpNoChannelFroServerPhone(new String("【联通】".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(unicom), LOG_ERROR_FULL_PATH);
            Utils.dumpNoChannelFroServerPhone(new String("【电信】".getBytes("utf-8")), NO_CHANNEL_HAS_LOCAL_INFO.get(dianxin), LOG_ERROR_FULL_PATH);

            //制作扣费通道图表
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
            list.addAll(CHANNEL_SUCCESS_COUNT.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> stringIntegerEntry, Map.Entry<String, Integer> stringIntegerEntry2) {
                    return stringIntegerEntry2.getValue() - stringIntegerEntry.getValue();
                }
            });

            HashMap<String, Integer> data = new HashMap<String, Integer>();
            int channelSuccessCount = 0;
            for (Map.Entry<String, Integer> obj : list) {
                data.put(obj.getKey(), obj.getValue());
                channelSuccessCount += obj.getValue();
            }
            PieChartUtils.buildChar(LOG_DIR_PATH + new String("/扣费通道信息.png".getBytes("utf-8"))
                                       , new String("扣费通道信息 (共".getBytes("utf-8")) + channelSuccessCount + ")"
                                       , data);
            //制作扣费地图图表
            HashMap<String, HashMap<String, Integer>> chartData = new HashMap<String, HashMap<String, Integer>>();
            HashMap<String, MoneyInfo> yidongLocalInfos = MONEY_INFO.get(cnmnet);
            if (yidongLocalInfos != null) {
                for (String local : yidongLocalInfos.keySet()) {
                    HashMap<String, Integer> charDataValue = chartData.get(local);
                    if (charDataValue == null) {
                        charDataValue = new HashMap<String, Integer>();
                        chartData.put(local, charDataValue);
                    }
                    MoneyInfo monkeyInfo = yidongLocalInfos.get(local);
                    charDataValue.put(cnmnetColum, monkeyInfo.count);
                }
            }
            HashMap<String, MoneyInfo> liantongLocalInfos = MONEY_INFO.get(unicom);
            if (liantongLocalInfos != null) {
                for (String local : liantongLocalInfos.keySet()) {
                    HashMap<String, Integer> charDataValue = chartData.get(local);
                    if (charDataValue == null) {
                        charDataValue = new HashMap<String, Integer>();
                        chartData.put(local, charDataValue);
                    }
                    MoneyInfo monkeyInfo = liantongLocalInfos.get(local);
                    charDataValue.put(unicomColum, monkeyInfo.count);
                }
            }
            HashMap<String, MoneyInfo> dianxinLocalInfos = MONEY_INFO.get(dianxin);
            if (dianxinLocalInfos != null) {
                for (String local : dianxinLocalInfos.keySet()) {
                    HashMap<String, Integer> charDataValue = chartData.get(local);
                    if (charDataValue == null) {
                        charDataValue = new HashMap<String, Integer>();
                        chartData.put(local, charDataValue);
                    }
                    MoneyInfo monkeyInfo = dianxinLocalInfos.get(local);
                    charDataValue.put(dianxinColum, monkeyInfo.count);
                }
            }
            //covert to list
            List<BarChartUtils.BarChartObject> chartDataList = new LinkedList<BarChartUtils.BarChartObject>();
            for (String local : chartData.keySet()) {
                for (String op : chartData.get(local).keySet()) {
                    BarChartUtils.BarChartObject barChartObject = new BarChartUtils.BarChartObject();
                    barChartObject.colum = local;
                    barChartObject.row = op;
                    barChartObject.value = chartData.get(local).get(op);
                    chartDataList.add(barChartObject);
                }
            }
            Collections.sort(chartDataList, new Comparator<BarChartUtils.BarChartObject>() {
                @Override
                public int compare(BarChartUtils.BarChartObject barChartObject, BarChartUtils.BarChartObject barChartObject2) {
                    if (barChartObject.value > barChartObject2.value) {
                        return -1;
                    } else if (barChartObject.value < barChartObject2.value) {
                        return 1;
                    }

                    return 0;
                }
            });

            BarChartUtils.buildChar(LOG_DIR_PATH + new String("/扣费通道地址.png".getBytes("utf-8")),
                                       new String("今日扣费手机地址".getBytes("utf-8")),
                                       new String("地址".getBytes("utf-8")),
                                       new String("数量".getBytes("utf-8")),
                                       chartDataList);

            //test mail
            if (IF_MAIL) {
                Utils.sendMail(LOG_DIR_PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
