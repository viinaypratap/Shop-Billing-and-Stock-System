package shopbilling.model;

public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private String category;
    private double price;
    private int quantity;
    private String supplier;

    public Product() { }
    public Product(int id, String code, String name, String category, double price, int quantity, String supplier) {
        this.productId = id; this.productCode = code; this.productName = name; this.category = category;
        this.price = price; this.quantity = quantity; this.supplier = supplier;
    }
    public int getProductId() { return productId; }
    public void setProductId(int value) { productId = value; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String value) { productCode = value; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public double getPrice() { return price; }
    public void setPrice(double value) { price = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { quantity = value; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String value) { supplier = value; }
    @Override public String toString() { return productCode + " - " + productName + " (Stock: " + quantity + ")"; }
}
