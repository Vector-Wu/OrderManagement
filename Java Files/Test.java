/**
 * Java file Test.java for OrderManager CS5200 project.
 * @author Yongliang Tan, Yuzhou Wu
 * 
 * This class provides testing for the OrderManger project. The testing mainly focus on
 * whether the tables input restrictions, triggers and stored functions are working properly.
 * At the end, it also prints out the queries implemented.
 * 
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class Test {
	/**
	 * Testing of the OrderManager database. The text files used are included in the repository's 'Testing TXT' folder.
	 * @param args Empty
	 */
	public static void main(String[] args) {
	    String protocol = "jdbc:derby:";
	    String dbName = "OrderManager";
		String connStr = protocol + dbName+ ";create=true";
		
		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");
        
        // result set for queries
		String line;
              
	    // tables created by this program
		String OrderManagerTables[] = { 
				"OrderRecord", "ProductOrder","Customer","InventoryRecord", "Product"
			};
        
        try {
			// connect to database
			Connection  conn = DriverManager.getConnection(connStr, props);
			Statement stmt = conn.createStatement();
			
			String fileName = "product.txt";
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			
			// insert into product
	    	PreparedStatement insertRow_Product = conn.prepareStatement(
	    			"insert into Product values(?, ?, ?)");
	    	
	    	// insert into inventoryRecord
	    	PreparedStatement insertRow_InventoryRecord = conn.prepareStatement(
	    			"insert into InventoryRecord values(?, ?, ?)");
	    	// update inventoryRecord
	    	PreparedStatement updateRow_InventoryRecord = conn.prepareStatement(
	    			"update InventoryRecord set Number = ?, UnitPrice = ? where SKU = ?");
	    	
	    	// insert to Customer
	    	PreparedStatement insertRow_Customer = conn.prepareStatement(
	    			"insert into Customer values(?, ?, ?, ?, ?, ?, ?, ?)");
	    	
	    	// insert to ProductOrder
	    	PreparedStatement insertRow_Order = conn.prepareStatement(
	    			"insert into ProductOrder values(?, ?, ?, ?, ?, ?)");
	    	
	    	// insert to OrderRecord
	    	PreparedStatement insertRow_OrderRecord = conn.prepareStatement(
	    			"insert into OrderRecord values(?, ?, ?, ?, ?)");

	    	// check number of some product in InventoryRecord
	    	PreparedStatement selectRow_InventoryRecord = conn.prepareStatement(
	    			"select Number from InventoryRecord where SKU = ?");
	    	
	    	// cancel order
	    	PreparedStatement cancel_Order = conn.prepareStatement(
	    			"update ProductOrder set Status = ? where OrderId = ?");
	    	
	    	// place order
	    	PreparedStatement place_Order = conn.prepareStatement(
	    			"update ProductOrder set Status = ?, ShipmentDate = ? where Status = ?");

	    	
			for (String tbl : OrderManagerTables) {
	            try {
	            		stmt.executeUpdate("delete from " + tbl);
	            		System.out.println("Truncated table " + tbl);
	            } catch (SQLException ex) {
	            		System.out.println("Did not truncate table " + tbl);
	            }
            }
			System.out.println("--------------------------------------------------------------------------------------------------");
			

			// read data to product related table
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				// insert data into Product
				try {
					DML.addProduct(insertRow_Product, data[0], data[1], data[2]);
				} catch (SQLException ex) {
					System.err.printf("LOG: Can not add Product %s\n", data[0]);
				}
				
				// insert data into InventoryRecord
				// if SKU already exists, update the number and price
					int number = Integer.parseInt( data[3] );
					double price = Double.parseDouble( data[4] );
				try {
					int count = DML.checkInventoryExistence(selectRow_InventoryRecord, data[2]);
					if(count == -1) DML.addInventory(insertRow_InventoryRecord, data[2], price, number);
					else DML.updateInventory(updateRow_InventoryRecord, count+number, price, data[2]);
				}catch (SQLException ex) {
					System.err.printf("ERROR: Can not add or update InventoryRecord for %s\n", data[2]);
				}				
			}
			
			// read data to Customer
			fileName = "customer.txt";
			br = new BufferedReader(new FileReader(new File(fileName)));
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				String FirstName = data[0];
				String LastName = data[1];
				String Address = data[2];
				String City = data[3];
				String State = data[4];
				String Country = data[5];
				int PostalCode = Integer.parseInt(data[6]);
				int CustomId = Integer.parseInt(data[7]);
				try {
					DML.addCustomer(insertRow_Customer, FirstName, LastName, Address, City, State, Country, PostalCode, CustomId);
				} catch (SQLException ex) {
					System.err.printf("LOG: Can not add Customer %s %s\n", data[0], data[1]);
				}	
			}
			
			//read data to order
			fileName = "order.txt";
			br = new BufferedReader(new FileReader(new File(fileName)));
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				int CustomerId = Integer.parseInt(data[0]);
				int OrderId = Integer.parseInt(data[1]);
				int Count = Integer.parseInt(data[2]);
				try {
					DML.addOrder(insertRow_Order, CustomerId, OrderId, Count);
				} catch (SQLException ex) {
					System.err.printf("LOG: Can not add order %d\n", OrderId);
				}	
			}
			
			System.out.println("Before processing all orders:");
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printProducts(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printInventoryRecord(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printCustomer(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printOrder(conn);
			
			// read data to orderRecord
			fileName = "orderlist.txt";
			br = new BufferedReader(new FileReader(new File(fileName)));
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				int OrderId = Integer.parseInt(data[0]);
				String SKU = data[1];
				int Number = Integer.parseInt(data[2]);
				double UnitPrice = Double.parseDouble(data[3]);
				try {
					DML.addOrderRecord(conn, insertRow_OrderRecord, OrderId, SKU, Number, UnitPrice);
				} catch (SQLException ex) {
					System.err.printf("LOG: Can not add orderId %d for product %s\n", OrderId, SKU);
					//ex.printStackTrace();
				}	
			}
			
			
			//update order status
			fileName = "order.txt";
			br = new BufferedReader(new FileReader(new File(fileName)));
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				int OrderId = Integer.parseInt(data[1]);
				try {
					int recordCount = DML.checkOrder(conn, OrderId);
					PreparedStatement getRecordCount = conn.prepareStatement("Select RecordCount from ProductOrder where OrderId = ?");
					getRecordCount.setInt(1, OrderId);
					ResultSet rs = getRecordCount.executeQuery();
					if(rs.next()) {
						if(rs.getInt(1) != recordCount) {
							DML.CancelOrder(cancel_Order, -1, OrderId);
						}
					}
				} catch (SQLException ex) {
					System.err.printf("LOG: Can not add order %d\n", OrderId);
				}	
			}
			
			DML.PlaceOrder(place_Order, 1, 0);
			
			
			System.out.println("After processing all orders:");
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printProducts(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printInventoryRecord(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printCustomer(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printOrder(conn);
			System.out.println("--------------------------------------------------------------------------------------------------");
			Print.printOrderRecord(conn);
			
			System.out.println("--------------------------------------------------------------------------------------------------");
			System.out.println("Below is the query test example:");
			Query.showDescription(conn, "CN-000000-0B");
			Query.showCustomerInformation(conn, 1);
			Query.checkShippment(conn, 1);
			Query.checkShippment(conn, 7);
			br.close();
			
			
        } catch (SQLException e) {
        	e.printStackTrace();
        }catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
