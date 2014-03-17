package com.agrasweets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TimeZone;

public class DataCleaning {
	TimeZone tz=null;	
	public DataCleaning()
	{
		tz=TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
	}
	public static void main(String args[])
	{
		new DataCleaning().cleanData();
	}
	public void cleanData()
	{
		File errDir=new File("/home/prabhu/agclean/datacleaning process");
		errDir.mkdirs();
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		Connection con=null;
		Statement st=null,st1=null;
		try
		{
			FileWriter filewriter=null;
			BufferedWriter bufwriter=null;
			String filepath=errDir.getParent();
			//filewriter=new FileWriter(filepath+"/errors.txt",true);
			//bufwriter=new BufferedWriter(filewriter);
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				//bufwriter.write("start");
				//bufwriter.newLine();
				st=con.createStatement();
				st1=con.createStatement();
				con.setAutoCommit(false);
				String sql="SELECT min(`DateTime`) as datetime FROM `Inventory1` WHERE `kitchenid`='kitchen' group by substr(`DateTime`,1,10)";
				String previousdate="2013-01-01 00:00:01";
				String presentdate;
				ResultSet rsst1=st1.executeQuery(sql);
				boolean check=true;
				while(rsst1.next()&&check)
				{
					presentdate=rsst1.getString("datetime");
					filewriter=new FileWriter(filepath+"/"+presentdate+"_errors.txt",true);
					bufwriter=new BufferedWriter(filewriter);
					String result=AutoTransfor(previousdate, presentdate,bufwriter);
					if(result.equalsIgnoreCase("success"))
					{
						sql="INSERT INTO `Inventory`(`InventoryId`, `ItemId`, `quantity`, `DateTime`, `EmployeeId`, `kitchenid`) SELECT d.`DistributionId`, d.`ItemId`, d.`quantity`, d.`DateTime`, d.`EmployeeId`, d.`TakenFrom` FROM `IndentInfo` as i join Distribution1 as d on i.DistributionId=d.DistributionId  WHERE d.`DateTime` like '"+presentdate.substring(0, 10)+"%'";
						bufwriter.write(sql);
						bufwriter.newLine();
						st.addBatch(sql);
						sql="INSERT INTO `Distribution`(`DistributionId`, `ItemId`, `DriverId`, `quantity`, `DateTime`, `DeliveryQuantity`, `TakenFrom`, `EmployeeId`) SELECT d.`DistributionId`, d.`ItemId`, d.`DriverId`, d.`quantity`, d.`DateTime`, d.`DeliveryQuantity`, d.`TakenFrom`, d.`EmployeeId` FROM `IndentInfo` as i join Distribution1 as d on i.DistributionId=d.DistributionId  WHERE d.`DateTime` like '"+presentdate.substring(0, 10)+"%'";
						bufwriter.write(sql);
						bufwriter.newLine();
						st.addBatch(sql);
						try
						{
							st.executeBatch();
							con.commit();
						}catch (Exception e) {
							con.rollback();
							check=false;
							System.out.println("failed in date1"+presentdate+"  "+e.toString());
						}
					}else
					{
						check=false;
						System.out.println("failed in date2"+presentdate);
					}
					previousdate=presentdate;
					System.out.println(presentdate+" success");
					bufwriter.write("end=================");
					bufwriter.newLine();
					bufwriter.newLine();
					bufwriter.newLine();
					bufwriter.close();
				}
				
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" cleanData "+e.toString();
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();
				if(!errorlogfinal.equals("")||errcheck)
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" cleanData ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("cleanData"+e.toString());
			}
		}
	}
	public String AutoTransfor(String fromdate,String todate,BufferedWriter bufwriter)
	{
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		String reuslt="failed";
		Connection con=null;
		Statement st=null,st1=null;
		String sql="";
		try
		{
			bufwriter.write(fromdate+"============"+todate);
			bufwriter.newLine();
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				st=con.createStatement();
				st1=con.createStatement();
				con.setAutoCommit(false);
				//For kiosks
				sql="SELECT `KioskId`, `KioskName` FROM `Kiosk` WHERE `Type`='kiosk'";
				ResultSet rsst1=st1.executeQuery(sql);
				HashMap<String, String> kioskavilmap;
				String returndate=getReturnDate(todate);
				String reassigndate=getReassinDate(returndate);
				while(rsst1.next())
				{
					String kioskid=rsst1.getString("KioskId");
					bufwriter.write(kioskid+"============");
					bufwriter.newLine();
					kioskavilmap=new KioskAvilabulity().getKioskAvilabulity(con,kioskid, fromdate, todate);
					Iterator<Entry<String, String>> entrys=kioskavilmap.entrySet().iterator();
					StringBuilder itids=new StringBuilder();
					StringBuilder qtys=new StringBuilder();
					//StringBuilder delivaryqty=new StringBuilder();
					while(entrys.hasNext())
					{
						Entry<String, String> entry=entrys.next();
						String itid=entry.getKey();
						String avilable=entry.getValue();
						if(Double.parseDouble(avilable)>0)
						{
							itids.append(itid+"_$_");
							qtys.append(avilable+"_$_");
							//delivaryqty.append("0_$_");
							String sqldata="INSERT INTO Distribution(DistributionId, ItemId, DriverId, quantity, DateTime, DeliveryQuantity, TakenFrom,	EmployeeId) VALUES ('"+Math.random()+"','"+itid+"','"+kioskid+"','"+avilable+"','"+reassigndate+"','0','kitchen','1')";
							bufwriter.write(sqldata);
							bufwriter.newLine();
							st.addBatch(sqldata);
						}
					}
					if(itids.length()>0)
					{
						String returnitemids=itids.substring(0,itids.length()-3);
						String returnqtys=qtys.substring(0,qtys.length()-3);
						//String delivaryqtys=delivaryqty.substring(0,delivaryqty.length()-3);
						String sqldata="INSERT INTO `Returns`(ItemId, DriverId, ReturnQuantity, DateTime, Amount, Status, ReturnPlace, EmployeeId,DeliveredQunatity) VALUES ('"+returnitemids+"','"+kioskid+"','"+returnqtys+"','"+returndate+"','0','completed','kitchen','kitchen','0')";
						bufwriter.write(sqldata);
						bufwriter.newLine();
						st.addBatch(sqldata);
					}
				}
				try
				{
					st.executeBatch();
				}catch (Exception e) {
					System.out.println("execution failed"+e.toString());
					con.rollback();
				}
				bufwriter.write(" kitchens data");
				bufwriter.newLine();
				//For kitchens
				String kitchensql="SELECT KioskId,KioskName FROM `Kiosk` WHERE Type='kitchen'";
				errorlog=kitchensql;
				rsst1=st1.executeQuery(kitchensql);
				while(rsst1.next())
				{
					//String kioskname=rs.getString("KioskName");
					String kioskid=rsst1.getString("KioskId");
					System.out.println(kioskid);
					HashMap<String,String> kitchendata=new KitchenAvailabulity().getKitchenAvailabulity(con,kioskid, fromdate, todate,bufwriter);
					bufwriter.write(kioskid+"======================");
					bufwriter.newLine();
					Iterator<Entry<String, String>> entrys=kitchendata.entrySet().iterator();
					while(entrys.hasNext())
					{
						Entry<String, String> entry=entrys.next();
						String itid=entry.getKey();
						String avilable=entry.getValue();
						if(Double.parseDouble(avilable)>0)
						{
							bufwriter.write(itid+"====="+avilable);
							bufwriter.newLine();
							sql="INSERT INTO `InventoryReturn`(kitchenid, EmployeeId, ItemId,WasteQty, TransforQty, DateTime) VALUES ( '"+kioskid+"', '1', '"+itid+"', '0', '"+avilable+"', '"+returndate+"')";
							st.addBatch(sql);
							String addinventaryqry="INSERT INTO `Inventory` (`InventoryId`, `ItemId`, `quantity`, `DateTime`,`EmployeeId`,`kitchenid`) VALUES ('"+Math.random()+"','"+itid+"','"+avilable+"','"+todate+"','1','"+kioskid+"')";
							st.addBatch(addinventaryqry);
						}
					}
				}
				String addinventaryqry="INSERT INTO `Inventory` (`InventoryId`, `ItemId`, `quantity`, `DateTime`,`EmployeeId`,`kitchenid`) VALUES ('"+Math.random()+"','0','0','"+todate+"','1','kitchen')";
				st.addBatch(addinventaryqry);
				try
				{
					st.executeBatch();
					con.commit();
					reuslt="success";
				}catch (Exception e) {
					System.out.println(sql);
					System.out.println("execution failed"+e.toString());
					con.rollback();
				}
				
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" getTotalAvilabulity "+e.toString();
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();
				if(!errorlogfinal.equals("")||errcheck)
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" getTotalAvilabulity ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("getTotalAvilabulity"+e.toString());
			}
		}
		return reuslt;
	}
	public String getReturnDate(String date)
	{
		String resultdate=null;
		Calendar calendar=Calendar.getInstance(tz);
		Date date1;
		try {
			//System.out.println(date);
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			calendar.setTime(date1);
			calendar.add(Calendar.SECOND, -20);
			resultdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return resultdate;
	}
	public String getReassinDate(String date)
	{
		String resultdate=null;
		Calendar calendar=Calendar.getInstance(tz);
		Date date1;
		try {
			//System.out.println(date);
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			calendar.setTime(date1);
			calendar.add(Calendar.SECOND, 25);
			resultdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return resultdate;
	}
}
