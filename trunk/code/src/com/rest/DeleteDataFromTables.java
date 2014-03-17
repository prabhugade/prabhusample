package com.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class DeleteDataFromTables {
	String DBName=null;	
	Connection con=null;
	Statement st=null;
	Statement st1=null;
	Statement st2=null;

	public static void main(String[] args) 
	{
		DeleteDataFromTables data=new DeleteDataFromTables();
		data.deletedataTables("hydhouse");
	}
	public String connect(String dbname)
	{
		//String res=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url="jdbc:mysql://localhost:3306/"+dbname;
			con=DriverManager.getConnection(url,"root","silica");
			st=con.createStatement();
			st1=con.createStatement();
			st2=con.createStatement();
			//res="connection obtained";
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		return "connection obtained";
	}
	public void deletedataTables(String dbname)
	{
		try
		{
			connect(dbname);
			st=con.createStatement();
			con.setAutoCommit(false);
			st.addBatch("DELETE FROM `AmountReturn` WHERE `DateTime` like '2012-07-27%'");
			st.addBatch("DELETE FROM `Distribution` WHERE `DateTime` like '2012-07-27%'");
			st.addBatch("DELETE FROM `Inventory` WHERE `DateTime` like '2012-07-27%'");
			st.addBatch("DELETE FROM `InventoryReturn` WHERE `DateTime` like '2012-07-27%'");
			st.addBatch("DELETE FROM `Returns` WHERE `DateTime` like '2012-07-27%'");
			st.executeBatch();
			con.commit();
			System.out.println("successfully deleted");
		}
		catch(Exception e)
		{
			try
			{
			con.rollback();
			}catch(Exception e1)
			{
				System.out.println(e1.getMessage());
			}
			System.out.println(e);
		}
	}
}
