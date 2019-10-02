/**
 * Java file Print.java for OrderManager CS5200 project.
 * @author Yongliang Tan, Yuzhou Wu
 * 
 * This class provides DQL for making commonly used queries to retrieve product, inventory, 
 * and order tables from the database.
 * 
 */


import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Print {
	
	/**
	 * Print function to print product table
	 * @param conn SQL connection to pass in
	 * @throws SQLException Potential SQL exception
	 */
	static void printProducts(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select Name, Description, SKU from Product order by SKU");
		) {
			System.out.println("Product:");
			System.out.println("SKU		Name		Description");
			int count = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				String sku = rs.getString(3);
				System.out.printf("%s	%s	%s\n", sku, name, description);
				count++;
			}
			System.out.printf("Product count: %d\n", count);
		}
	}
	
	/**
	 * Print function to print InventoryRecord table
	 * @param conn SQL connection to pass in
	 * @throws SQLException Potential SQL exception
	 */
	static void printInventoryRecord(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select Number, UnitPrice, SKU from InventoryRecord order by SKU");
		) {
			System.out.println("InventoryRecord:");
			System.out.println("sku		Number	unitprice");
			int count = 0;
			while (rs.next()) {
				int number = rs.getInt(1);
				double unitPrice = rs.getDouble(2);
				String sku = rs.getString(3);
				System.out.printf("%s	%s	%f\n", sku, number, unitPrice);
				count++;
			}
			System.out.printf("Inventory count: %d\n", count);
		}
	}
	
	/**
	 * Print function to print Customer table
	 * @param conn SQL connection to pass in
	 * @throws SQLException Potential SQL exception
	 */
	static void printCustomer(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select FirstName, LastName, Address, City, State, Country, PostalCode, CustomerId from Customer order by LastName,FirstName");
		) {
			System.out.println("Customer:");
			System.out.println("FirstName	LastName	Address			City		State	Country		PostalCode	CustomerId");
			int count = 0;
			while (rs.next()) {
				String firstName = rs.getString(1);
				String lastName = rs.getString(2);
				String address = rs.getString(3);
				String city = rs.getString(4);
				String state = rs.getString(5);
				String country = rs.getString(6);
				int postalCode = rs.getInt(7);
				int customerId = rs.getInt(8);
				System.out.printf("%s		%s		%s	%s	%s	%s	%d		%d\n", firstName, lastName, address, city, state, country
						,postalCode, customerId);
				count++;
			}
			System.out.printf("Customer count: %d\n", count);
		}
	}
	
	/**
	 * Print function to print Order table
	 * @param conn SQL connection to pass in
	 * @throws SQLException Potential SQL exception
	 */
	static void printOrder(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select CustomerId, OrderId, OrderDate,ShipmentDate,Status from ProductOrder order by OrderDate");
		) {
			System.out.println("Order:");
			System.out.println("CustomerId	OrderId		OrderDate	ShipmentDate		Status");
			int count = 0;
			while (rs.next()) {
				int customerId = rs.getInt(1);
				int orderId = rs.getInt(2);
				Date orderDate= rs.getDate(3);
				Date shipmentDate = rs.getDate(4);
				int status = rs.getInt(5);
				String strStatus = null;
				if(status == 0) strStatus = "Pending";
				else if(status == 1) strStatus = "Complete";
				else if(status == -1) strStatus = "Incomplete";
				System.out.printf("%d		%d		%s	%s		%s\n", customerId, orderId, orderDate, shipmentDate, strStatus);
				count++;
			}
			System.out.printf("Order count: %d\n", count);
		}
	}
	
	/**
	 * Print function to print OrderRecord table
	 * @param conn SQL connection to pass in
	 * @throws SQLException Potential SQL exception
	 */
	static void printOrderRecord(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select OrderId, SKU, Number, UnitPrice, Status from OrderRecord order by OrderId");
		) {
			System.out.println("OrderRecord:");
			System.out.println("OrderId		SKU		Number	UnitPrice");
			int count = 0;
			while (rs.next()) {
				int orderId = rs.getInt(1);
				String SKU = rs.getString(2);
				int number= rs.getInt(3);
				double unitPrice = rs.getDouble(4);
				System.out.printf("%d		%s	%d	%s\n", orderId, SKU, number, unitPrice);
				count++;
			}
			System.out.printf("OrderRecord count: %d\n", count);
		}
	}
}
