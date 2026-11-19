package shopbilling.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BillNumberGenerator {
    private BillNumberGenerator() { }
    public static String generate() {
        return "BIL-" + LocalDate.now().getYear() + "-" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")) + "-" +
                Long.toString(System.currentTimeMillis(), 36).toUpperCase();
    }
}
