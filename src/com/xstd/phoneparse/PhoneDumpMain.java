package com.xstd.phoneparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

/**
 * Created by michael on 14-1-11.
 */
public class PhoneDumpMain {

    private static String INPUT_SRC = null;
    private static String OUPUT = null;

    public static void main_fake(String[] args) {
        PhoneMap PHONE_MAP = null;

        for (int index = 0; index < args.length; index++) {
            System.out.println(args[index]);

            if (args[index].equals("-s")) {
                INPUT_SRC = args[index + 1];
            } else if (args[index].equals("-o")) {
                OUPUT = args[index + 1];
            } else if (args[index].equals(PhoneMap.PHONE_MAP_KEY)) {
                PHONE_MAP = new PhoneMap(args[index + 1]);
            }
        }

        HashMap<String, String> localMap = PHONE_MAP.mData;

        HashMap<String, String> outMap = new HashMap<String, String>();

        try {
            int count = 0;
            FileReader fr = new FileReader(INPUT_SRC);
            BufferedReader bufferedreader = new BufferedReader(fr);
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                line = line.trim();
                if (0 != line.length()) {
                    String[] dataSplitor = line.split(",");
                    if (dataSplitor == null) continue;

                    if (localMap.containsKey(dataSplitor[1])) continue;

                    String local = dataSplitor[2];
                    local.trim();
                    if (local.contains(" ")) {
                        String[] locals = local.split(" ");
                        locals[0].trim();
                        locals[1].trim();
                        if (locals[0].equals(locals[1])) {
                            dataSplitor[2] = locals[0];
                        } else {
                            dataSplitor[2] = locals[0] + "-" + locals[1];
                        }
                    } else {
                        dataSplitor[2]  = local;
                    }
                    System.out.println(dataSplitor[1] + "=" + dataSplitor[2]);
                    outMap.put(dataSplitor[1], dataSplitor[2]);

                    count++;
                }
            }
            System.out.println("Total Count : " + count);
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        if (OUPUT == null) return;
        File out = new File(OUPUT);
        try {
            FileWriter writer = new FileWriter(out);
            for (String phone : outMap.keySet()) {
                writer.write(phone + "=" + outMap.get(phone));
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
