/**
 * Java file OrderManger.java for OrderManager CS5200 project.
 * @author Yongliang Tan, Yuzhou Wu
 * 
 * This class provide DDL functions, including create the tables, stored functions/procedures,
 * and triggers.
 * 
 */


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.*;


public class OrderManager {
	
	
	//below are supporting functions for DDL
	/**
	 * Determines whether SKU is a valid SKU. Use in stored function
	 * 
	 * @param sku The SKU to be determined
	 * @return true If SKU is a valid SKU
	 */
	public static boolean isSKU(String sku) {
		//The SKU is a 12-character value of the form AA-NNNNNN-CC 
		//where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an upper-case letter. 
		//For example, "AB-123456-0N".
		return (sku != null) && sku.matches("([A-Z]{2})-(\\d{6})-([A-Z0-9]{2})$");
	}
	
	/**
	 * Determines whether unitPrice is a valid unitPrice. Use in stored function
	 * 
	 * @param unitPrice The unitPrice to be determined
	 * @return true If unitPrice is a valid unitPrice
	 */
	public static boolean isUnitPrice(BigDecimal unitPrice) {
		//The UnitPrice is a positive number with 2 digits after the decimal place
		//boolean fail = (BigDecimal.valueOf(unitPrice).scale() > 2);
		//return (unitPrice > 0) && fail;
		boolean fail = unitPrice.scale() > 2;
		return (unitPrice.doubleValue() > 0.0) && (!fail);
	}
	
	/**
	 * The state enum created to support isValidStateOrNull function
	 */
	enum State{
		AL,AK,AZ,AR,CA,CO,CT,DE,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,MT,NE
		,NV,NH,NJ,NM,NY,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WA,WV,WI,WY,DC;
	}
	
	/**
	 * Determines whether state is a valid state or null. Use in stored function
	 * 
	 * @param state The state to be determined
	 * @return true If state is a valid state or null
	 */
	public static boolean isValidStateOrNull(String state) {
		if (state == null) {
			return true;
		} else {
			for(State s: State.values()) {
				if(s.name().equals(state)) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	/**
	 * Determines whether country is a valid country name using CountryCode enum. Use in stored function
	 * 
	 * @param country The country to be determined
	 * @return true If country is a valid country name
	 */
	public static boolean isValidCountry(String country) {
		for(CountryCode c: CountryCode.values()) {
			if(c.getName().equals(country)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This function throws SQL exception on calling.  Use in stored procedure
	 * 
	 * @param SKU The SKU that is not sufficient to fulfill order
	 * @param OrderId The Order ID that has certain OrderRecord exceed inventory stock
	 */
	public static void checkInventory(String SKU, int OrderId){
		System.err.printf("OrderID %d, %s is out of store\n", OrderId, SKU);
	} 
	
	
	
	/**
	 * Main function to create stored functions/procedures, tables, and triggers
	 * @param args Empty
	 */
	public static void main(String[] args) {
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "OrderManager";
		String connStr = protocol + dbName+ ";create=true";
		
	    // tables created by this program
		String OrderManagerTables[] = { 
				"OrderRecord", "ProductOrder","Customer","InventoryRecord", "Product"
			};
		// functions created by this program
		String OrderManagerStoredFunctions[] = {
				"isSKU", "isUnitPrice", "isValidStateOrNull", "isValidCountry"
			};
		
		//
		String StoredProcedure[]= {
				"checkInventory"	
		};
		
		
		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derby client frameworks
        props.put("user", "user1");
        props.put("password", "user1");
        
		try (
		        // connect to the database using URL
				Connection conn = DriverManager.getConnection(connStr, props);
					
		        // statement is channel for sending commands thru connection 
		        Statement stmt = conn.createStatement();
			){
		        System.out.println("Connected to and created database " + dbName);
	            
	            // drop the database triggers and recreate them below

	            // drop the database tables and recreate them below
	            for (String tbl : OrderManagerTables) {
		            try {
		            		stmt.executeUpdate("drop table " + tbl);
		            		System.out.println("Dropped table " + tbl);
		            } catch (SQLException ex) {
		            		System.out.println("Did not drop table " + tbl);
		            }
	            }
		        // drop the function and recreate them below
	            for (String func : OrderManagerStoredFunctions) {
		            try {
		            		stmt.executeUpdate("drop function " + func);
		            		System.out.println("Dropped function " + func);
		            } catch (SQLException ex) {
		            		System.out.println("Did not drop function " + func);
		            }
	            }
	         // drop the function and recreate them below
	            for (String func : StoredProcedure) {
		            try {
		            		stmt.executeUpdate("drop procedure " + func);
		            		System.out.println("Dropped function " + func);
		            } catch (SQLException ex) {
		            		System.out.println("Did not drop function " + func);
		            }
	            }
	            


	            /**
	             * create stored function to support DDL
	             */
	            //create stored function isSKU
	            String createFunction_isSKU =
	            	 "CREATE FUNCTION isSKU("
	            	+ " 	SKU varchar(12)"
	            	+ "	)  RETURNS BOOLEAN"
	            	+ " PARAMETER STYLE JAVA"
	            	+ " LANGUAGE JAVA"
	            	+ " DETERMINISTIC"
	          	    + " NO SQL"
	          	    + " EXTERNAL NAME"
	          	    + "		'OrderManager.isSKU'";
	            stmt.executeUpdate(createFunction_isSKU);
	            System.out.println("Created stored function isSKU");
	            
	            //create stored function isUnitPrice
	            String createFunction_isUnitPrice =
		            	 "CREATE FUNCTION isUnitPrice("
		            + " 	UnitPrice decimal(18,2)"
		            + "	)  RETURNS BOOLEAN"
		            + " PARAMETER STYLE JAVA"
		           	+ " LANGUAGE JAVA"
		           	+ " DETERMINISTIC"
		            + " NO SQL"
		         	+ " EXTERNAL NAME"
		         	+ "		'OrderManager.isUnitPrice'";
		        stmt.executeUpdate(createFunction_isUnitPrice);
		        System.out.println("Created stored function isUnitPrice");
	            
		        //create stored function isValidStateOrNull
	            String createFunction_isValidStateOrNull =
		            	 "CREATE FUNCTION isValidStateOrNull("
		            + " 	State varchar(32)"
		            + "	)  RETURNS BOOLEAN"
		            + " PARAMETER STYLE JAVA"
		           	+ " LANGUAGE JAVA"
		           	+ " DETERMINISTIC"
		            + " NO SQL"
		         	+ " EXTERNAL NAME"
		         	+ "		'OrderManager.isValidStateOrNull'";
		        stmt.executeUpdate(createFunction_isValidStateOrNull);
		        System.out.println("Created stored function isValidStateOrNull");
		        
		        //create stored function isValidCountry
	            String createFunction_isValidCountry =
		            	 "CREATE FUNCTION isValidCountry("
		            + " 	Country varchar(32)"
		            + "	)  RETURNS BOOLEAN"
		            + " PARAMETER STYLE JAVA"
		           	+ " LANGUAGE JAVA"
		           	+ " DETERMINISTIC"
		            + " NO SQL"
		         	+ " EXTERNAL NAME"
		         	+ "		'OrderManager.isValidCountry'";
		        stmt.executeUpdate(createFunction_isValidCountry);
		        System.out.println("Created stored function isValidCountry");
		        
		        
		        //create stored procedure checkInventory
		        String createProcedure_checkInventory =
		            	 "CREATE Procedure checkInventory("
		            + " 	SKU varchar(12),"
		            + "		OrderId int"
		            + "	)  "
		            + " PARAMETER STYLE JAVA"
		           	+ " LANGUAGE JAVA"
		           	+ " DETERMINISTIC"
		            + " NO SQL"
		         	+ " EXTERNAL NAME"
		         	+ "		'OrderManager.checkInventory'";
		        stmt.executeUpdate(createProcedure_checkInventory);
		        System.out.println("Created stored function checkInventory");
	            
	            
	            
	            /**
	             * create tables below
	             */
	            // create Product table
		        String createTable_Product = "create table Product ("
		        		+ "	Name varchar(32) not null,"
		        		+ "	Description varchar(512) not null,"
		        		+ "	SKU varchar(12) not null,"
		        		+ "	primary key(SKU),"
		        		+ "	check (isSKU(SKU))"//use stored function isSKU to check SKU format
		        		+ ")";
	            stmt.executeUpdate(createTable_Product);
	            System.out.println("Created table Product");
	            
	            //create InventoryRecord table
	            String createTable_InventoryRecord = "create table InventoryRecord ("
	            		+ " Number int not null,"
	            		+ " check (Number >= 0),"
	            		+ "	UnitPrice decimal(18,2) not null,"
	            		+ " SKU varchar(12) not null,"
	            		+ " primary key (SKU),"
	            		+ "	foreign key (SKU) references Product(SKU) on delete cascade,"
	            		+ "	check (isUnitPrice(UnitPrice))"//use stored function isUnitPrice to check UnitPrice format
	            		+ ")";
	            stmt.executeUpdate(createTable_InventoryRecord);
	            System.out.println("Created table InventoryRecord");
	            
	            //create Customer table
	            String createTable_Customer = "create table Customer ("
	            		//+ " Name varchar(32) not null,"
	            		//i think changing this to first name and last name is better, and won't hurt the project anyway
	            		//how's your opinion?
	            		+ " FirstName varchar(32) not null,"
	            		+ " LastName varchar(32) not null,"
	            		+ " Address varchar(128) not null,"
	            		+ " City varchar(32) not null,"
	            		+ " State varchar(32),"
	            		+ " check (isValidStateOrNull(State)),"
	            		+ " Country varchar(32) not null,"
	            		+ " check (isValidCountry(Country)),"
	            		+ " PostalCode int not null,"
	            		+ " CustomerId int not null,"
	            		+ " check (CustomerId > 0),"
	            		+ " primary key (CustomerId)"
	            		+ ")";
	            stmt.executeUpdate(createTable_Customer);
	            System.out.println("Created table Customer");
	            
	            //create Order table 
	            String createTable_Order = "create table ProductOrder ("
	            		+ " CustomerId int not null,"
	            		+ " OrderId int not null,"
	            		+ " OrderDate date not null,"
	            		+ " ShipmentDate date,"
	            		+ " Status int default 0,"// 0 pending, -1 incomplete, 1 complete
	            		+ " RecordCount int not null," 
	            		+ " primary key (OrderId),"
	            		+ " foreign key (CustomerId) references Customer(CustomerId) on delete cascade"
	            		+ ")";
	            stmt.executeUpdate(createTable_Order);
	            System.out.println("Created table Order");
	            
	            //create OrderRecord table
	            String createTable_OrderRecord = "create table OrderRecord ("
	            		+ " OrderId int not null,"
	            		+ " SKU varchar(12) not null,"
	            		+ " Number int not null,"
	            		+ " check (Number > 0),"
	            		+ " UnitPrice decimal(18,2) not null,"
	            		+ " Status int not null default 0,"
	            		+ "	check (isUnitPrice(UnitPrice)),"
	            		+ " primary key (OrderId, SKU),"
	            		+ " foreign key (OrderId) references ProductOrder(OrderId) on delete cascade,"
	            		+ "	foreign key (SKU) references Product(SKU) on delete cascade"
	            		+ ")";
	            stmt.executeUpdate(createTable_OrderRecord);
	            System.out.println("Created table OrderRecord");
	                       
	            
	            
	            /**
	             * create triggers below
	             */
	            //check OrderRecord's number doesn't exceed inventory stock
	            String createTrigger_checkInventory =
	            		  "create trigger checkInventory"
	            		+ " no cascade before insert on OrderRecord"
	            		+ " referencing new as insertedOrderRecord"
	            		+ " for each row mode db2sql"
	            		// need to use **no cascade** before insert
	            		+ " when (insertedOrderRecord.Number > "
	            		+ " (select InventoryRecord.Number from InventoryRecord where insertedOrderRecord.SKU = InventoryRecord.SKU))"
	            		+ " call checkInventory(insertedOrderRecord.SKU, insertedOrderRecord.OrderId)";
	            stmt.executeUpdate(createTrigger_checkInventory);
	            System.out.println("Created trigger for checkInventory");
	            
	            
	            //adding number in OrderRecord will decrease the respecting inventory stock number
	            String createTrigger_DecreaseInventoryAfterAddOrderRecord =
	            		  "create trigger DecreaseInventoryAfterAddOrderRecord"
	            		+ " after insert on OrderRecord"
	            		+ " referencing new as insertedOrderRecord"
	            		+ " for each row mode db2sql"
	            		+ " when (insertedOrderRecord.Number <= "
	            		+ " (select InventoryRecord.Number from InventoryRecord where insertedOrderRecord.SKU = InventoryRecord.SKU))"
	            		+ " Update InventoryRecord "
	            		+ " SET Number = Number - insertedOrderRecord.Number where insertedOrderRecord.SKU = InventoryRecord.SKU";
	            stmt.executeUpdate(createTrigger_DecreaseInventoryAfterAddOrderRecord);
	            System.out.println("Created trigger for decreasing inventory after adding OrderRecord");
	            
	            
	            //create trigger for deleting OrderRecord
	            String createTrigger_DeleteOrderRecord =
	            		  "create trigger DeleteOrderRecord"
	            		+ " after delete on OrderRecord"
	            		+ " referencing old as deletedOrderRecord"
	            		+ " for each row mode db2sql"
	            		+ " Update InventoryRecord "
	            		+ " SET Number = Number + deletedOrderRecord.Number where InventoryRecord.SKU = deletedOrderRecord.SKU";
	            stmt.executeUpdate(createTrigger_DeleteOrderRecord);
	            System.out.println("Created trigger for deleting OrderRecord");
	            
     
			} catch (SQLException e) {
				e.printStackTrace();
			}	
	}
}