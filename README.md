# 5200_OrderManager_Project_Yongliang_Tan_and_Yuzhou_Wu

## Brief Introduction

This is a CS5200 database management project created by Yongliang Tan and Yuzhou Wu using Apache Derby.

The Java Files folder contains all the files to run this project. **First run OrderManager.java, then run Test.java.**

The testing text files used are located at the "Testing TXT" folder. The result will show in IDE's console.

The OrderManager.pptx and OrderManager.pdf are slides for this project.

This project used Takahiko Kawasaki's CountryCode enum class.
For JavaDoc about CountryCode enum class, please visit: http://takahikokawasaki.github.io/nv-i18n/

The remaining JavaDoc could be found in https://pages.github.ccs.neu.edu/seantanty/cs5200_OrderManager_Project_Yongliang_Tan_and_Yuzhou_Wu/

## Design
### Requirements
#### Product
SKU is a 12-character value of the form AA-NNNNNN-CC where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an upper case letter. For example, “AB-123456-0N”.

#### InventoryRecord
Price per unit for the current inventory is a positive number with 2 digits after the decimal place

#### Customer
Payment information is not part of this database.

#### Order 
1. Shipment date in date format, null means not shipped yet
2. All items must be available in a single transaction to place an order*(We have a different approach)

#### OrderRecord
The item must be available and the inventory is automatically reduced when an order record is created for an order*(We have a different approach)

### Decisions and assumptions
#### Table Relations

![table relations](https://media.github.ccs.neu.edu/user/4294/files/3cab4a00-9705-11e9-9bbd-b2adf50c629d)

#### Data type and stored functions
For details about stored functions' codes, please refer to the OrderManager slides in this repository.
##### Product
![product table](https://media.github.ccs.neu.edu/user/4294/files/a46e8e80-971f-11e9-8bfc-172648fc00dd)

The create table statement will call isSKU stored function to check whether the SKU meet the format requirement.
##### InventoryRecord
![inventory table](https://media.github.ccs.neu.edu/user/4294/files/d2ec6980-971f-11e9-9d7a-b9ee132f8d42)

The create table statement will call isUnitPrice stored function to check whether the UnitPrice meet the format requirement.
##### Customer
![customer table](https://media.github.ccs.neu.edu/user/4294/files/f8797300-971f-11e9-9609-575c10040ea3)

The create table statement will call isValidStateOrNull stored function to check whether the State meet the format requirement.

The create table statement will call isValidCountry stored function to check whether the Country meet the format requirement.
##### Order
![order table](https://media.github.ccs.neu.edu/user/4294/files/252d8a80-9720-11e9-8ae5-9037eea548f5)

##### OrderRecord
![orderrecord table](https://media.github.ccs.neu.edu/user/4294/files/3a0a1e00-9720-11e9-977b-82b629db471d)

The create table statement will call isUnitPrice stored function to check whether the UnitPrice meet the format requirement.

#### Triggers and stored Procedure
We total have three triggers and one stored procedure in our database. These triggers and procedure could be found in OrderManager.java's main class.

1. A before insert trigger to print a message showing the OrderRecord order a product exceed its current stock

2. A after insert trigger to deduct inventory stock upon accepting OrderRecord didn't exceed its stock

3. A after delete trigger to add back the inventory stock when an OrderRecord is deleted

### Specific design issues
#### Restrictions
Even though we have delete on cascade for some referencing keys, the product, inventory and customer should not be deleted. Those entries could be obsolete or no longer useful, but we could still keep those data in our database. 

#### Order/OrderRecords
The requirement states that "All items must be available in a single transaction to place an order". However, we decided to add feilds for Order and OrderRecords table to allow incomplete orders by considering this could attract more bussiness for our client. If the client has a different opinion, we could always change this design to meet their requirement.Currently, order records which exceed current inventory stock will store in system, but that order record and the order it belongs to will be marked as incompelte.

We could also add additoinal feature to have email notification function when inventory back in stock for those incomplete orders to enhance the sale.

## Function
All method and method details can be checked in the link https://pages.github.ccs.neu.edu/seantanty/cs5200_OrderManager_Project_Yongliang_Tan_and_Yuzhou_Wu/

Below are the brief descriptions.

### Data Manipulate Language

#### addProduct: 
This function is to insert data into table 'Product'.

#### addInventory:
This function is to insert data into table 'InventoryRecord'.

#### updateInventory:
This function is to update something in the table 'InventoryRecord'. For example, if we want to change price or add number, we can call this function.

#### checkInventoryExistence:
This function is to get the inventory number for a specific SKU.
When we scan the InventoryRecord file, for each InventoryRecord, we need to first call this function.
If return value is -1, it means there is no record for this product. So we need to insert it directly.
If return value is 0 or others, it means the InventoryRecord for this SKU already exists in the table, so we need to update it.

#### addCustomer:
This function is to add Customer into table 'Customer'

#### updateCustomer:
This function is to update Customer information in the table 'Customer'

#### addOrder:
This function is to add Order into table 'ProductOrder'. We set the 'OrderDate' by using the current time. 
Before loading orderRecords, 'ShipmentDate' will be null and status will be 0 (means pending for this order).

#### addOrderRecord:
This function is to load order records into table 'OrderRecord'. 
If order number is larger than inventory number, we will set status to 0 (means fail), otherwise to 1, means this order record is successful.

#### checkOrder
This function is to get the number of successful order records for a specific order.
Then we can compare this number to the count field in the table 'ProductOrder' for each orderId.
If they are not equal, it means this order is incomplete, we will set status in table 'Product' for this orderId to -1 (incomplete).
Otherwise, it means this order is complete, we can use the current time to set the shippment date and update the status to 1 (complete).

#### CancelOrder
This function is to set status in table 'Product' for the specific orderId to -1

#### PlaceOrder
This function is to use the current time to set the shippment date and update the status to 1 (complete).

#### updateShipment
This function is to update the shipmment date if necessary (for example, sometimes shippment will be delayed due to some issues)


### Data Query Language

#### printProducts
This function is to print the product information

#### printInventoryRecord
This function is to print the Inventory Record

#### printCustomer
This function is to print the customer information

#### printOrder
This function is to print the order informtaion

#### printOrderRecord
This function is to print the order record.

#### showDescription
This function is to check the description for a specific product SKU

#### showCustomerInformation
This function is to show customer information for a specific customer id

#### checkShippment
This function is to check the shippment date for a specific order id

## Test
Our test files are in the repository, you can check it. Now we will show the test result.

### Before processing orderRecords
    Product:
    SKU		        Name		   Description
    AA-000000-0A	iPhone XR	 All-screen design...
    AA-000000-0B	iPhone XS	 super Retina in two sizes...
    AA-000000-0C	iphone 8	 No description
    AA-000000-0D	iphone 7	 No description
    AA-000000-0E	iphone 6	 No description
    CN-000000-0A	HUAWEI P9	 Chinese brand
    CN-000000-0B	Xiaomi 10	 Another Chinese brand
    Product count: 7
--------------------------------------------------------------------------------------------------
    InventoryRecord:
    sku		Number	unitprice
    AA-000000-0A	100	749.000000
    AA-000000-0B	100	999.000000
    AA-000000-0C	100	599.000000
    AA-000000-0D	100	449.000000
    AA-000000-0E	200	399.000000
    CN-000000-0A	200	399.250000
    CN-000000-0B	400	300.050000
    Inventory count: 7
--------------------------------------------------------------------------------------------------
    Customer:
    FirstName	LastName	Address			City		State	Country		PostalCode	CustomerId
    LINYI		GAO		5805 Charlotte Dr	San Jose	CA	United States	95123		4
    SEAN		TAN		5805 Charlotte Dr	San Jose	CA	United States	95123		2
    YUZHOU		WU		5805 Charlotte Dr	San Jose	CA	United States	95123		1
    ZHONG		ZHUANG		5805 Charlotte Dr	San Jose	CA	United States	95123		3
    Customer count: 4
--------------------------------------------------------------------------------------------------
    Order:
    CustomerId	OrderId		OrderDate	ShipmentDate		Status
    4		8		2019-06-24	null		Pending
    4		7		2019-06-24	null		Pending
    4		6		2019-06-24	null		Pending
    4		5		2019-06-24	null		Pending
    3		4		2019-06-24	null		Pending
    2		3		2019-06-24	null		Pending
    1		2		2019-06-24	null		Pending
    1		1		2019-06-24	null		Pending
    Order count: 8

We can see that inventory number is initialized in the table 'InventoryRecord'
In the Order table, the shipment date is null and status is pending for all order id.
Now we begin to load order records.

### After processing orderRecords
--------------------------------------------------------------------------------------------------
    Product:
    SKU		Name		Description
    AA-000000-0A	iPhone XR	All-screen design...
    AA-000000-0B	iPhone XS	super Retina in two sizes...
    AA-000000-0C	iphone 8	No description
    AA-000000-0D	iphone 7	No description
    AA-000000-0E	iphone 6	No description
    CN-000000-0A	HUAWEI P9	Chinese brand
    CN-000000-0B	Xiaomi 10	Another Chinese brand
    Product count: 7
--------------------------------------------------------------------------------------------------
    InventoryRecord:
    sku		Number	unitprice
    AA-000000-0A	100	749.000000
    AA-000000-0B	0	999.000000
    AA-000000-0C	7	599.000000
    AA-000000-0D	79	449.000000
    AA-000000-0E	168	399.000000
    CN-000000-0A	0	399.250000
    CN-000000-0B	1	300.050000
    Inventory count: 7
--------------------------------------------------------------------------------------------------
    Customer:
    FirstName	LastName	Address			City		State	Country		PostalCode	CustomerId
    LINYI		GAO		5805 Charlotte Dr	San Jose	CA	United States	95123		4
    SEAN		TAN		5805 Charlotte Dr	San Jose	CA	United States	95123		2
    YUZHOU		WU		5805 Charlotte Dr	San Jose	CA	United States	95123		1
    ZHONG		ZHUANG		5805 Charlotte Dr	San Jose	CA	United States	95123		3
    Customer count: 4
--------------------------------------------------------------------------------------------------
    Order:
    CustomerId	OrderId		OrderDate	ShipmentDate		Status
    4		8		2019-06-24	2019-06-24		Complete
    4		7		2019-06-24	null		Incomplete
    4		6		2019-06-24	2019-06-24		Complete
    4		5		2019-06-24	null		Incomplete
    3		4		2019-06-24	2019-06-24		Complete
    2		3		2019-06-24	null		Incomplete
    1		2		2019-06-24	2019-06-24		Complete
    1		1		2019-06-24	2019-06-24		Complete
    Order count: 8
--------------------------------------------------------------------------------------------------
    OrderRecord:
    OrderId		SKU		Number	UnitPrice
    1		AA-000000-0C	18	599.0
    1		AA-000000-0E	32	399.0
    2		AA-000000-0C	50	599.0
    3		AA-000000-0A	101	749.0
    4		AA-000000-0B	100	999.0
    5		AA-000000-0E	200	399.0
    5		AA-000000-0C	25	559.0
    5		AA-000000-0D	21	449.0
    6		CN-000000-0A	200	399.25
    7		CN-000000-0A	1	399.25
    8		CN-000000-0B	399	300.05
    OrderRecord count: 11

We can see that after processing order records, the inventory number has reduced. 
For complete orders, it has shipped while for incomplete orders, its status is incomplete and shipment date is still null.

## Future: Issues and Improvements
1. Order and OrderRecords table
We are currently using status feild for both table to determine if the Order/OrderRecords have been fulfilled or not. Our design is apart from the requirement. However, we do believe this is a better way to attract bussiness. We allow the customers to place their orders first.

2. Update Order/OrderRecords
We didn't implement update functions for Order and OrderRecords. We believe it would be better to let customers cancel the whole order then place a new one. With our personal experience using Ebay, Amazon and Taobao(a Chinese e commerece website belong to Alibaba), any change to an order with previous mistake has a very high chance of still having potential errors. 

3. Back ordering items:
We could have a future improvement for UpdateInventory functionality. Currently, the incomplete Order need to be manually updated. The database would be easier to use if the incomplete OrderRecords will be automatically fulfill and updated to complete status when the inventory stock has been added to the database.

4. Convert to MySQL
We are considering convert the whole project to using MySQL instead of Derby in the future. There are many limitations Debry have. One of them is that the trigger only allow one sql statement. In addition, MySQL is an overall more widely used database comparing to Derby. Converting Derby database to a MySQL one is also a great learning experience for us.
