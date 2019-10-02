/**
 * Java file Print.java for OrderManager CS5200 project.
 * @author Yongliang Tan, Yuzhou Wu
 * 
 * This class provides DQL for showing Product and Customer's information and checking shipment
 *  from the database.
 * 
 */


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Query {
	
	/**
	 * Query function to print selected product's name with description
	 * @param conn SQL connection to pass in
	 * @param SKU The selected SKU
	 * @throws SQLException Potential SQL exception
	 */
	static void showDescription(Connection conn, String SKU) throws SQLException {
				
			PreparedStatement showDescription = conn.prepareStatement(
						"select Name, Description from Product where sku = ?");
				
			showDescription.setString(1, SKU);
			ResultSet rs = showDescription.executeQuery();
				
			if (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				System.out.printf("%s	%s \n",  name, description);
			}			
	}
	
	/**
	 * Query function to print selected Customer's information
	 * @param conn SQL connection to pass in
	 * @param id The CustomerID of the selected customer
	 * @throws SQLException Potential SQL exception
	 */
	static void showCustomerInformation(Connection conn, int id) throws SQLException {
			
		PreparedStatement showCustomerInformation = conn.prepareStatement(
					"select FirstName, LastName, Address, City, State, Country from Customer where CustomerId = ?");
			
		showCustomerInformation.setInt(1, id);
		ResultSet rs = showCustomerInformation.executeQuery();
			
		if (rs.next()) {
			String name = rs.getString(1) + " " + rs.getString(2);
			String detailedAddress = rs.getString(3) + ", " + rs.getString(4) + ", " +rs.getString(5) + ", "+
					rs.getString(6);
			System.out.printf("%s	%s \n",  name, detailedAddress);
		}			
	}
	
	/**
	 * Query function to print selected Order's shipment information
	 * @param conn SQL connection to pass in
	 * @param orderId The Order ID of the selected order
	 * @throws SQLException Potential SQL exception
	 */
	static void checkShippment(Connection conn, int orderId) throws SQLException {
		
		PreparedStatement checkShippment = conn.prepareStatement(
					"select ShipmentDate from ProductOrder where OrderId = ?");
			
		checkShippment.setInt(1, orderId);
		ResultSet rs = checkShippment.executeQuery();
			
		if (rs.next()) {
			Date date = rs.getDate(1);
			if(date != null)System.out.printf("Order %d has shipped, time: %s \n",orderId, date);
			else System.out.printf("Order %d pending, not shipped.\n", orderId);
		}			
	}
}
