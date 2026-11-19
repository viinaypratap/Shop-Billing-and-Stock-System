package shopbilling.dao;

import shopbilling.db.DatabaseConnection;
import shopbilling.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> search(String term) throws SQLException {
        List<Customer> rows = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_name LIKE ? OR phone LIKE ? ORDER BY customer_name";
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,"%"+term+"%"); ps.setString(2,"%"+term+"%");
            try (ResultSet rs=ps.executeQuery()) { while(rs.next()) rows.add(new Customer(rs.getInt("customer_id"),rs.getString("customer_name"),rs.getString("phone"),rs.getString("address"))); }
        }
        return rows;
    }
    public List<Customer> findAll() throws SQLException { return search(""); }
    public void add(Customer x) throws SQLException { save(x,false); }
    public void update(Customer x) throws SQLException { save(x,true); }
    private void save(Customer x, boolean update) throws SQLException {
        String sql=update ? "UPDATE customers SET customer_name=?,phone=?,address=? WHERE customer_id=?" : "INSERT INTO customers(customer_name,phone,address) VALUES(?,?,?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,x.getCustomerName()); ps.setString(2,x.getPhone()); ps.setString(3,x.getAddress()); if(update) ps.setInt(4,x.getCustomerId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM customers WHERE customer_id=?")) { ps.setInt(1,id); ps.executeUpdate(); }
    }
}
