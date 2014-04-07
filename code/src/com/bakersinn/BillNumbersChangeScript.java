package com.bakersinn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.agrasweets.DatabaseConnections;

public class BillNumbersChangeScript {

	public static void main(String[] args) 
	{
		new BillNumbersChangeScript().billnumbersChanges();
	}
	public void billnumbersChanges()
	{
		String errorlog="start";
		String errorlogfinal="";
		Connection con=null;
		Statement st=null;
		PreparedStatement ps=null;
		try
		{
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				FileWriter filewriter=null;
				BufferedWriter bufwriter=null;
				filewriter=new FileWriter("/home/prabhu/Desktop/database/bakersinn/billnumberchange2014-04-05.sql",true);
				bufwriter=new BufferedWriter(filewriter);
				//For orders
				String sql="SELECT distinct o.KioskId,KioskName,InvoiceCode FROM `Orders` o join Kiosk as k on k.`KioskId`=o.`KioskId`";
				st=con.createStatement();
				ResultSet rs=st.executeQuery(sql);
				int q=1;
				String invoicecode="",kioskid;
				while(rs.next())
				{
					q=1;
					bufwriter.write("/*kioskid="+rs.getString("KioskId")+" kioskname="+rs.getString("KioskName")+" InvoiceCode="+rs.getString("InvoiceCode")+"*/\n");
					bufwriter.newLine();
					invoicecode=rs.getString("InvoiceCode").length()>0?rs.getString("InvoiceCode").replace("\\", "\\\\")+"\\\\":"";
					//invoicecode=rs.getString("InvoiceCode").replace("\\", "\\\\");
					kioskid=rs.getString("KioskId");
					//bufwriter.write("INSERT INTO `Orders`(`OrderId`,`CustmerId`, `ItemId`, `Quantity`, `Amount`,`ItemDescription`,`DateTime`, `Status`,`EmployeeId`,`OrderStatus`,`Discount`,`TaxAmts`,`PaymentType`,`KioskId`,`AreaName`,`Pin`,`rest_id`,`DeliveryQty`,`ordernumber`,`OrderedAddress`,`TaxId`,`BasePrices`,`DeliveryCharge`,`PriceTypeId`,`BillNumber`,`OrderPlaceDateTime`,`PaymentEmployeeID`,CustomerDetails,`AdjustedAmount`,DiscountPercent) values('"+p+"','0','0','0','0','0','2014-04-01 00:00:00','T','0','0','0','{}','[]','"+kioskid+"','-','-','0','0','0','-','0','0','0-0','1','"+invoicecode+"0','2014-04-00 00:00:00','0','-','0','0%');");
					//bufwriter.newLine();
					ps=con.prepareStatement("SELECT `OrderId`,`BillNumber` FROM `Orders` WHERE KioskId = '"+kioskid+"' AND DateTime > '2014-04-01 00:00:00' ORDER BY `OrderPlaceDateTime` ASC");
					ResultSet reset=ps.executeQuery();
					while(reset.next())
					{
						bufwriter.write("UPDATE `Orders` SET `BillNumber`='"+invoicecode+(q)+"' WHERE `OrderId`='"+reset.getString("OrderId")+"';");
						bufwriter.newLine();
						q++;
					}
				}
				//For invoice
				sql="SELECT distinct KioskId,KioskName,InvoiceCode FROM `BatchInvoiceDetails` b join Kiosk as k on k.`KioskId`=b.`SourceId`";
				st=con.createStatement();
				rs=st.executeQuery(sql);
				q=1;
				while(rs.next())
				{
					q=1;
					bufwriter.write("/*kitchenid="+rs.getString("KioskId")+" kitchenname="+rs.getString("KioskName")+" InvoiceCode="+rs.getString("InvoiceCode")+"*/\n");
					bufwriter.newLine();
					invoicecode=rs.getString("InvoiceCode").length()>0?rs.getString("InvoiceCode").replace("\\", "\\\\")+"\\\\":"";
					//invoicecode=rs.getString("InvoiceCode").replace("\\", "\\\\");
					kioskid=rs.getString("KioskId");
					//bufwriter.write("INSERT INTO `Orders`(`OrderId`,`CustmerId`, `ItemId`, `Quantity`, `Amount`,`ItemDescription`,`DateTime`, `Status`,`EmployeeId`,`OrderStatus`,`Discount`,`TaxAmts`,`PaymentType`,`KioskId`,`AreaName`,`Pin`,`rest_id`,`DeliveryQty`,`ordernumber`,`OrderedAddress`,`TaxId`,`BasePrices`,`DeliveryCharge`,`PriceTypeId`,`BillNumber`,`OrderPlaceDateTime`,`PaymentEmployeeID`,CustomerDetails,`AdjustedAmount`,DiscountPercent) values('"+p+"','0','0','0','0','0','2014-04-01 00:00:00','T','0','0','0','{}','[]','"+kioskid+"','-','-','0','0','0','-','0','0','0-0','1','"+invoicecode+"0','2014-04-00 00:00:00','0','-','0','0%');");
					//bufwriter.newLine();
					ps=con.prepareStatement("SELECT `BatchInvoiceDetailsId` FROM `BatchInvoiceDetails` WHERE `SourceId`='"+kioskid+"' and  DateTime > '2014-04-01 00:00:00' ORDER BY DateTime ASC");
					ResultSet reset=ps.executeQuery();
					while(reset.next())
					{
						bufwriter.write("UPDATE `BatchInvoiceDetails` SET `BatchInvoiceId`='"+invoicecode+(q)+"' WHERE `BatchInvoiceDetailsId`='"+reset.getString("BatchInvoiceDetailsId")+"';");
						bufwriter.newLine();
						q++;
					}
				}
				bufwriter.close();
				System.out.println("successfully completed");
			}else
			{
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" billnumbersChanges "+e.toString();
			try
			{
				//con.rollback();
			}catch (Exception e1) {
			}
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();				
				if(!errorlogfinal.equals(""))
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" billnumbersChanges ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("billnumbersChanges"+e.toString());
			}
		}
	}

}
