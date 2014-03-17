package com.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class QueryForming 
{

	String DBName=null;	
	Connection con=null;
	Statement st=null;
	PreparedStatement st1=null;
	PreparedStatement st2=null;
	public static void main(String[] args) 
	{
		try
		{
			QueryForming form=new QueryForming();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			System.out.println("Enter Date( like yyyy-mm-dd)");
			String date=reader.readLine();
			date=date+" 00:00:00";
			form.queryForming(date,dbname);
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
			
			//res="connection obtained";
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		return "connection obtained";
	}
	public void queryForming(String date,String dbname)
	{
		try
		{
			String condition="";
			connect(dbname);
			String sql="SELECT store_itemid,min(datetime) as date FROM `Store_Inventory` WHERE status='old' and datetime<='"+date+"' group by `store_itemid`";
			st=con.createStatement();
			ResultSet rs=st.executeQuery(sql);
			while(rs.next())
			{
				System.out.println(rs.getString("store_itemid")+"  "+rs.getString("date"));
				condition+="(datetime between '"+rs.getString("date")+"' and '"+date+"' and store_itemid='"+rs.getString("store_itemid")+"') or ";
			}
			condition=condition.substring(0,condition.length()-3);
			System.out.println(condition);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
