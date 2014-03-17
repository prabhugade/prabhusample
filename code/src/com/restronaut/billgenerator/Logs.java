package com.restronaut.billgenerator;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class Logs 
{
	TimeZone tz=null;	
	public Logs()
	{
		tz=TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
	}
	/*public String storeErrorLog(String driver, String url, String user,String pwd,String empid,String action,String desc,String status)
	{
		String result="failed";
		Connection con=null;
		Statement st=null;
		String today=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance(new reportMethods().getTimeZones()).getTime());
		try
		{
			con=DatabaseConnections.connectionOpen(driver, url, user, pwd);
			if(con!=null)
			{
				st=con.createStatement();
				int i=st.executeUpdate("INSERT INTO `ErrorLog`(`EmployeeId`, `Action`, `DateTime`, `Desc`, `Status`) VALUES ('"+empid.replaceAll("'", "''")+"','"+action.replaceAll("'", "''")+"','"+today.replaceAll("'", "''")+"','"+desc.replaceAll("'", "''")+"','"+status.replaceAll("'", "''")+"')");
				if(i>0)
					result="success";
				else
					result="failed";
			}else
			{
				//System.out.println(driver+" "+url+" "+action+" "+desc);
			}
		}catch(Exception e)
		{
			System.out.println("failed in errorlog"+e);
		}finally
		{
			try
			{
				st.close();
				con.close();
			}catch(Exception e)
			{
				System.out.println("failed in errorlog"+e);
			}
		}
		return result;
	}*/
	public String storeErrorLog(String bid,String empid,String action,String desc,String status)
	{
		String result="failed";
		Connection con=null;
		Statement st=null;
		String today=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance(tz).getTime());
		try
		{
			con=DatabaseConnections.getConnection("main");
			if(con!=null)
			{
				st=con.createStatement();
				int i=st.executeUpdate("INSERT INTO `ErrorLog`(`EmployeeId`, `Action`, `DateTime`, `Desc`, `Status`, `Urlid`)  SELECT '"+empid.replaceAll("'", "''")+"','"+action.replaceAll("'", "''")+"','"+today.replaceAll("'", "''")+"','"+desc.replaceAll("'", "''")+"','"+status.replaceAll("'", "''")+"',urlid  from Databaseurls where `url`='"+bid+"'");
				if(i>0)
					result="success";
				else
					result="failed";
			}else
			{
				System.out.println(bid+" "+action+" "+desc);
			}
		}catch(Exception e)
		{
			System.out.println(action+"\nfailed in errorlog"+e);
		}finally
		{
			try
			{
				st.close();
				con.close();
			}catch(Exception e)
			{
				System.out.println(action+"failed in errorlog"+e);
			}
		}
		return result;
	}
}
