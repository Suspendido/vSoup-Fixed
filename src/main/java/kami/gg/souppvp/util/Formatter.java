package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright (c) 2026. @Comunidad, made since 29/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class Formatter {

    private static final ThreadLocal<StringBuilder> mmssBuilder = ThreadLocal.withInitial(StringBuilder::new);
    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";

    // TimeUtil methods
    public static String convertToHhMmSs(Long secs) {
        int h = (int) (secs / 3600), i = (int) (secs - h * 3600), m = i / 60, s = i - m * 60;
        String timeF = "";
        if (m < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + m + ":";
        if (s < 10) {
            timeF = timeF + "0";
        }
        timeF = timeF + s;
        return timeF;
    }

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        } else {
            return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
        }
    }

    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0F);
    }

    public static String dateToString(Date date, String secondaryColor) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new SimpleDateFormat("MMM dd yyyy " + (secondaryColor == null ? "" : secondaryColor) + "(hh:mm aa zz)").format(date);
    }

    public static Timestamp addDuration(long duration) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + duration));
    }

    public static Timestamp truncateTimestamp(Timestamp timestamp) {
        if (timestamp.toLocalDateTime().getYear() > 2037) {
            timestamp.setYear(2037);
        }

        return timestamp;
    }

    public static Timestamp addDuration(Timestamp timestamp) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + timestamp.getTime()));
    }

    public static Timestamp fromMillis(long millis) {
        return new Timestamp(millis);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String millisToRoundedTime(long millis) {
        millis += 1L;

        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        if (years > 0) {
            return years + " year" + (years == 1 ? "" : "s");
        } else if (months > 0) {
            return months + " month" + (months == 1 ? "" : "s");
        } else if (weeks > 0) {
            return weeks + " week" + (weeks == 1 ? "" : "s");
        } else if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s");
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
    }

    public static long parseTimeToMillis(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            found = switch (type) {
                case "s" -> {
                    totalTime += value;
                    yield true;
                }
                case "m" -> {
                    totalTime += value * 60;
                    yield true;
                }
                case "h" -> {
                    totalTime += value * 60 * 60;
                    yield true;
                }
                case "d" -> {
                    totalTime += value * 60 * 60 * 24;
                    yield true;
                }
                case "w" -> {
                    totalTime += value * 60 * 60 * 24 * 7;
                    yield true;
                }
                case "M" -> {
                    totalTime += value * 60 * 60 * 24 * 30;
                    yield true;
                }
                case "y" -> {
                    totalTime += value * 60 * 60 * 24 * 365;
                    yield true;
                }
                default -> found;
            };
        }

        return !found ? -1 : totalTime * 1000;
    }

    public static String formatScoreboardDate(Date date) {
        String format = "dd/MM/yy";

        try {
            format = SoupPvP.getInstance().getScoreboardManager().getScoreboardConfig().getString("SCOREBOARD_INFO.DATE_FORMAT", "dd/MM/yy");
        } catch (Exception ignored) {}

        return new SimpleDateFormat(format).format(date);
    }

    // TimeUtils methods
    public static String formatIntoHHMMSS(int secs) {
        return formatIntoMMSS(secs);
    }

    public static String formatLongIntoHHMMSS(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoMMSS(unconvertedSeconds);
    }

    public static String formatIntoMMSS(int secs) {
        int seconds = secs % 60;
        long minutesCount = (secs - seconds) / 60;
        long minutes = minutesCount % 60L;
        long hours = (minutesCount - minutes) / 60L;
        StringBuilder result = mmssBuilder.get();
        result.setLength(0);
        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0");
            }
            result.append(hours);
            result.append(":");
        }
        if (minutes < 10L) {
            result.append("0");
        }
        result.append(minutes);
        result.append(":");
        if (seconds < 10) {
            result.append("0");
        }
        result.append(seconds);
        return result.toString();
    }

    public static String formatScoreboardHHMMSS(int secs) {
        int seconds = secs % 60;
        long minutesCount = (secs - seconds) / 60;
        long minutes = minutesCount % 60L;
        long hours = (minutesCount - minutes) / 60L;
        StringBuilder result = mmssBuilder.get();
        result.setLength(0);
        if (hours > 0L) {
            result.append(hours);
            result.append("h");
        }
        if (minutes > 0L) {
            if (hours > 0L) {
                result.append(", ");
            }
            result.append(minutes);
            result.append("m");
        }
        if (seconds > 0) {
            if (minutes > 0L) {
                result.append(", ");
            }
            result.append(seconds);
            result.append("s");
        }
        return result.toString();
    }

    public static String formatLongIntoMMSS(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoMMSS(unconvertedSeconds);
    }

    public static String formatIntoDetailedString(int secs) {
        String fMinutes;
        String fHours;
        String fDays;
        if (secs == 0) {
            return "0 seconds";
        }
        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = remainder / 60 - hours * 60;
        int seconds = remainder % 3600 - minutes * 60;
        fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
        fHours= hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
        fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
        String fSeconds = seconds > 0 ? " " + seconds + " second" + (seconds > 1 ? "s" : "") : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatLongIntoDetailedString(long secs) {
        int unconvertedSeconds = (int)secs;
        return formatIntoDetailedString(unconvertedSeconds);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final SimpleDateFormat dateFormatNoTime = new SimpleDateFormat("MM/dd/yyyy");

    public static String formatIntoCalendarString(Date date) {
        return dateFormat.format(date);
    }

    public static String formatIntoCalendarStringNoTime(Date date) {
        return dateFormatNoTime.format(date);
    }

    public static int parseTime(String time) {
        if (time.equals("0") || time.isEmpty()) {
            return 0;
        }
        String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
        int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
        int seconds = -1;
        for (int i = 0; i < lifeMatch.length; ++i) {
            Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time);
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0;
                }
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }
        if (seconds == -1) {
            throw new IllegalArgumentException("Invalid time provided.");
        }
        return seconds;
    }

    public static long parseTimeToLong(String time) {
        return parseTime(time);
    }

    public static int getSecondsBetween(Date a, Date b) {
        return (int)getSecondsBetweenLong(a, b);
    }

    public static long getSecondsBetweenLong(Date a, Date b) {
        long diff = a.getTime() - b.getTime();
        long absDiff = Math.abs(diff);
        return absDiff / 1000L;
    }

    public static String formatBalance(double balance) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(balance);
    }
}
