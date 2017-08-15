package org.sounddrive.sounddrivemobile.model;


import java.util.HashMap;
import java.util.Map;

public class DriveCommand {

    public static final int Stop = 20;
    public static final int One = 21;
    public static final int Two = 22;
    public static final int Three = 23;
    public static final int Four = 24;
    public static final int Five = 25;
    public static final int Six = 26;
    public static final int Seven = 27;
    public static final int Eight = 28;
    public static final int Nine = 29;
    public static final int Ten = 30;
    public static final int Eleven = 31;
    public static final int Twelve = 32;
    public static final int Slow = 46;
    public static final int Quick = 50;
    public static final int TrimUp = 70;
    public static final int TrimRight = 71;
    public static final int TrimDown = 72;
    public static final int TrimLeft = 73;
    public static final int GetData = 80;
    public static final int Quit = 100;

    private static Map<String, Integer> resultMapping = new HashMap<>();


    static {
        resultMapping.put("stop", Stop);
        resultMapping.put("one", One);
        resultMapping.put("1", One);
        resultMapping.put("two", Two);
        resultMapping.put("2", Two);
        resultMapping.put("to", Two);
        resultMapping.put("three", Three);
        resultMapping.put("street", Three);
        resultMapping.put("free", Three);
        resultMapping.put("train", Three);
        resultMapping.put("cree", Three);
        resultMapping.put("3", Three);
        resultMapping.put("fort", Four);
        resultMapping.put("four", Four);
        resultMapping.put("4", Four);
        resultMapping.put("five", Five);
        resultMapping.put("5", Five);
        resultMapping.put("six", Six);
        resultMapping.put("sex", Six);
        resultMapping.put("6", Six);
        resultMapping.put("seven", Seven);
        resultMapping.put("7", Seven);
        resultMapping.put("hate", Eight);
        resultMapping.put("eight", Eight);
        resultMapping.put("8", Eight);
        resultMapping.put("nine", Nine);
        resultMapping.put("9", Nine);
        resultMapping.put("ten", Ten);
        resultMapping.put("10", Ten);
        resultMapping.put("eleven", Eleven);
        resultMapping.put("11", Eleven);
        resultMapping.put("twelve", Twelve);
        resultMapping.put("12", Twelve);
        resultMapping.put("slow", Slow);
        resultMapping.put("quick", Quick);
    }

    public static Integer interpret(String result) {
        if (result == null)
            return null;

        String key = result.trim().toLowerCase();

        if (key == "")
            return null;

        if (!resultMapping.containsKey(result))
            return null;

        return resultMapping.get(result);
    }
}
