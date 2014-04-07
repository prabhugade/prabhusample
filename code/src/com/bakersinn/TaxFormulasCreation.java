package com.bakersinn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.agrasweets.DatabaseConnections;

public class TaxFormulasCreation {

	public static void main(String[] args) {
		new TaxFormulasCreation().createFormulas();
	}
	public void createFormulas()
	{
		String errorlog="start";
		String errorlogfinal="";
		Connection con=null;
		PreparedStatement ps=null,ps1=null,ps2=null,ps3,ps4,ps5,ps6,ps7,ps8,ps9;
		ResultSet rs=null,rs1=null,rs2,rs3;
		try
		{
			con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				con.setAutoCommit(false);
				HashMap<String, String> taxnothavemap=new HashMap<String, String>();
				ArrayList<String> restaurantdiscountdiffernt=new ArrayList<String>();
				ps=con.prepareStatement("SELECT `rest_id`, `restaurant_name` FROM `Restaurant_details`");
				ps1=con.prepareStatement("SELECT `TaxId`, `VAT`, `vat_type`, `ServiceTax`, `st_type`, `ServiceCharge`, `sc_type`, `packaging_charges`, `delivary_charge`, `driver_commision`,Discount,Discount_Type,ServiceTaxonDelivery,AdditionalCharges,IncludeTaxFlag,TaxOnAdditionalCharges,DelivaryChargesWithTax,DelivaryChargesOnDiscount,EnableDelivaryCharge,DateTime FROM TaxConfiguration WHERE `rest_id`=? AND `Type`=? and DateTime=(select max(`DateTime`) from TaxConfiguration where `rest_id`=? AND `Type`=?)");
				ps2=con.prepareStatement("SELECT `TaxField_Id`, `TaxFormula_DateTime`, `TaxFormula_Structure` FROM `TaxFormulas` WHERE `TaxFormula_Structure`=?");
				ps3=con.prepareStatement("INSERT INTO `TaxFields`(`TaxField_Name`, `TaxField_Desc`, `TaxField_Status`, `TaxField_Type`) VALUES (?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
				ps4=con.prepareStatement("INSERT INTO `TaxFormulas`(`TaxField_Id`, `TaxFormula_DateTime`, `TaxFormula_Structure`) VALUES (?,?,?)");
				ps5=con.prepareStatement("UPDATE `ItemPrices` SET Pricetypeid=? WHERE `Pricetypeid`=? and `rest_id`=?");
				ps6=con.prepareStatement("UPDATE `Mapping_table` SET `TaxField_Id`=?,`pricetypeid`=? WHERE `pricetypeid`=? and `rest_id`=?");
				ps7=con.prepareStatement("UPDATE `OldPrices` SET `pricetypeid`=? where `pricetypeid`=? and `rest_id`=?");
				ps8=con.prepareStatement("UPDATE `TaxFields` SET TaxField_Desc=concat(TaxField_Desc,?) WHERE `TaxField_Id`=?");
				ps9=con.prepareStatement("UPDATE `Restaurant_details` SET `RestauranDiscount`=?,`TaxOnPostDiscount`=? where `rest_id`=?");
				rs=ps.executeQuery();
				String restaurantdiscount,restaurantdiscounttype;
				HashMap<String, JSONObject> taxformulasmap=new HashMap<String, JSONObject>();
				ArrayList<JSONObject> formulaslist=new ArrayList<JSONObject>();
				HashMap<JSONObject, JSONObject> formulas_restnames=new HashMap<JSONObject, JSONObject>();
				int fomulascount=1;
				while(rs.next())
				{
					restaurantdiscount="";
					restaurantdiscounttype="";
					String restname=rs.getString("restaurant_name");
					String restid=rs.getString("rest_id");
					for(int p=1;p<=2;p++)
					{
						ps1.setString(1, restid);
						ps1.setString(2, p+"");
						ps1.setString(3, restid);
						ps1.setString(4, p+"");
						//System.out.println(ps1.toString());
						rs1=ps1.executeQuery();
						JSONObject taxesjson=new JSONObject();
						if(rs1.next())
						{
							/*
							 * For restaurant discounts different or not check
							 */
							if((restaurantdiscount.length()!=0&&!restaurantdiscount.equalsIgnoreCase(rs1.getString("Discount")))||(restaurantdiscounttype.length()!=0&&!restaurantdiscounttype.equalsIgnoreCase(rs1.getString("Discount_Type"))))
							{
								restaurantdiscountdiffernt.add(restname);
							}
							/*
							 * 
							 */
							boolean taxonadch=false;
							if(rs1.getString("TaxOnAdditionalCharges").equalsIgnoreCase("1"))
								taxonadch=true;
							if(!rs1.getString("VAT").equalsIgnoreCase("0"))
							{
								JSONObject taxes=createTaxFieldFormula(rs1.getString("VAT"),rs1.getString("vat_type"),taxonadch);
								taxesjson.put("1", taxes);
							}
							if(!rs1.getString("ServiceTax").equalsIgnoreCase("0"))
							{
								JSONObject taxes=createTaxFieldFormula(rs1.getString("ServiceTax"),rs1.getString("st_type"),taxonadch);
								taxesjson.put("2", taxes);
							}
							if(rs1.getString("ServiceCharge").contains("%"))
							{
								if(!rs1.getString("ServiceCharge").replace("%", "").equalsIgnoreCase("0"))
								{
									JSONObject taxes=createTaxFieldFormula(rs1.getString("ServiceCharge").replace("%", ""),rs1.getString("sc_type"),taxonadch);
									taxesjson.put("3", taxes);
								}
							}else
							{
								if(!rs1.getString("ServiceCharge").equalsIgnoreCase("0"))
								{
									JSONObject formulajson=new JSONObject();
									formulajson.put("taxon", rs1.getString("ServiceCharge"));
									formulajson.put("type", "0");
									taxesjson.put("3", formulajson);
								}
							}
							/*
							 * For packaging charges
							 */
							if(rs1.getString("packaging_charges").contains("%"))
							{
								if(!rs1.getString("packaging_charges").replace("%", "").equalsIgnoreCase("0"))
								{
									JSONObject taxes=createTaxFieldFormula(rs1.getString("packaging_charges").replace("%", ""),"100*(0)",false);
									taxesjson.put("4", taxes);
								}
							}else
							{
								if(!rs1.getString("packaging_charges").equalsIgnoreCase("0"))
								{
									JSONObject formulajson=new JSONObject();
									formulajson.put("taxon", rs1.getString("packaging_charges"));
									formulajson.put("type", "0");
									taxesjson.put("4", formulajson);
								}
							}
							/*
							 * delivary_charge
							 */
							if(rs1.getString("delivary_charge").contains("%"))
							{
								if(!rs1.getString("delivary_charge").replace("%", "").equalsIgnoreCase("0"))
								{
									JSONObject taxes=createTaxFieldFormula(rs1.getString("delivary_charge").replace("%", ""),"100*(0)",false);
									taxesjson.put("5", taxes);
								}
							}else
							{
								if(!rs1.getString("delivary_charge").equalsIgnoreCase("0"))
								{
									JSONObject formulajson=new JSONObject();
									formulajson.put("taxon", rs1.getString("delivary_charge"));
									formulajson.put("type", "0");
									taxesjson.put("5", formulajson);
								}
							}
							/*
							 * ServiceTaxonDelivery
							 */
							if(rs1.getString("ServiceTaxonDelivery").contains("%"))
							{
								if(!rs1.getString("ServiceTaxonDelivery").replace("%", "").equalsIgnoreCase("0"))
								{
									JSONObject taxes=createTaxFieldFormula(rs1.getString("ServiceTaxonDelivery").replace("%", ""),"100*()+5",false);
									taxesjson.put("6", taxes);
								}
							}else
							{
								if(!rs1.getString("ServiceTaxonDelivery").equalsIgnoreCase("0"))
								{
									JSONObject formulajson=new JSONObject();
									formulajson.put("taxon", rs1.getString("ServiceTaxonDelivery"));
									formulajson.put("type", "0");
									taxesjson.put("6", formulajson);
								}
							}
							/*
							 * AdditionalCharges
							 */
							if(rs1.getString("AdditionalCharges").contains("%"))
							{
								if(!rs1.getString("AdditionalCharges").replace("%", "").equalsIgnoreCase("0"))
								{
									JSONObject taxes=createTaxFieldFormula(rs1.getString("AdditionalCharges").replace("%", ""),"100*(0)",false);
									taxesjson.put("7", taxes);
								}
							}else
							{
								if(!rs1.getString("AdditionalCharges").equalsIgnoreCase("0"))
								{
									JSONObject formulajson=new JSONObject();
									formulajson.put("taxon", rs1.getString("AdditionalCharges"));
									formulajson.put("type", "0");
									taxesjson.put("7", formulajson);
								}
							}
							taxformulasmap.put(restid+"-"+p, taxesjson);
							boolean jsonequal=false;
							JSONObject temp1;
							Iterator<JSONObject> it=formulaslist.iterator();
							String taxformulaid="0";
							while(it.hasNext())
							{
								JSONObject temp=it.next();
								if(CompareJsonObjects.jsonsEqual(temp, taxesjson))
								{
									jsonequal=true;
									temp1=formulas_restnames.get(temp);
									temp1.put(restname, restid+"-"+p);
									formulas_restnames.put(temp, temp1);
									ps2.setString(1, temp.toString());
									rs2=ps2.executeQuery();
									if(rs2.next())
									{
										taxformulaid=rs2.getString("TaxField_Id");
										ps8.setString(1, restname+",");
										ps8.setString(2, taxformulaid);
										ps8.executeUpdate();
									}
									break;
								}
							}
							if(jsonequal==false)
							{
								formulaslist.add(taxesjson);
								temp1=new JSONObject();
								temp1.put(restname, restid+"-"+p);
								formulas_restnames.put(taxesjson, temp1);
								ps3.setString(1, "formula"+fomulascount);
								ps3.setString(2, restname+",");
								ps3.setString(3, "1");
								ps3.setString(4, "2");
								ps3.executeUpdate();
								rs3=ps3.getGeneratedKeys();
								int autoIncValue = -1;
								if(rs3.next())
								{
									autoIncValue = rs3.getInt(1);
									taxformulaid=autoIncValue+"";
									ps4.setString(1, taxformulaid);
									ps4.setString(2, rs1.getString("DateTime"));
									ps4.setString(3, taxesjson.toString());
									ps4.executeUpdate();
									fomulascount++;
								}
							}
							if(!taxformulaid.equalsIgnoreCase("0"))
							{
								String newpricetypeid="0";
								if(p==1)
								{
									if(rs1.getString("IncludeTaxFlag").equalsIgnoreCase("1")||rs1.getString("IncludeTaxFlag").equalsIgnoreCase("1-1"))
									{
										newpricetypeid="6";
									}else if(rs1.getString("IncludeTaxFlag").equalsIgnoreCase("1-0"))
									{
										newpricetypeid="4";
									}else if(rs1.getString("IncludeTaxFlag").equalsIgnoreCase("0-1"))
									{
										newpricetypeid="3";
									}else
									{
										newpricetypeid="5";
									}
								}else
								{
									newpricetypeid="2";
								}
								ps5.setString(1, newpricetypeid);
								ps5.setString(2, p+"");
								ps5.setString(3, restid);
								ps5.executeUpdate();
								
								ps6.setString(1, taxformulaid);
								ps6.setString(2, newpricetypeid);
								ps6.setString(3, p+"");
								ps6.setString(4, restid);
								ps6.executeUpdate();
								
								ps7.setString(1, newpricetypeid);
								ps7.setString(2, p+"");
								ps7.setString(3, restid);
								ps7.executeUpdate();
								
								ps9.setString(1, rs1.getString("Discount"));
								ps9.setString(2, rs1.getString("Discount_Type"));
								ps9.setString(3, restid);
								System.out.println(ps9.toString());
								ps9.executeUpdate();
							}else
							{
								System.out.println(restname+" does not added taxformula");
							}
							
						}else
						{
							taxnothavemap.put(restname, p+"");
						}
					}
					
				}
				//System.out.println("restaurantdiscountdiffernt "+restaurantdiscountdiffernt);
				//System.out.println("taxes not having restaurants\n"+taxnothavemap);
				//System.out.println("\n formulaslist length \n"+formulaslist.size());
				//System.out.println("\n formulaslist \n"+formulaslist);
				//System.out.println("\n taxformulasmap with restaurants\n"+taxformulasmap);
				System.out.println("\n formulas_restaurants"+formulas_restnames);
				con.commit();
				System.out.println("\n\n successfully completed");
			}else
			{
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" createFormulas "+e.toString();
			try
			{
				con.rollback();
			}catch (Exception e1) {
			}
		}finally
		{
			try
			{
				if(ps!=null)
					ps.close();
				if(con!=null)
					con.close();				
				if(!errorlogfinal.equals(""))
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" createFormulas ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("createFormulas"+e.toString());
			}
		}
	}
	public JSONObject createTaxFieldFormula(String tax,String taxtype,boolean adchontax)
	{
		JSONObject resultjson=new JSONObject();
		try
		{
			//System.out.println(tax+" "+taxtype);
			String taxof="";
			String taxof1="";
			String formulatext=tax+"% On";
			String[] taxparts=taxtype.split("\\*");
			String[] stringarray,stringarray1;
			if(taxparts.length>0)
			{
				taxof+=taxparts[0]+"*(";
				formulatext+=taxparts[0]+"% Of(";
				stringarray=taxparts[1].split("\\)");
				if(stringarray.length>0)
				{
					stringarray1=stringarray[0].replace("(", "").split("\\+");
					for(int p=0;p<stringarray1.length;p++)
					{
						if(stringarray1[p].length()>0)
						{
							if(stringarray1[p].equalsIgnoreCase("0"))
							{
								taxof+="0";
								formulatext+="Total Bill";
							}else if(stringarray1[p].equalsIgnoreCase("1"))
							{
								taxof+="+1";
								formulatext+="+VAT";
							}else if(stringarray1[p].equalsIgnoreCase("2"))
							{
								taxof+="+2";
								formulatext+="+ST";
							}else if(stringarray1[p].equalsIgnoreCase("3"))
							{
								taxof+="+3";
								formulatext+="+SC";
							}else if(stringarray1[p].equalsIgnoreCase("4"))
							{
								taxof+="+4";
								formulatext+="+PC";
							}else if(stringarray1[p].equalsIgnoreCase("5"))
							{
								taxof+="+5";
								formulatext+="+DC";
							}
						}
					}
					taxof+=")";
					formulatext+=")";
					if(stringarray.length>1)
					{
						stringarray1=stringarray[1].split("\\+");
						for(int p=0;p<stringarray1.length;p++)
						{
							if(stringarray1[p].length()>0)
							{
								if(stringarray1[p].equalsIgnoreCase("1"))
								{
									taxof+="+1";
									formulatext+="+VAT";
								}else if(stringarray1[p].equalsIgnoreCase("2"))
								{
									taxof+="+2";
									formulatext+="+ST";
								}else if(stringarray1[p].equalsIgnoreCase("3"))
								{
									taxof+="+3";
									formulatext+="+SC";
								}else if(stringarray1[p].equalsIgnoreCase("4"))
								{
									taxof+="+4";
									formulatext+="+PC";
								}else if(stringarray1[p].equalsIgnoreCase("5"))
								{
									taxof+="+5";
									formulatext+="+DC";
								}
							}
						}
					}
					if(adchontax)
					{
						taxof+="+7";
						formulatext+="+ADCH";
					}
				}
			}else
			{
				
			}
			if(taxof.length()==0&&taxof1.length()==0)
			{
			}else
			{
				JSONObject formulajson=new JSONObject();
				formulajson.put("taxon", tax);
				formulajson.put("type", "1");
				formulajson.put("taxof", taxof);
				formulajson.put("taxof1", taxof1);
				formulajson.put("taxoftext", formulatext);
				resultjson=formulajson;
			}
			//System.out.println(resultjson);
		}catch (Exception e) {
			System.out.println("Exception :"+e.toString());
		}
		return resultjson;
	}
}
