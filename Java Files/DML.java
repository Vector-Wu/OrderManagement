/**
 * Java file DML.java for OrderManager CS5200 project.
 * @author Yongliang Tan, Yuzhou Wu
 * 
 * This class provide DML for adding, updating and deleting entries in the tables.
 * 
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Date;

public class DML {
	
	/**
	 * DML function to add product to the product table
	 * @param stmt_Product  Prepared statement to pass in
	 * @param ProductName  The product name
	 * @param Description  The description of the product
	 * @param SKU  The SKU of the product
	 * @throws SQLException  Potential SQL exception
	 */
	static public void addProduct(PreparedStatement stmt_Product, 
			String ProductName, String Description, String SKU) throws SQLException{
		
		stmt_Product.setString(1, ProductName);
		stmt_Product.setString(2, Description);
		stmt_Product.setString(3, SKU);
		
		stmt_Product.execute();
		System.out.printf("%s added into Product\n", ProductName);
	}
	
	/**
	 * DML function to add Inventory to the InventoryRecord table
	 * @param stmt_InventoryRecord  Prepared statement to pass in
	 * @param SKU  The SKU of the inventory
	 * @param UnitPrice  The unit price of the inventory
	 * @param Number The number of stock of the inventory
	 * @throws SQLException  Potential SQL exception
	 */
	static public void addInventory(PreparedStatement  stmt_InventoryRecord,
			 String SKU, double UnitPrice, int Number) throws SQLException{
		stmt_InventoryRecord.setInt(1, Number);
		stmt_InventoryRecord.setDouble(2, UnitPrice);
		stmt_InventoryRecord.setString(3, SKU);
		
		stmt_InventoryRecord.execute();
		System.out.printf("%d of %s added into InventoryRecord\n", Number ,SKU);
	}
	
	/**
	 * DML function to update Inventory in the InventoryRecord table
	 * @param updateRow_InventoryRecord  Prepared statement to pass in
	 * @param Number  The number of stock of the inventory
	 * @param UnitPrice  The unit price of the inventory
	 * @param SKU  The SKU of the inventory
	 * @throws SQLException  Potential SQL exception
	 */
	static public void updateInventory(PreparedStatement  updateRow_InventoryRecord,
			 int Number, double UnitPrice, String SKU) throws SQLException{
		updateRow_InventoryRecord.setInt(1, Number);
		updateRow_InventoryRecord.setDouble(2, UnitPrice);
		updateRow_InventoryRecord.setString(3, SKU);
		
		updateRow_InventoryRecord.execute();
		System.out.printf("%s updated to number %d in InventoryRecord\n",SKU, Number);
	}
	
	/**
	 * DMl function to add Customer to the Customer table
	 * @param stmt_Customer  Prepared statement to pass in
	 * @param FirstName  First name of the customer
	 * @param LastName  Last name of the customer
	 * @param Address  Address of the customer
	 * @param City  City of the customer
	 * @param State  State of the customer
	 * @param Country  Country of the customer
	 * @param PostalCode  Postal code of the customer
	 * @param CustomId  Customer ID
	 * @throws SQLException  Potential SQL exception
	 */
	static public void addCustomer(PreparedStatement stmt_Customer,
			String FirstName, String LastName, String Address, String City, String State, String Country,
			int PostalCode, int CustomId) throws SQLException{
		stmt_Customer.setString(1, FirstName);
		stmt_Customer.setString(2, LastName);
		stmt_Customer.setString(3, Address);
		stmt_Customer.setString(4, City);
		stmt_Customer.setString(5, State);
		stmt_Customer.setString(6, Country);
		stmt_Customer.setInt(7, PostalCode);
		stmt_Customer.setInt(8, CustomId);
		
		stmt_Customer.execute();
		
		System.out.printf("%s %s with id %d added to Customer\n", FirstName, LastName, CustomId);
	}
	
	/**
	 * DML function to add Order to the Order table
	 * @param stmt_Order  Prepared statement to pass in
	 * @param CustomerId CustomerID
	 * @param OrderId  OrderId of the order
	 * @throws SQLException  Potential SQL exception
	 */
	static public void addOrder(PreparedStatement stmt_Order, int CustomerId, 
			int OrderId, int Count) throws SQLException {
		
		long millis=System.currentTimeMillis();  
		Date orderDate=new java.sql.Date(millis); 
		
		stmt_Order.setInt(1,CustomerId);
		stmt_Order.setInt(2,OrderId);
		stmt_Order.setDate(3, orderDate);
		stmt_Order.setDate(4, null);
		stmt_Order.setInt(5, 0);
		stmt_Order.setInt(6, Count);
		
		stmt_Order.execute();
		System.out.printf("%s added to Order\n", OrderId);
	}
	
	/**
	 * DML function to add OrderRecord to OrderRecord table
	 * @param stmt_OrderRecord  Prepared statement to pass in
	 * @param OrderId  OrderId of the order
	 * @param SKU  The SKU of the OrderRecord
	 * @param Number  Number of units ordered in OrderRecord
	 * @param UnitPrice  Unit price of the ordered inventory
	 * @throws SQLException  Potential SQL exception
	 */
	static public void addOrderRecord(Connection conn, PreparedStatement stmt_OrderRecord, int OrderId, String SKU, 
			int Number, double UnitPrice) throws SQLException {
		stmt_OrderRecord.setInt(1, OrderId);
		stmt_OrderRecord.setString(2, SKU);
		stmt_OrderRecord.setInt(3, Number);
		stmt_OrderRecord.setDouble(4, UnitPrice);;
		
		PreparedStatement checkInventoryNumber = conn.prepareStatement("select number from InventoryRecord where SKU = ?");
		checkInventoryNumber.setString(1, SKU);
		ResultSet rs = checkInventoryNumber.executeQuery();
		int InventoryNumber = 0;
		if(rs.next()) InventoryNumber = rs.getInt(1);
		if(InventoryNumber >= Number) stmt_OrderRecord.setInt(5, 1);
		else stmt_OrderRecord.setInt(5, 0);
		
		stmt_OrderRecord.execute();
		System.out.printf("OrderId %s with SKU %s added to OrderRecord\n", OrderId, SKU);
	}
	
	/**
	 * DML function to check Inventory
	 * @param selectRow_InventoryRecord  Prepared statement to pass in
	 * @param SKU The SKU of the inventory
	 * @return count of inventory  If 0, nothing in stock. -1 means doesn't exist. Any positive integer represents the stock amount.
	 * @throws SQLException  Potential SQL exception
	 */
	static public int checkInventoryExistence(PreparedStatement selectRow_InventoryRecord, String SKU) throws SQLException{
		ResultSet rs;
		int count = -1;
		selectRow_InventoryRecord.setString(1, SKU);
		rs = selectRow_InventoryRecord.executeQuery();
		if(rs.next()) count = rs.getInt(1);
		return count;
	}

	/**
	 * DML function to set status of order that has OrderRecord's exceed inventory stock to incomplete
	 * @param updateRow_Order  Prepared statement to pass in
	 * @param status  The status of the order
	 * @param OrderId  The OrderID of the order
	 * @throws SQLException  Potential SQL exception
	 */
	static public void CancelOrder(PreparedStatement updateRow_Order, int status, 
			int OrderId) throws SQLException {
		updateRow_Order.setInt(1, status);
		updateRow_Order.setInt(2, OrderId);
		
		updateRow_Order.execute();
		System.out.printf("Order %d cancelled.\n", OrderId);
	}
	
	/**
	 * DML function to place order that all of OrderRecords didn't exceed inventory stock
	 * @param updateRow_Order  Prepared statement to pass in
	 * @param status  The status of the order going to be set
	 * @param oldStatus  Old status of the order
	 * @throws SQLException  Potential SQL exception
	 */
	static public void PlaceOrder(PreparedStatement updateRow_Order, int status, 
			int oldStatus) throws SQLException {
		updateRow_Order.setInt(1, status);
		long millis=System.currentTimeMillis();  
		Date shipmentDate=new java.sql.Date(millis); 
		updateRow_Order.setDate(2, shipmentDate);
		updateRow_Order.setInt(3, oldStatus);
		
		updateRow_Order.execute();
	}
	
	/**
	 * DML function to get number of complete orderRecords
	 * @param conn SQL connection to pass in
	 * @param OrderId  order id
	 * @throws SQLException  Potential SQL exception
	 */
	static public int checkOrder(Connection conn, int OrderId) throws SQLException {
		PreparedStatement checkStatus = conn.prepareStatement("Select Status from  OrderRecord where OrderId = ?");
		checkStatus.setInt(1, OrderId);
		int RecordCount = 0;
		ResultSet rs = checkStatus.executeQuery();
		while(rs.next()) 
			if(rs.getInt(1) == 1)RecordCount++;
		return RecordCount;
	}
	
	/**
	 * DML function to update the shipment date
	 * @param updateRow_Order Prepared statement to pass in
	 * @param shippingDate The shipping date going to be set
	 * @param OrderId The Order ID to set the shipping date
	 * @throws SQLException Potential SQL exception
	 */
	static public void updateShipment(PreparedStatement updateRow_Order, 
			Date shippingDate, int OrderId) throws SQLException {
		updateRow_Order.setDate(1, shippingDate);
		updateRow_Order.setInt(2, OrderId);
		
		updateRow_Order.execute();
		System.out.printf("Order %d shippping time is:" + shippingDate, OrderId);
		System.out.printf("\n");
	}
	
}
