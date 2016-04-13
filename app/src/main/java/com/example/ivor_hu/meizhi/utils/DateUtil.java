package com.example.ivor_hu.meizhi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Ivor on 2016/2/12.
 */
public final class DateUtil {
    public static final String DATE_FORMAT_WHOLE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}\\-\\d{2}\\-\\d{2}");
    private DateUtil() {
    }

    public static final Date parse(String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }

    public static String format(Date date) {
        return format(DATE_FORMAT.format(date));
    }

    public static String format(String date) {
        if (!DATE_PATTERN.matcher(date).matches()) {
            return null;
        }

        String[] strs = date.split("-");
        return strs[0] + "/" + strs[1] + "/" + strs[2];
    }

    public static List<String> generateSequenceDateTillToday(Date start){
        List<String> dates = new ArrayList<>();
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (calendar.getTime().before(today)){
            String date = format(calendar.getTime());
            dates.add(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    public static List<String> generateSequenceDateBefore(Date start, int interval){
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        for (int i = 0; i < interval; i++) {
            String date = format(calendar.getTime());
            dates.add(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        return dates;
    }

}
