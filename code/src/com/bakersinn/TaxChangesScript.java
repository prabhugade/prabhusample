package com.bakersinn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import com.agrasweets.DatabaseConnections;

public class TaxChangesScript {
	NumberFormat formatter = new DecimalFormat("#0.00");
	public static void main(String[] args) {
		new TaxChangesScript().CleanData();
	}
	public void CleanData()
	{
		String errorlog="start";
		String errorlogfinal="";
		Connection con=null;
		Statement st=null;
		PreparedStatement ps=null;
		try
		{
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				FileWriter filewriter=null;
				BufferedWriter bufwriter=null;
					filewriter=new FileWriter("/home/prabhu/Desktop/database/productin move changes/productlog.txt",true);
					bufwriter=new BufferedWriter(filewriter);
					con.setAutoCommit(false);
					String sql="SELECT o.Quantity,o.PriceTypeId,o.rest_id,o.Amount,o.Discount,o.BasePrices,o.`OrderId`,`TaxAmts`, t.`TaxId`, `Type`, `VAT`, `vat_type`, `ServiceTax`, `st_type`, `ServiceCharge`, `sc_type`, `packaging_charges`, `delivary_charge`, `ServiceTaxonDelivery`, `driver_commision`, t.`Discount`, `Discount_Type`, `AdditionalCharges`, `TaxOnAdditionalCharges`, `IncludeTaxFlag`,  `DelivaryChargesWithTax`, `DelivaryChargesOnDiscount`, `EnableDelivaryCharge`,`IpAddress` FROM `Orders` as o join TaxConfiguration as t on o.`TaxId`=t.TaxId";
					System.out.println(sql);
					bufwriter.write(sql);
					bufwriter.newLine();
					st=con.createStatement();
					ResultSet rs=st.executeQuery(sql);
					JSONObject tempjson;
					JSONArray jsonarray = null;
					ps=con.prepareStatement("UPDATE `Orders` SET TaxAmts=?,`TaxId`=?,PaymentType=? WHERE `OrderId`=?");
					while(rs.next())
					{
						bufwriter.write("orderid="+rs.getString("OrderId")+" baseprice="+rs.getString("BasePrices"));
						bufwriter.newLine();
						String[] amts=rs.getString("TaxAmts").split("\\_\\$\\_");
						String[] calculatedamts;
						boolean taxonlyinclude=false;
						boolean chargeonlyinclude=false;
						double amt=0;
						if(!rs.getString("BasePrices").equalsIgnoreCase("0"))
						{
							bufwriter.write("if=========");
							bufwriter.newLine();
							String qtys[]=rs.getString("Quantity").split("\\_\\$\\_");
							String baseamts[]=rs.getString("BasePrices").split("\\_\\$\\_");
							for(int p=0;p<baseamts.length;p++)
							{
								amt+=Double.parseDouble(baseamts[p])*Double.parseDouble(qtys[p]);
							}
							String taxes=new OldTaxConfigurations().getTaxconfigurations(rs.getString("TaxId"), amt+"", "include");

							if(rs.getString("IncludeTaxFlag").equalsIgnoreCase("1-0"))
								taxonlyinclude=true;
							else if(rs.getString("IncludeTaxFlag").equalsIgnoreCase("0-1"))
								chargeonlyinclude=true;
							else if(rs.getString("IncludeTaxFlag").equalsIgnoreCase("1-1"))
							{
								taxonlyinclude=true;
								chargeonlyinclude=true;
							}
							bufwriter.write("taxes==="+taxes);
							bufwriter.newLine();
							calculatedamts=taxes.split("\\$");
							for(int p=0;p<calculatedamts.length;p++)
							{
								if(taxonlyinclude)
								{
									if(p==0||p==1||p==2)
										amts[p]=calculatedamts[p];
								}
								if(chargeonlyinclude)
								{
									if(p==6)
										amts[p]=calculatedamts[p];
								}
							}
						}else
						{
							bufwriter.write("else=======");
							bufwriter.newLine();
							String baseamts[]=rs.getString("Amount").split("\\_\\$\\_");
							for(int p=0;p<baseamts.length;p++)
							{
								amt+=Double.parseDouble(baseamts[p]);
							}
						}
						bufwriter.write("tax"+taxonlyinclude+"  Charges"+chargeonlyinclude);
						bufwriter.newLine();
						JSONObject temp1=new JSONObject();
						jsonarray=new JSONArray();
						for(int p=0;p<amts.length;p++)
						{
							bufwriter.write("amts["+p+"] ="+amts[p]);
							bufwriter.newLine();
							if(Double.parseDouble(amts[p])>0)
							{
								if(p==0)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("VAT")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									if(taxonlyinclude)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("1", temp3);
								}else if(p==1)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("ServiceTax")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									if(taxonlyinclude)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("2", temp3);
								}else if(p==2)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("ServiceCharge")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									if(taxonlyinclude)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("3", temp3);
								}else if(p==3)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("packaging_charges")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("4", temp3);
								}else if(p==4)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("delivary_charge")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("5", temp3);
								}else if(p==5)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("ServiceTaxonDelivery")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("6", temp3);
								}else if(p==6)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("AdditionalCharges")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									if(chargeonlyinclude)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("7", temp3);
								}
							}
						}
						bufwriter.write("final tax="+temp1);
						bufwriter.newLine();
						tempjson=new JSONObject();
						tempjson.put("paymentid", "1");
						tempjson.put("amount", formatter.format(amt));
						tempjson.put("desc", "");
						jsonarray.put(tempjson);
						ps.setString(1, temp1.toString());
						ps.setString(2, "0");
						ps.setString(3, jsonarray.toString());
						ps.setString(4, rs.getString("OrderId"));
						bufwriter.write(ps.toString());
						bufwriter.newLine();
						ps.addBatch();
					}
					sql="SELECT `OrderId`,`Amount`, `Discount`, `TaxAmts`,`DeliveryCharge`, `PriceTypeId`,`BasePrices` FROM `Orders` WHERE `TaxId`='0' and `TaxAmts` like '%_$_%'";
					st=con.createStatement();
					rs=st.executeQuery(sql);
					while(rs.next())
					{
						bufwriter.write("orderid="+rs.getString("OrderId")+" baseprice="+rs.getString("BasePrices"));
						bufwriter.newLine();
						String[] amts=rs.getString("TaxAmts").split("\\_\\$\\_");
						boolean include=false;
						double amt=0;

						String baseamts[]=rs.getString("Amount").split("\\_\\$\\_");
						for(int p=0;p<baseamts.length;p++)
						{
							amt+=Double.parseDouble(baseamts[p]);
						}
						JSONObject temp1=new JSONObject();
						jsonarray=new JSONArray();
						for(int p=0;p<amts.length;p++)
						{
							if(Double.parseDouble(amts[p])>0)
							{
								if(p==0)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									if(include)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("1", temp3);
								}else if(p==1)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									if(include)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("2", temp3);
								}else if(p==2)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									if(include)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("3", temp3);
								}else if(p==3)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("4", temp3);
								}else if(p==4)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put(rs.getString("DeliveryCharge")+"%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("5", temp3);
								}else if(p==5)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("6", temp3);
								}else if(p==6)
								{
									JSONObject temp2=new JSONObject();
									JSONObject temp3=new JSONObject();
									temp2.put("0%", amts[p]);
									temp3.put("taxamt", temp2);
									if(include)
										temp3.put("include", "1");
									else
										temp3.put("include", "0");
									amt+=Double.parseDouble(amts[p]);
									temp1.put("7", temp3);
								}
							}
						}
						tempjson=new JSONObject();
						tempjson.put("paymentid", "1");
						tempjson.put("amount", formatter.format(amt));
						tempjson.put("desc", "");
						jsonarray.put(tempjson);
						ps.setString(1, temp1.toString());
						ps.setString(2, "0");
						ps.setString(3, jsonarray.toString());
						ps.setString(4, rs.getString("OrderId"));
						bufwriter.write(ps.toString());
						bufwriter.newLine();
						ps.addBatch();
					}
					int[] res=ps.executeBatch();
					con.commit();
					bufwriter.close();
			}else
			{
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" cleanData "+e.toString();
			try
			{
				con.rollback();
			}catch (Exception e1) {
			}
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();				
				if(!errorlogfinal.equals(""))
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
}
