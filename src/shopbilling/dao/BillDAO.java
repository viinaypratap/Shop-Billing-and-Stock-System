package shopbilling.dao;

import shopbilling.db.DatabaseConnection;
import shopbilling.model.BillItem;
import shopbilling.model.Product;
import shopbilling.util.BillNumberGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    public String completeSale(Integer customerId, List<BillItem> items) throws SQLException {
        if (items.isEmpty()) throw new IllegalArgumentException("Add at least one item to the bill.");
        String number = BillNumberGenerator.generate();
        String billSql="INSERT INTO bills(bill_number,customer_id,total_amount) VALUES(?,?,?)";
        String itemSql="INSERT INTO bill_items(bill_id,product_id,quantity,price,subtotal) VALUES(?,?,?,?,?)";
        String stockSql="UPDATE products SET quantity=quantity-? WHERE product_id=? AND quantity>=?";
        try(Connection c=DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                double total=items.stream().mapToDouble(BillItem::getSubtotal).sum();
                int billId;
                try(PreparedStatement ps=c.prepareStatement(billSql,Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,number); if(customerId==null) ps.setNull(2,Types.INTEGER); else ps.setInt(2,customerId);
                    ps.setBigDecimal(3,java.math.BigDecimal.valueOf(total)); ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()) { if(!keys.next()) throw new SQLException("Could not create bill record."); billId=keys.getInt(1); }
                }
                try(PreparedStatement stock=c.prepareStatement(stockSql); PreparedStatement line=c.prepareStatement(itemSql)) {
                    for(BillItem item:items) {
                        int qty=item.getQuantity();
                        stock.setInt(1,qty); stock.setInt(2,item.getProduct().getProductId()); stock.setInt(3,qty);
                        if(stock.executeUpdate()!=1) throw new SQLException("Insufficient stock for " + item.getProduct().getProductName() + ". Refresh products and try again.");
                        line.setInt(1,billId); line.setInt(2,item.getProduct().getProductId()); line.setInt(3,qty);
                        line.setBigDecimal(4,java.math.BigDecimal.valueOf(item.getProduct().getPrice()));
                        line.setBigDecimal(5,java.math.BigDecimal.valueOf(item.getSubtotal())); line.addBatch();
                    }
                    line.executeBatch();
                }
                c.commit(); return number;
            } catch(Exception ex) { c.rollback(); if(ex instanceof SQLException) throw (SQLException)ex; throw new SQLException("Sale could not be completed.",ex); }
            finally { c.setAutoCommit(true); }
        }
    }
    public List<Object[]> history() throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        String sql="SELECT b.bill_id,b.bill_number,b.bill_date,COALESCE(c.customer_name,'Walk-in') customer_name,b.total_amount FROM bills b LEFT JOIN customers c ON b.customer_id=c.customer_id ORDER BY b.bill_date DESC";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) rows.add(new Object[]{rs.getInt("bill_id"),rs.getString("bill_number"),rs.getTimestamp("bill_date"),rs.getString("customer_name"),rs.getBigDecimal("total_amount")});
        }
        return rows;
    }
    public List<Object[]> billItems(int billId) throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        String sql="SELECT p.product_code,p.product_name,bi.price,bi.quantity,bi.subtotal FROM bill_items bi JOIN products p ON bi.product_id=p.product_id WHERE bi.bill_id=?";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,billId); try(ResultSet rs=ps.executeQuery()) { while(rs.next()) rows.add(new Object[]{rs.getString(1),rs.getString(2),rs.getBigDecimal(3),rs.getInt(4),rs.getBigDecimal(5)}); }
        }
        return rows;
    }
    public double[] dashboardCounts() throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); Statement st=c.createStatement()) {
            double products=0,low=0,bills=0,sales=0;
            try(ResultSet rs=st.executeQuery("SELECT COUNT(*) total,COALESCE(SUM(quantity<=5),0) low FROM products")) { if(rs.next()){products=rs.getLong("total");low=rs.getLong("low");} }
            try(ResultSet rs=st.executeQuery("SELECT COUNT(*) bills,COALESCE(SUM(total_amount),0) sales FROM bills WHERE DATE(bill_date)=CURDATE()")) { if(rs.next()){bills=rs.getLong("bills");sales=rs.getDouble("sales");} }
            return new double[]{products,low,bills,sales};
        }
    }
    public boolean login(String username,String password) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT user_id FROM users WHERE username=? AND password=?")) {
            ps.setString(1,username); ps.setString(2,password); try(ResultSet rs=ps.executeQuery()) { return rs.next(); }
        }
    }
}
