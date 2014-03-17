package com.restronaut.billgenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

public abstract class DatabaseConnections 
{
	static HashMap<String, DataSource> connetionsmap=new HashMap<String, DataSource>();
	public static void setDatabaseConnections(HashMap<String, DataSource> connections)
	{
		connetionsmap=connections;
	}
	public static Connection getConnection(String bid)
	{
		Connection con=null;
		try
		{
			//System.out.println("connection"+bid);
			if(con==null&&bid!=null&&connetionsmap.containsKey(bid))
			{
				//System.out.println(connetionsmap.size());
				con= connetionsmap.get(bid).getConnection();
				if(con==null)
					System.out.println("connection failed");
			}
		}catch(Exception e)
		{
			//System.out.println("connectionopen()"+e);
		}
		return con;
	}
	public static String getDatabaseconfigurations(String driver,String dburl,String user,String pwd,String requrlvalue,String authtoken) 
	{
		String res = "failed";
		Connection con = null;
		PreparedStatement ps = null;
		try 
		{
			con = getConnection("main");
			if(con!=null)
			{
				ps=con.prepareStatement("SELECT `urlid`, `database`,`ImageUrl`,`Token1`,`Token2` FROM `Databaseurls` where status='T' and url=?");
				ps.setString(1, requrlvalue);
				ResultSet rs=ps.executeQuery();
				if(rs.next())
				{
					String token1=rs.getString("Token1");
					String token2=rs.getString("Token2");
					String md5key=computeHash(token1+token2);
					if(authtoken.equalsIgnoreCase(md5key))
					{
					res="jdbc:mysql://"+rs.getString("database");
					}else
						res="Authentication Failed";
				}else
					res="Invalid BussinessID";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e2) {

			}
		}
		return res;
	}
	public static String getUrlidtoBusid(String requrlvalue)
	{
		String res=null;
		Connection con=null;
		Statement st=null;
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		try
		{
			con=DatabaseConnections.getConnection("main");
			if(con!=null)
			{
				st=con.createStatement();
				String seldatabaseqry="SELECT `url` FROM `Databaseurls` WHERE `status`='T' and `urlid`='"+requrlvalue+"'";
				ResultSet rs=st.executeQuery(seldatabaseqry);
				if(rs.next())
				{
					res=rs.getString("url");
				}else
					res=null;
			}
			else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch (Exception e)
		{
			errorlogfinal=errorlog+" DownloadServlet getDatabaseconfigurations "+e.getMessage();
		}
		finally
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
						errorlogfinal=errorlog+" DownloadServlet getDatabaseconfigurations ";
					new Logs().storeErrorLog(res, "-", "DownloadServlet getDatabaseconfigurations", errorlogfinal, "failed");
				}
			}
			catch (Exception e2) 
			{

			}
		}
		return res;
	}
	public static String getBusidtoUrlid(String requrlvalue) 
	{
		String res=null;
		Connection con=null;
		Statement st=null;
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		try
		{
			con=DatabaseConnections.getConnection("main");
			if(con!=null)
			{
				st=con.createStatement();
				String seldatabaseqry="SELECT `urlid` FROM `Databaseurls` WHERE `status`='T' and `url`='"+requrlvalue+"'";
				ResultSet rs=st.executeQuery(seldatabaseqry);
				if(rs.next())
				{
					res=rs.getString("urlid");
				}else
					res=null;
			}
			else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch (Exception e)
		{
			errorlogfinal=errorlog+" DownloadServlet getDatabaseconfigurations "+e.getMessage();
		}
		finally
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
						errorlogfinal=errorlog+" DownloadServlet getDatabaseconfigurations ";
					System.out.println(errorlogfinal);
					new Logs().storeErrorLog(res, "-", "DownloadServlet getDatabaseconfigurations", errorlogfinal, "failed");
				}
			}
			catch (Exception e2) 
			{

			}
		}
		return res;
	}
	public static String computeHash(String x) throws Exception  
	{
		java.security.MessageDigest d =null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(x.getBytes());
		return  byteArrayToHexString(d.digest());
	}

	public static String byteArrayToHexString(byte[] b)
	{
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			int v = b[i] & 0xff;
			if (v < 16)
			{
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
}
