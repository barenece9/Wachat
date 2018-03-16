package com.wachat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    public static String getDifferenceFormattedTime(String commentTime,
                                                    Context mContext) {
        long milliSeconds = 0;
        try {
            milliSeconds = Long.parseLong(commentTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        long currentTime = System.currentTimeMillis();

        long diff = currentTime - milliSeconds;

        if (diff > (1000 * 60 * 60 * 24)) {
            return getDateFromMilliSecond(commentTime, "dd MMMM", false);
        } else {
            return getDateFromMilliSecond(commentTime, "HH:mm", false);
        }
    }

    public static long getTimeDifference(String lastProfileUpdateTimeString) {

        long lastUpdateTimeInMiliSec = 0;

        try {
            lastUpdateTimeInMiliSec = Long
                    .parseLong(lastProfileUpdateTimeString);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            return (System.currentTimeMillis() - lastUpdateTimeInMiliSec)
                    / (1000 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 60 * 24;

    }

    public static String formatDistance(String distanceInMeter) {
        long distance = 0;
        try {
            distance = Integer.parseInt(distanceInMeter);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // long distanceCall = (long) Math.round(distance>100);

        // return calculateBoundariesAndGetMilePosts(distance);

        return distance >= 1000 ? String.valueOf(distance / 1000) + "km"
                : String.valueOf(distance) + "m";
    }

    private static String calculateBoundariesAndGetMilePosts(long distance) {

        if (distance >= 1000) {
            return String.valueOf(distance / 10000) + "km";
        } else {
            if (distance >= 1000) {
                return String.valueOf(distance / 1000) + "km";
            } else {
                if (distance >= 100) {
                    if (distance >= 500) {
                        return "500m";
                    } else {
                        return "100m";
                    }
                } else {
                    return "20m";
                }
            }
        }

    }

    public static Date getLinkedinExpiryDate(String dateString) {
        Date expirationDate = null;
        String format = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            expirationDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e1) {
        }
        return expirationDate == null ? new Date() : expirationDate;
    }

    public static String getCurrentTimeStampInMilliSecond() {
        try {
            return String.valueOf(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * Return date in specified format.
     *
     * @param
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDateFromMilliSecond(String timestamp,
                                                String dateFormat) {
        dateFormat = dateFormat.replace("h:mm a", "HH:mm");
        return getDateFromMilliSecond(timestamp, dateFormat, false);

    }

    public static String getDateFromMilliSecond(String timestamp,
                                                String dateFormat, boolean isUTC) {

        // Create a DateFormatter object for displaying date in specified
        // format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Date date = null;
        try {
            date = formatter.parse(timestamp);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return timestamp;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
//		if (isUTC)
//			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//		else
//			formatter.setTimeZone(TimeZone.getDefault());
//		// Create a calendar object that will convert the date and time value in
//		// milliseconds to date.
////		Calendar calendar = Calendar.getInstance();
////		calendar.setTimeInMillis(milliSeconds);
//		return new SimpleDateFormat(dateFormat).format(date);

    }


    public static String getUTCDateFromMilliSecond(String timestamp,
                                                   String dateFormat) {

        // Create a DateFormatter object for displaying date in specified
        // format.
//		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//		
//		Date date = null;
//		try {
//			date = formatter.parse(timestamp);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//			return timestamp;
//		}

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(Long.parseLong(timestamp)));
//		if (isUTC)
//			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//		else
//			formatter.setTimeZone(TimeZone.getDefault());
//		// Create a calendar object that will convert the date and time value in
//		// milliseconds to date.
////		Calendar calendar = Calendar.getInstance();
////		calendar.setTimeInMillis(milliSeconds);
//		return new SimpleDateFormat(dateFormat).format(date);

    }

    public static String getLocalDateFromMilliSecond(String timestamp,
                                                     String dateFormat) {

        // Create a DateFormatter object for displaying date in specified
        // format.
//		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//		
//		Date date = null;
//		try {
//			date = formatter.parse(timestamp);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//			return timestamp;
//		}

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//		return sdf.format(new Date(Long.parseLong(timestamp)));
//		if (isUTC)
//			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//		else
            sdf.setTimeZone(TimeZone.getDefault());
            // Create a calendar object that will convert the date and time value in
            // milliseconds to date.
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(milliSeconds);
            return sdf.format(new Date(Long.parseLong(timestamp)));
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }

    }

    public static String convertUtcToLocal(String input, Locale local) {
        try {
            // working at live server
            // String dateTimeFormat = "MM/dd/yyyy hh:mm:ss a";

            // temporarily for dev server
            String dateTimeFormat = "M-dd-yyyy hh:mm a";

            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    dateTimeFormat);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = simpleDateFormat.parse(getFormattedDate(input));
            } catch (ParseException e1) {
                e1.printStackTrace();
                return input;
            }

            return new SimpleDateFormat(dateTimeFormat, local).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
        // in android device try.....

        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }



    public static String convertUtcToLocal(String input,String inputFormat,String outputFormat, Locale local) {
        try {
            // working at live server
            // String dateTimeFormat = "MM/dd/yyyy hh:mm:ss a";

            // temporarily for dev server
//            String dateTimeFormat = "dd-M-yyyy hh:mm a";

            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    outputFormat);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = simpleDateFormat.parse(getFormattedDate(input,inputFormat,outputFormat));
            } catch (ParseException e1) {
                e1.printStackTrace();
                return input;
            }

            return new SimpleDateFormat(outputFormat, local).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
        // in android device try.....

        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }


    public static String convertUtcToLocalForGroupDetails(String input, Locale local) {
        try {
            // working at live server
            // String dateTimeFormat = "MM/dd/yyyy hh:mm:ss a";

            // temporarily for dev server
            String dateTimeFormat = "hh:mm a MMM d, yyyy";

            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    dateTimeFormat);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = simpleDateFormat.parse(getFormattedDateGrpDetails(input));
            } catch (ParseException e1) {
                e1.printStackTrace();
                return input;
            }

            return new SimpleDateFormat(dateTimeFormat, local).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
        // in android device try.....

        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }
    public static String formatDateTimeDifference(String input, Context context) {
        try {
            // working at live server
            // String dateTimeFormat = "MM/dd/yyyy hh:mm:ss a";

            // temporarily for dev server
            String dateTimeFormat = "dd MMMM yyyy h:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);

            Date inputDay = null;
            try {
                inputDay = sdf.parse(input);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cInput = Calendar.getInstance();
            cInput.setTime(inputDay);
            // cInput.getTimeInMillis();

            // CharSequence cs = DateUtils.getRelativeDateTimeString(context,
            // cInput.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS,
            // DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_TIME);
            // CharSequence cs =
            // DateUtils.formatSameDayTime(cInput.getTimeInMillis(),
            // Calendar.getInstance().getTimeInMillis(),DateFormat.DATE ,
            // DateFormat.MINUTE);
            CharSequence cs = null;
            try {
                cs = DateUtils.getRelativeTimeSpanString(cInput
                        .getTimeInMillis());
            } catch (Exception e) {
                e.printStackTrace();
                return input;
            }

            // Calendar currentDate = Calendar.getInstance();
            // SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
            //
            //
            // String dateNow = sdf.format(currentDate.getTime());
            // Date today = new Date(dateNow);
            // Date finalDay = null;
            // finalDay = (Date) sdf.parse("MM/dd/yyyy");
            // int
            // numberOfDays=(int)((finalDay.getTime()-today.getTime())/(3600*24*1000));
            // return cs.toString().substring(cs.toString().indexOf(",")+1);
            return cs.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDate(String input) {
        // "31-03-2001 00:00:00"
        String inputdateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String outputdateTimeFormat = "MM-dd-yyyy hh:mm a";

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                inputdateTimeFormat);
        try {
            date = simpleDateFormat.parse(input);
        } catch (ParseException e1) {
            return input;
        }

        return new SimpleDateFormat(outputdateTimeFormat).format(date);

        // in android device try.....
        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }

    public static String getFormattedDate(String inputTime,String inputFormat,String outputFormat) {

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                inputFormat);
        try {
            date = simpleDateFormat.parse(inputTime);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return inputTime;
        }

        return new SimpleDateFormat(outputFormat).format(date);

        // in android device try.....
        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }




    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDateGrpDetails(String input) {
        // "31-03-2001 00:00:00"
        String inputdateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String outputdateTimeFormat = "hh:mm a MMM d, yyyy";

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                inputdateTimeFormat);
        try {
            date = simpleDateFormat.parse(input);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return input;
        }

        return new SimpleDateFormat(outputdateTimeFormat).format(date);

        // in android device try.....
        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }
    public static String convertFormattedDateToMillisecond(String input,
                                                           String inputFormat) {
        // "31-03-2001 00:00:00"
        // String inputdateTimeFormat = "MM-dd-yyyy HH:mm:ss";

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        try {
            date = simpleDateFormat.parse(input);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return input;
        }
        return String.valueOf(date.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String convert24HourTo12HourFormat(String input,
                                                     Context context) {
        // Locale local = context.getResources().getConfiguration().locale;
        String inputTimeFormat = "HH:mm:ss";
        String outputTimeFormat = "h:mm a";
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                inputTimeFormat);
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = simpleDateFormat.parse(input);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return input;
        }

        return new SimpleDateFormat(outputTimeFormat).format(date);

        // in android device try.....
        // return new
        // SimpleDateFormat(dateTimeFormat,context.getResources().getConfiguration().locale).format(date);
    }

    public static long getScheduleTimeDifference(String yyyy_mm_dd_HH_mm_ss) {

        long differenceInMinutes = 0;

        Date currentDate = new Date(System.currentTimeMillis());
        String dateTimeFormat = "M-dd-yyyy h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);

        Date inputDay = null;
        try {
            inputDay = sdf.parse(yyyy_mm_dd_HH_mm_ss);
        } catch (ParseException e) {

        }

        if (inputDay == null) {
            return differenceInMinutes;
        }
        long differenceInMilliseconds = 0;
        differenceInMilliseconds = inputDay.getTime() - currentDate.getTime();
        differenceInMinutes = differenceInMilliseconds / (1000 * 60);
        // long differenceInHours = 0;
        // differenceInHours = differenceInMilliseconds / (60 * 60 * 1000) % 24;

        return differenceInMinutes;
    }


    public static String getLocaltime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getTodaysDate() {
        final Calendar c = Calendar.getInstance();
        String todaysDate = c.get(Calendar.YEAR) + "-"
                + (c.get(Calendar.MONTH) + 1) + "-"
                + (c.get(Calendar.DAY_OF_MONTH));
        return todaysDate;
    }

    public static long calculateDayDifference(String startDate, String endDate) {
        Date date1 = null,date2 = null;
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd-M-yyyy hh:mm a");
        try {
         date1 = simpleDateFormat.parse(startDate);
         date2 = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //milliseconds
        long different = date1.getTime() - date2.getTime();

//        DateUtils.getRelativeDateTimeString(AppVhortex.applicationContext,new Date().getTime(), DateUtils.DAY_IN_MILLIS, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_WEEKDAY)
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return date2.getDate() - date1.getDate();
    }

    /**Check an input date "dd-mm-yyyy" if is todays date or not*/
    public static boolean isTodaysChat(String inputDate) {
        String inputTimeFormat = "M-dd-yyyy";
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                inputTimeFormat);
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return false;
        }

      return DateUtils.isToday(date.getTime());
    }
}
