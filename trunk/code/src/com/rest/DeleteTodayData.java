package com.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class DeleteTodayData 
{
	String DBName=null;	
	Connection con=null;
	Statement st=null;
	Statement st1=null;
	Statement st2=null;
	public static void main(String[] args) 
	{
		try
		{
		ArrayList<String> tabnames=new ArrayList<String>();
		tabnames.add("AmountReturn");
		tabnames.add("Inventory");
		tabnames.add("InventoryReturn");
		tabnames.add("Distribution");
		tabnames.add("Returns");
		tabnames.add("Orders");
		
		DeleteTodayData deltable=new DeleteTodayData();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		 System.out.println("Enter Database name");
		  String dbname=reader.readLine();
		  System.out.println("Enter from Date( like yyyy-mm-dd)");
		  String fromdate=reader.readLine();
		  System.out.println("Enter to Date( like yyyy-mm-dd)");
		  String todate=reader.readLine();
		deltable.deletedata(tabnames, fromdate+" 00:00:01",todate+" 23:59:59",dbname);
		System.out.println("deleted successfully");
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
	}
	public String connect(String dbname)
	{
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
	public void deletedata(ArrayList<String> list,String fromdate,String todate,String dbname)
	{
		try
		{
			connect(dbname);
			st=con.createStatement();
			con.setAutoCommit(false);
			Iterator<String> it=list.iterator();
			while(it.hasNext())
			{
				String tablename=it.next();
				String sql="DELETE FROM "+tablename+" WHERE DateTime between '"+fromdate+"' and '"+todate+"'";
				System.out.println(sql);
				st.addBatch(sql);
			}
			st.executeBatch();
			con.commit();
			
		}catch(Exception e)
		{
			try
			{
			con.rollback();
			}catch(Exception e1)
			{
				System.out.println(e1.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}
}
