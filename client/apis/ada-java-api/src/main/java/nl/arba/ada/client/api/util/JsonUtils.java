package nl.arba.ada.client.api.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JsonUtils {
    public static Date readJsonDate(Map input) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ((Integer) input.get("year")));
        c.set(Calendar.MONTH, ((Integer) input.get("month"))-1);
        c.set(Calendar.DATE, (Integer) input.get("day"));
        c.set(Calendar.HOUR_OF_DAY, (Integer) input.get("hour"));
        c.set(Calendar.MINUTE, (Integer) input.get("minute"));
        c.set(Calendar.SECOND, (Integer) input.get("second"));
        return c.getTime();
    }
}
