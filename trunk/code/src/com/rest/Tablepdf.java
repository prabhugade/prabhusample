package com.rest;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Tablepdf
{
	public static void main(String args[])throws Exception
	{
		Tablepdf pdf=new Tablepdf();
		InputStream stream=pdf.downloadFile("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/hydhouse_new3", "root", "silica", "0.9539217351764253","kiosk");
		ByteArrayInputStream input = null;
		FileOutputStream output = null;

		try 
		{
			output = new FileOutputStream("/home/prabhu/test2.pdf");

			byte[] buffer = new byte[1024];
			for (int length = 0; (length = stream.read(buffer)) > 0;) 
			{
				output.write(buffer, 0, length);
			}
		} finally 
		{
			if (output != null) try { output.close(); } catch (IOException ignore) {}
			if (input != null) try { input.close(); } catch (IOException ignore) {}
		}
		System.out.println("success");
	   
	}
	public InputStream tableTitlePDF(String rest,String address,String phone,String date,String custname,String custphone,String items,String quty,String pricedata,String discounts,String taxdeatils,String rest1,String address1,String phone1)throws Exception
	{
		String[] charges={"Vat","ServiceTax","ServiceCharge","PackageingCharge","DelivaryCharge","Discount","FinalAmount"};
		Rectangle pageSize = new Rectangle(200, 500);
		pageSize.setLeft(0);
		Document document = new Document(pageSize,0,0,0,0);
		//Document document=new Document();
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document,outstream);
		document.open();
		//for header
		Font hfont=new Font(Font.TIMES_ROMAN, 15, Font.BOLD, Color.BLACK);
		Paragraph para=new Paragraph(rest,hfont);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		//for address number
		Paragraph phonpara=new Paragraph(address);
		phonpara.setAlignment(Element.ALIGN_CENTER);
		document.add(phonpara);
		//for phone number
		phonpara=new Paragraph(phone);
		phonpara.setAlignment(Element.ALIGN_CENTER);
		document.add(phonpara);
		document.add(new Paragraph(" "));
		//for name
		PdfPTable table=new PdfPTable(2);
		table.setWidthPercentage(95);
		table.addCell(nameKeyCells("CustomerName"));
		table.addCell(nameValueCells(custname));
		table.addCell(nameKeyCells("Mobile number"));
		table.addCell(nameValueCells(custphone));
		table.addCell(nameKeyCells("Date"));
		table.addCell(nameValueCells(date));
		document.add(table);

		//for item details
		float[] columnWidths = new float[] {90f, 60f, 50f};
		PdfPTable table1=new PdfPTable(columnWidths);
		table1.setWidthPercentage(95);
		table1.addCell(ItemsHeader("Item Names"));
		table1.addCell(ItemsHeader("Quantity"));
		table1.addCell(ItemsHeader("Amount"));
		String[] item=items.replace("_$_",",").split(",");
		String[] qutys=quty.replace("_$_",",").split(",");
		String[] amount=pricedata.replace("_$_",",").split(",");
		String[] taxs=taxdeatils.replace("_$_",",").split(",");
		Double totamt=0.0;
		double qty=0;
		for(int i=0;i<item.length;i++)
		{
			table1.addCell(ItemsCell(item[i]));
			table1.addCell(ItemsCell(qutys[i]));
			qty+=Double.parseDouble(qutys[i]);
			table1.addCell(ItemsCell(amount[i]));
			totamt+=Double.parseDouble(amount[i]);
		}
		table1.addCell(TotalAmtCell("Total Amount"));
		table1.addCell(TotalAmtCell(qty+""));
		table1.addCell(TotalAmtCell(totamt+""));
		boolean check=false;
		for(int i=0;i<charges.length-2;i++)
		{
			if(Double.parseDouble(taxs[i])>0)
			{
				check=true;
				table1.addCell(nameKeyCells(charges[i]));
				totamt+=Double.parseDouble(taxs[i]);
				table1.addCell(nameKeyCells(""));
				table1.addCell(nameKeyCells(taxs[i]));
			}
		}
		if(Double.parseDouble(discounts)>0)
		{
			check=true;
			table1.addCell(nameKeyCells(charges[5]));
			totamt-=Double.parseDouble(discounts);
			table1.addCell(nameKeyCells(""));
			table1.addCell(nameKeyCells(discounts));
		}
		if(check)
		{
			table1.addCell(FianlAmtCell(charges[6]));
			table1.addCell(FianlAmtCell(""));
			table1.addCell(FianlAmtCell(totamt+""));
		}
		document.add(table1);
		document.add(new Paragraph(" "));
		if(!rest.equalsIgnoreCase(rest1))
		{
			//for header
			Font resthfont=new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);
			Paragraph restpara=new Paragraph("From:",resthfont);
			restpara.setAlignment(Element.ALIGN_LEFT);
			document.add(restpara);
			restpara=new Paragraph(rest1,resthfont);
			restpara.setAlignment(Element.ALIGN_LEFT);
			document.add(restpara);
			//for address number
			Paragraph restphonpara=new Paragraph(address1);
			restphonpara.setAlignment(Element.ALIGN_LEFT);
			document.add(restphonpara);
			//phone number
			restphonpara=new Paragraph(phone1);
			restphonpara.setAlignment(Element.ALIGN_LEFT);
			document.add(restphonpara);
			document.add(new Paragraph(" "));
		}
		
		document.close();
        byte[] by=outstream.toByteArray();
        InputStream stream=new ByteArrayInputStream(by);
        
        return stream;
	}
	public PdfPCell nameKeyCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell nameValueCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsHeader(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.WHITE));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.GRAY);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsCell(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell TotalAmtCell(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.WHITE));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.GRAY);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell FianlAmtCell(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.WHITE));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.GRAY);
		cell.setBorder(0);
		return cell;
	}
	public Font getCellFont(Color color)
	{
		return new Font(Font.TIMES_ROMAN, 10, Font.BOLD, color);
	}
	public InputStream downloadFile(String driver,String url,String user,String pwd,String reservedid,String type)
	{
		boolean errcheck=false;
		InputStream istream=null;
		Connection con=null;
		Statement st3=null,st1=null,st2=null;
		ResultSet rs1,rs2;
		String errorlog="start";
		String errorlogfinal="";
		try
		{
			con=connectionOpen(driver, url, user, pwd);
			if(con!=null)
			{
				String Restaurantname=null;
				String Restaurantaddress=null;
				String Restaurantph=null;
				String Restaurantname1=null;
				String Restaurantaddress1=null;
				String Restaurantph1=null;
				String custname=null;
				String mobile=null;
				String billdate=null;
				String discounts=null;

				String itemname=null;
				String itemquty=null;
				String itemprice=null;
				String Taxs=null;

				st3=con.createStatement();
				st1=con.createStatement();
				st2=con.createStatement();
				String sql="";
				if(type.equalsIgnoreCase("kiosk")||type.equalsIgnoreCase("callcenter"))
					sql="SELECT rest.restaurant_name,rest.address,rest.phone,o.DateTime as date,c.CustmerName,c.Mobile,o.`ItemId`, o.`Quantity`, o.`Amount` ,o.Discount,o.TaxAmts,o.KioskId FROM `Orders` as o join Customer as c join `Restaurant_details` as rest on rest.rest_id=o.rest_id AND o.CustmerId=c.CustmerId where o.OrderId='"+reservedid+"'";
				else
					sql="SELECT rest.restaurant_name,rest.address,rest.phone,tr.EndTime as date,c.CustmerName,c.Mobile,do.`ItemId`, do.`Quantity`, do.`Amount` ,tr.Discount,tr.TaxAmts FROM `DiningOrders` as do join TableReservation as tr join Customer as c join `Restaurant_details` as rest on rest.rest_id=tr.rest_id AND do.`ReserveId`=tr.`ReserveId` AND tr.CustomerId=c.CustmerId where do.ReserveId='"+reservedid+"'";
				errorlog=sql;
				rs1=st1.executeQuery(sql);
				if(rs1.next())
				{
					String callcenter="";
					if(type.equalsIgnoreCase("kiosk")||type.equalsIgnoreCase("callcenter"))
					{
						callcenter=rs1.getString("KioskId");
						if(callcenter.startsWith("cc"))
						{
							ResultSet rest=st3.executeQuery("SELECT r.rest_id,rd.restaurant_name,rd.address,phone FROM `RestaurantDashboard` as r join Restaurant_details as rd on r.rest_id=rd.rest_id");
							if(rest.next())
							{
								Restaurantname=rest.getString("restaurant_name");
								Restaurantaddress=rest.getString("address");
								Restaurantph=rest.getString("phone");
							}
						}
					}
					Restaurantname1=rs1.getString("restaurant_name");
					Restaurantaddress1=rs1.getString("address");
					Restaurantph1=rs1.getString("phone");
					if(Restaurantname==null||Restaurantname.equalsIgnoreCase(Restaurantname1))
					{
						Restaurantname=Restaurantname1;
						Restaurantaddress=Restaurantaddress1;
						Restaurantph=Restaurantph1;
					}
					custname=rs1.getString("CustmerName");
					mobile=rs1.getString("Mobile");
					billdate=rs1.getString("date");
					Taxs=rs1.getString("TaxAmts");
					discounts=rs1.getString("Discount");
					itemquty=rs1.getString("Quantity");
					itemprice=rs1.getString("Amount");
					String  itemids=rs1.getString("ItemId");
					String  i1=itemids.replace("_$_", "','");
					String sql2="SELECT `ItemId`,`ItemName` FROM `ItemDetails` WHERE `ItemId` IN ('"+i1+"')";
					errorlog=sql2;
					rs2=st2.executeQuery(sql2);
					itemids="_$_"+itemids+"_$_";
					while(rs2.next())
					{
						itemids=itemids.replace("_"+rs2.getString("ItemId")+"_","_"+rs2.getString("ItemName")+"_");
					}
					itemids=itemids.substring(3,itemids.length()-3);
					itemname=itemids;
					istream=tableTitlePDF(Restaurantname,Restaurantaddress,"Phone:"+Restaurantph,billdate, custname,mobile,itemname, itemquty,itemprice, discounts, Taxs,Restaurantname1,Restaurantaddress1,"Phone:"+Restaurantph1);
				}
			}
			else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch (Exception e) 
		{
			errorlogfinal=errorlog+" downloadFile "+e.getMessage();
		}
		finally
		{
			try
			{
				if(!errorlogfinal.equals("")||errcheck)
				{/*
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" downloadFile ";
					new reportMethods().storeErrorLog(driver, url, user, pwd, "-", "downloadFile", errorlogfinal, "failed");
				*/}
				st2.close();
				st1.close();
				st3.close();
				con.close();
			} 
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		}
		return istream;
	}
	public Connection connectionOpen(String driver,String url,String user,String pwd)
	{
		Connection con=null;
		try
		{		
			if(con==null)
			{
				Class.forName(driver).newInstance();
				con= DriverManager.getConnection(url,user,pwd);
			}
		}catch(Exception e)
		{
			//System.out.println("connectionopen()"+e);
		}
		return con;
	}
}
