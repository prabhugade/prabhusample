package com.bakersinn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeMap;

import com.agrasweets.DatabaseConnections;

public class OldTaxConfigurations 
{
	NumberFormat formatter = new DecimalFormat("#0.00");
	TreeMap<String, String> taxmap=new TreeMap<String, String>();
	/**
	 *  This method is used to getTaxes as string
	 * 
	 * @param busid bussiness id
	 * @param type pricetypeid
	 * @param restid restaurntid
	 * @param orgamount totalamount
	 * @param purpose item/other
	 * @return restuns the string
	 */
	public String getTaxconfigurations(String taxid,String orgamount,String purpose) 
	{
		boolean errcheck=false;
		TreeMap<String, String> tmap=new TreeMap<String, String>();
		String result="-1";
		Connection con=null;
		PreparedStatement st=null;
		String errorlog="start";
		String errorlogfinal="";
		try
		{
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				String getTaxConfigurations="SELECT `TaxId`, `VAT`, `vat_type`, `ServiceTax`, `st_type`, `ServiceCharge`, `sc_type`, `packaging_charges`, `delivary_charge`, `driver_commision`,Discount,Discount_Type,ServiceTaxonDelivery,AdditionalCharges,IncludeTaxFlag,TaxOnAdditionalCharges,DelivaryChargesWithTax,DelivaryChargesOnDiscount,EnableDelivaryCharge FROM TaxConfiguration WHERE TaxId='"+taxid+"'";
				
				String sql=getTaxConfigurations;
				st=con.prepareStatement(sql);
					errorlog=st.toString();
					ResultSet rs=st.executeQuery();
					if(rs.next())
					{
						String amount="0";
						taxmap=new TreeMap<String, String>();
						if(rs.getString("Discount_Type").equals("1"))
						{
							amount=check_discount(orgamount,rs.getString("Discount"),rs.getString("Discount_Type"));
						}else
							amount=orgamount;

						tmap.put("0", amount);
						//for additioanl charges
						if(rs.getString("AdditionalCharges").contains("%"))
						{
							String adcharge=rs.getString("AdditionalCharges").replace("%", "").trim();
							double delivery=(Double.parseDouble(adcharge)*Double.parseDouble(tmap.get("0")))/100;
							taxmap.put("8", delivery+"");
						}else
						{
							taxmap.put("8", rs.getString("AdditionalCharges"));
						}

						if(rs.getString("TaxOnAdditionalCharges").equalsIgnoreCase("1"))
						{
							if((rs.getString("IncludeTaxFlag").equalsIgnoreCase("0-1")&&purpose.equalsIgnoreCase("other"))||(rs.getString("IncludeTaxFlag").equalsIgnoreCase("1-1")&&purpose.equalsIgnoreCase("other"))||(rs.getString("IncludeTaxFlag").equalsIgnoreCase("1")&&purpose.equalsIgnoreCase("other")))
							{								
							}else
							{
								amount=(Double.parseDouble(amount)+Double.parseDouble(taxmap.get("8")))+"";
								tmap.put("0", amount);
							}
						}
						tmap.put("1", rs.getString("VAT")+"-"+rs.getString("vat_type"));
						tmap.put("2", rs.getString("ServiceTax")+"-"+rs.getString("st_type"));
						tmap.put("3", rs.getString("ServiceCharge")+"-"+rs.getString("sc_type"));
						tmap.put("4", rs.getString("packaging_charges"));
						tmap.put("5", rs.getString("delivary_charge"));
						tmap.put("6", rs.getString("driver_commision"));
						tmap.put("7", rs.getString("ServiceTaxonDelivery"));
						tmap.put("8", rs.getString("AdditionalCharges"));
						result=getTax(tmap);
					}else
					{
						// vat $ servicetax $ service charge $ packaging charge $ delivary charge $ service tax on delivary charge $ aditional charges
						result="0$0$0$0$0$0$0";
					}
			}
			else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch (Exception e)
		{
			errorlogfinal=errorlog+" getTaxconfigurations "+e.getMessage();
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
						errorlogfinal=errorlog+" getTaxconfigurations ";
					//new Logs().storeErrorLog(busid,"", "getTaxconfigurations", errorlogfinal, "failed");
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return result;
	}	
	public String check_discount(String amount, String discount, String type) 
	{
		if(type.equals("1"))
		{
			//Pre taxes
			double amt=0;
			double disc=0;
			DecimalFormat df = new DecimalFormat("#.##");
			if(discount.contains("%"))
			{
				amt=Double.parseDouble(amount);
				disc=Double.parseDouble(discount.replace("%", "").trim());
				double per=(amt*disc)/100;
				amt=amt-per;
			}else
			{
				amt=(Double.parseDouble(amount)-Double.parseDouble(discount));
			}
			amount=df.format(amt);
		}
		return amount;
	}
	public String getTax(TreeMap<String, String> tmap)
	{
		//for service charge
		if(!tmap.get("3").split("-")[0].contains("%"))
		{
			taxmap.put("3", tmap.get("3").split("-")[0]);
		}else if(Double.parseDouble(tmap.get("3").split("-")[0].replace("%", ""))>0)
		{
			tmap.put("3", tmap.get("3").replace("%", ""));
			while(!taxmap.containsKey("3"))
			{
				calAmount("3",tmap);
			}
			
		}else
		{
			taxmap.put("3", "0");
		}
		//for vat
		if(Double.parseDouble(tmap.get("1").split("-")[0].replace("%", ""))>0)
		{
			while(!taxmap.containsKey("1"))
			{
				calAmount("1",tmap);
			}
			
		}else
		{
			taxmap.put("1", "0");
		}
		//for service tax
		if(Double.parseDouble(tmap.get("2").split("-")[0].replace("%", ""))>0)
		{
			while(!taxmap.containsKey("2"))
			{
				calAmount("2",tmap);
			}
			
		}else
		{
			taxmap.put("2", "0");
		}
		//for packageing charge
		taxmap.put("4", tmap.get("4"));
		
		//for delivary charge
		if(tmap.get("5").contains("%"))
		{
			String del=tmap.get("5").replace("%", "").trim();
			double delivery=(Double.parseDouble(del)*Double.parseDouble(tmap.get("0")))/100;
			taxmap.put("5", delivery+"");
		}else
		{
			taxmap.put("5", tmap.get("5"));
		}
		//for driver commission
		if(tmap.get("6").equals("1"))
		{
			taxmap.put("6", taxmap.get("3"));
		}else if(tmap.get("6").equals("2"))
		{
			taxmap.put("6", taxmap.get("5"));
		}else if(tmap.get("6").equals("0"))
		{
			taxmap.put("6", "0");
		}else if(tmap.get("6").split("-")[0].equals("3"))
		{
			taxmap.put("6", tmap.get("6").split("-")[1]);
		}
		//for service tax on delivary charge
		if(tmap.get("7").contains("%")&&Double.parseDouble(taxmap.get("5"))>0)
		{
			String stONdel=tmap.get("7").replace("%", "").trim();
			double stONdelivery=(Double.parseDouble(stONdel)*Double.parseDouble(taxmap.get("5")))/100;
			taxmap.put("7", stONdelivery+"");
		}else
		{
			if(Double.parseDouble(taxmap.get("5"))>0)
				taxmap.put("7", tmap.get("7"));
			else
				taxmap.put("7", "0");
		}
		String result="";
		result=formatter.format(Double.parseDouble(taxmap.get("1")))+"$"+formatter.format(Double.parseDouble(taxmap.get("2")))+"$"+formatter.format(Double.parseDouble(taxmap.get("3")))+"$"+formatter.format(Double.parseDouble(taxmap.get("4")))+"$"+formatter.format(Double.parseDouble(taxmap.get("5")))+"$"+formatter.format(Double.parseDouble(taxmap.get("7")))+"$"+formatter.format(Double.parseDouble(taxmap.get("8")));
		return result;
	}
	public void calAmount(String value,TreeMap<String, String> tmap)
	{
		if(!taxmap.containsKey(value))
		{
			if(value.intern()=="0")
				taxmap.put(value, tmap.get(value));
			else
			{
				String str=tmap.get(value);
				String vatper=str.split("-")[0];
				String vat_type=str.split("-")[1];
				String per="0";
				if(vat_type.contains("*"))
					per=vat_type.substring(0,vat_type.indexOf("*"));
				String grassbill="";
				String outbill="";
				if(vat_type.contains("(")&&vat_type.contains(")"))
				{
					grassbill=vat_type.substring(vat_type.indexOf("("), vat_type.indexOf(")"));
					outbill=vat_type.substring(vat_type.indexOf(")"));
				}
				if(grassbill.length()==0&&outbill.length()==0)
					taxmap.put(value, "0");
				else
				{
					if(!taxmap.containsKey("0")&&(grassbill.contains("0")||outbill.contains("0")))
					{
						calAmount("0",tmap);
					}else if(!taxmap.containsKey("1")&&(grassbill.contains("1")||outbill.contains("1")))
					{
						calAmount("1",tmap);
					}else if(!taxmap.containsKey("2")&&(grassbill.contains("2")||outbill.contains("2")))
					{
						calAmount("2",tmap);
					}else if(!taxmap.containsKey("3")&&(grassbill.contains("3")||outbill.contains("3")))
					{
						calAmount("3",tmap);
					}else
					{
						double amount=0;
						if(grassbill.contains("0"))
							amount+=Double.parseDouble(taxmap.get("0"));
						if(grassbill.contains("1"))
							amount+=Double.parseDouble(taxmap.get("1"));
						if(grassbill.contains("2"))
							amount+=Double.parseDouble(taxmap.get("2"));
						if(grassbill.contains("3"))
							amount+=Double.parseDouble(taxmap.get("3"));
						amount=Double.parseDouble(per)*amount/100;
						if(outbill.contains("1"))
							amount+=Double.parseDouble(taxmap.get("1"));
						if(outbill.contains("2"))
							amount+=Double.parseDouble(taxmap.get("2"));
						if(outbill.contains("3"))
							amount+=Double.parseDouble(taxmap.get("3"));
						amount=(amount*Double.parseDouble(vatper))/100;
						taxmap.put(value, formatter.format(amount));
					}
					
				}
				
			}
		}
	}
}
