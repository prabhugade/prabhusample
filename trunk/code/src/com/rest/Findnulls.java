package com.rest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Findnulls 
{
	String DBName=null;	
	Connection con=null;
	Statement st=null;
	Statement st1=null;
	Statement st2=null;
	public static void main(String[] args) 
	{
		Findnulls find=new Findnulls();
		HashMap<String,String> map=find.loaddbnames();
		map.put("exit", "exit application");
		Iterator<String> it=map.keySet().iterator();

		while(it.hasNext())
		{
			String index=it.next();
			System.out.println("index:"+index+"     database name:  "+map.get(index));
		}
		boolean check=true;
		while(check)
		{
			System.out.println("select database and enter index");
			Scanner scan=new Scanner(System.in);
			String s=scan.nextLine();
			if(!s.equals("exit"))
			{
			System.out.println(map.get(s));
			ArrayList<String> tables=find.loadtables(map.get(s));
			System.out.println(tables);
			find.findNullContainTables(tables,map.get(s));
			}else
			{
				check=false;
			}
		}
	}

	public HashMap<String,String> loaddbnames()
	{
		HashMap<String,String> dbnames=new HashMap<String,String>();
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306","root","silica");
			DatabaseMetaData dbmd=con.getMetaData();
			ResultSet rs=dbmd.getCatalogs();
			int i=0;
			while(rs.next())
			{
				dbnames.put(i+"",rs.getString(1));
				i++;
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return dbnames;
	}
	public String connect(String dbname)
	{
		//String res=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url="jdbc:mysql://localhost:3306/"+dbname;
			con=DriverManager.getConnection(url,"root","Silica123");
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
	public ArrayList<String> loadtables(String dbname)
	{

		ArrayList<String> dbnames=new ArrayList<String>();
		try
		{
			connect(dbname);
			DatabaseMetaData dbm = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbm.getTables(null,null,"%",types);
			while (rs.next()){
				String table = rs.getString("TABLE_NAME");
				dbnames.add(table);
			}

		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return dbnames;
	}

	public ArrayList<String> findNullContainTables(ArrayList<String> tables,String dbname)
	{
		ArrayList<String> tablenames=new ArrayList<String>();
		try
		{
			connect(dbname);
			Iterator<String> it=tables.iterator();
			while(it.hasNext())
			{
				String tabname=it.next();
				ResultSet rs = st.executeQuery("SELECT * FROM "+tabname);

				// Get result set meta data
				ResultSetMetaData rsmd = rs.getMetaData();
				int numColumns = rsmd.getColumnCount();
				String sql="select * from "+tabname+" where ";
				String sql1="";
				// Get the column names; column indices start from 1
				for (int i=1; i<numColumns+1; i++) 
				{
					String columnName = rsmd.getColumnName(i);
					sql1+="`"+columnName+"`='null' OR ";

				}
				sql=sql+sql1.substring(0,sql1.length()-4);
				rs = st.executeQuery(sql);
				if(rs.next())
				{
					tablenames.add(tabname);
				}
			}
			System.out.println("null contain tables="+tablenames);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return tablenames;
	}
}
