package shopbilling.util;

public final class ValidationUtil {
    private ValidationUtil() { }
    public static double positivePrice(String value) {
        double price = Double.parseDouble(value.trim());
        if (!Double.isFinite(price) || price <= 0) throw new IllegalArgumentException("Price must be greater than 0.");
        return price;
    }
    public static int nonNegativeInteger(String value, String field) {
        int number = Integer.parseInt(value.trim());
        if (number < 0) throw new IllegalArgumentException(field + " cannot be negative.");
        return number;
    }
    public static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " cannot be empty.");
        return value.trim();
    }
    public static String phone(String value) {
        String phone = value == null ? "" : value.trim();
        if (!phone.isEmpty() && !phone.matches("[0-9+() -]{7,20}")) throw new IllegalArgumentException("Enter a valid phone number.");
        return phone;
    }
}
