package shopbilling.model;

public class Customer {
    private int customerId;
    private String customerName;
    private String phone;
    private String address;

    public Customer() { }
    public Customer(int id, String name, String phone, String address) {
        this.customerId = id; this.customerName = name; this.phone = phone; this.address = address;
    }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int value) { customerId = value; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String value) { customerName = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { phone = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { address = value; }
    @Override public String toString() { return customerName + (phone == null || phone.isBlank() ? "" : " - " + phone); }
}
