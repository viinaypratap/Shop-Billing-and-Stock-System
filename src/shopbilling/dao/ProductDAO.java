package shopbilling.dao;

import shopbilling.db.DatabaseConnection;
import shopbilling.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Product map(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("product_id"), rs.getString("product_code"), rs.getString("product_name"),
                rs.getString("category"), rs.getDouble("price"), rs.getInt("quantity"), rs.getString("supplier"));
    }
    public List<Product> search(String term) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_code LIKE ? OR product_name LIKE ? OR category LIKE ? ORDER BY product_name";
        List<Product> products = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + term + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) products.add(map(rs)); }
        }
        return products;
    }
    public List<Product> findAll() throws SQLException { return search(""); }
    public Product findById(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM products WHERE product_id=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public void add(Product p) throws SQLException {
        String sql = "INSERT INTO products(product_code,product_name,category,price,quantity,supplier) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p); ps.executeUpdate();
        }
    }
    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET product_code=?,product_name=?,category=?,price=?,quantity=?,supplier=? WHERE product_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p); ps.setInt(7, p.getProductId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE product_id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
    private void bind(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1,p.getProductCode()); ps.setString(2,p.getProductName()); ps.setString(3,p.getCategory());
        ps.setDouble(4,p.getPrice()); ps.setInt(5,p.getQuantity()); ps.setString(6,p.getSupplier());
    }
}
