package com.tin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtil {
    public static void main(String[] args) {
        // LocalDate LocalTime LocalDateTime
        // 获取当前时间
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        //LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDate);
        System.out.println(localTime);
        //System.out.println(localDateTime);

        // LocalDateTime  使用频率较高
        // of() 设置指定的年月日时分秒  体现不偏移性
        LocalDateTime dateTime = LocalDateTime.now(); //LocalDateTime.of(2019, 04, 10, 23, 03);
        //System.out.println(dateTime);
        // 2020
        System.out.println(dateTime.getYear());
        // 7
        System.out.println(dateTime.getMonthValue());
        // 17
        System.out.println(dateTime.getDayOfMonth());
        // FRIDAY
        System.out.println(dateTime.getDayOfWeek().getValue());
        // JULY
        System.out.println(dateTime.getMonth());
        System.out.println(dateTime.getHour());
        // 58
        System.out.println(dateTime.getMinute());
        // second
        System.out.println(dateTime.getSecond());

        //FEFEFEFE68350000000100688804353C32396A16
//        String str = "68350000000100688804353C32396A16";
//        System.out.println(str.length());
//        String aa = str.length() > 26 ? str.substring(16,18):"";
//        System.out.println(aa);
    }
}
