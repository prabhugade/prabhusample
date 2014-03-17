package com.bakersinn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

public class FindTaxesDifferents {
	NumberFormat formatter = new DecimalFormat("#0.00");
	public static void main(String[] args) 
	{
		try
		{
			FindTaxesDifferents diff=new FindTaxesDifferents();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			Connection con=diff.getConnection(dbname);
			Connection con1;
			if(con!=null)
			{
				System.out.println("Enter Another Database name");
				dbname=reader.readLine();
				con1=diff.getConnection(dbname);
				diff.findDifferentTaxes(con,con1);
			}
		}catch (Exception e) {
		}
	}
	public void findDifferentTaxes(Connection con,Connection con1)
	{
		Statement st=null,st1=null;
		try
		{
			HashMap<String, String> firstdata=new HashMap<String, String>();
			HashMap<String, String> seconddata=new HashMap<String, String>();
			String sql="SELECT OrderId,TaxAmts,Amount,Discount,BasePrices,Quantity  FROM `Orders` where BasePrices!='0'";
			st=con.createStatement();
			ResultSet rs=st.executeQuery(sql);
			String[] amts,taxes,qtys;
			while(rs.next())
			{
				double amt=0;
				amts=rs.getString("Amount").split("\\_\\$\\_");
				taxes=rs.getString("TaxAmts").split("\\_\\$\\_");
				for(int p=0;p<amts.length;p++)
				{
					amt+=Double.parseDouble(amts[p]);
				}
				for(int p=0;p<amts.length;p++)
				{
					amt+=Double.parseDouble(taxes[p]);
				}
				amt-=Double.parseDouble(rs.getString("Discount"));
				firstdata.put(rs.getString("OrderId"), formatter.format(amt));
			}
			sql="SELECT OrderId,TaxAmts,Amount,Discount,BasePrices,Quantity  FROM `Orders` where BasePrices!='0'";
			st1=con1.createStatement();
			rs=st1.executeQuery(sql);
			while(rs.next())
			{
				double amt=0;
				amts=rs.getString("BasePrices").split("\\_\\$\\_");
				qtys=rs.getString("Quantity ").split("\\_\\$\\_");
				for(int p=0;p<amts.length;p++)
				{
					amt+=Double.parseDouble(amts[p])*Double.parseDouble(qtys[p]);
				}
				amt+=getTotalTaxamt(rs.getString("TaxAmts"));
				amt-=Double.parseDouble(rs.getString("Discount"));
				seconddata.put(rs.getString("OrderId"), formatter.format(amt));
			}
			Iterator<String> it=firstdata.keySet().iterator();
			while(it.hasNext())
			{
				String orderid=it.next();
				if(seconddata.containsKey(orderid))
				{
				if(Double.parseDouble(firstdata.get(orderid))!=Double.parseDouble(seconddata.get(orderid)))
				{
					System.out.println("========="+orderid);
					System.out.println(firstdata.get(orderid));
					System.out.println(seconddata.get(orderid)+"\n========");
				}
				}else
				{
					System.out.println("========="+orderid);
					System.out.println(firstdata.get(orderid));
					System.out.println("sencond data not exist\n========");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	public static double getTotalTaxamt(String taxamts)
	{
		double taxesamt=0;
		try
		{
			JSONObject taxjson=new JSONObject(taxamts);
			Iterator<String> it=taxjson.keys();
			String id;
			JSONObject tempjson;
			while(it.hasNext())
			{
				double amt=0;
				id=it.next();
				tempjson=taxjson.getJSONObject(id);
				JSONObject values=tempjson.getJSONObject("taxamt");
				Iterator<String> valuesid=values.keys();
				while(valuesid.hasNext())
				{
					amt+=Double.parseDouble(values.getString(valuesid.next()));
				}
				taxesamt+=amt;
			}
		}catch (Exception e) {
		}
		return taxesamt;
	}
	public static Connection getConnection(String database)
	{
		String uname="root";
		String pwd="silica";
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
}
