package shopbilling.model;

public class BillItem {
    private final Product product;
    private int quantity;
    public BillItem(Product product, int quantity) { this.product = product; this.quantity = quantity; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { quantity = value; }
    public double getSubtotal() { return product.getPrice() * quantity; }
}
