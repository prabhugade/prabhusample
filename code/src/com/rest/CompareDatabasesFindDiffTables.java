package com.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CompareDatabasesFindDiffTables 
{
	//working
	String dbname=null;
	public static void main(String[] args) 
	{
		try
		{
			CompareDatabasesFindDiffTables comp=new CompareDatabasesFindDiffTables();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			System.out.println("Enter Username");
			String uname=reader.readLine();
			System.out.println("Enter Password");
			String pwd=reader.readLine();
			HashMap<String,String> map1=comp.getTables(dbname,uname,pwd);
			System.out.println("Enter Another Database name");
			dbname=reader.readLine();
			HashMap<String,String> map2=comp.getTables(dbname,uname,pwd);
			comp.compareToMaps(map1, map2);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public Connection getConnection(String database,String uname,String pwd)
	{
		Connection con=null;
		try
		{
			if(con==null)
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database,uname,pwd);
			}
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return con;
	}
	public HashMap<String,String> getTables(String dbname,String uname,String pwd)
	{
		HashMap<String,String> map=new HashMap<String,String>();
		Connection con=null;
		Statement st=null;
		
		try
		{
			con=getConnection(dbname,uname,pwd);
			st=con.createStatement();
			DatabaseMetaData dbm = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbm.getTables(null,null,"%",types);
			while (rs.next())
			{
				String table = rs.getString("TABLE_NAME");
				ResultSet rest=st.executeQuery("SELECT * FROM "+table);
				ResultSetMetaData rsmd = rest.getMetaData();
				int numColumns = rsmd.getColumnCount();
				String columnnames=numColumns+",";
				for (int i=1; i<numColumns+1; i++) 
				{
					String columnName = rsmd.getColumnName(i);
					columnnames+=columnName+",";
				}
				map.put(table, columnnames);
			}
		}catch(Exception e)
		{
			
			System.out.println(e.getMessage());
		}finally
		{
			try
			{
			con.close();
			st.close();
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		return map;
	}
	public void compareToMaps(HashMap<String,String> map1,HashMap<String,String> map2)
	{
		try
		{
			System.out.println(map1);
			System.out.println(map2);
			Set<String> set=map1.keySet();
			Iterator<String> it=set.iterator();
			while(it.hasNext())
			{
				String tablename=it.next();
				if(map2.containsKey(tablename))
				{
					if(!map1.get(tablename).equals(map2.get(tablename)))
						System.out.println("columns different\n"+tablename+" "+map1.get(tablename)+"    "+map2.get(tablename));
				}else
				{
					System.out.println("new table\n"+tablename+" "+map1.get(tablename));
				}
			}
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
