
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaxCalculation {
	DecimalFormat df = new DecimalFormat("#.##");
	public JSONObject getTaxAmounts(JSONObject Taxformulasmap,String orgamount,String Discount,String Discount_type,String includedTaxes)
	{
		JSONObject result=new JSONObject();
		String errorlog="start";
		String errorlogfinal="";
		try
		{
			result.put(JsonKeys.status, "failed");
			String amount="0";
			if(Discount_type.equals("1"))
			{
				amount=check_discount(orgamount,Discount,Discount_type);
			}else
				amount=orgamount;	
			Iterator<String> it=Taxformulasmap.keys();
			String id;
			String value;
			HashMap<String, String> taxesmap=new HashMap<String, String>();
			while(it.hasNext())
			{
				id=it.next();
				if(!taxesmap.containsKey(id))
				{
					value=calculateValue(id,Taxformulasmap,taxesmap,amount);
					if(value!=null)
						taxesmap.put(id, value);
				}
			}
			JSONArray josndata=new JSONArray();
			JSONObject tempjson;
			if(taxesmap.size()>0)
			{
				it=taxesmap.keySet().iterator();
				int p=0;
				while(it.hasNext())
				{
					id=it.next();
					value=taxesmap.get(id);
					tempjson=new JSONObject();
					if(includedTaxes.startsWith(id+",")||includedTaxes.endsWith(","+id)||includedTaxes.contains(","+id+","))
					{
						tempjson.put(JsonKeys.include, "1");
					}else
						tempjson.put(JsonKeys.include, "0");
					tempjson.put(id, value);
					josndata.put(tempjson);
					p++;
				}
			}
			result.put("data", josndata);
			result.put(JsonKeys.status, "success");
		}
		catch (Exception e)
		{
			errorlogfinal=errorlog+" getTaxAmounts "+e.getMessage();
		}
		finally
		{
			try
			{
				result.put(JsonKeys.status, errorlogfinal);
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * This is inner method for getTaxAmounts
	 * @param amount
	 * @param discount
	 * @param type
	 * @return
	 */
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
	public String calculateValue(String id,JSONObject Taxformulasmap,HashMap<String, String> taxamt,String amount)
	{
		try
		{
			String value=Taxformulasmap.getString(id);
			JSONObject tempjson=new JSONObject(value);
			if(tempjson.getString(JsonKeys.type).equalsIgnoreCase("0"))
				return tempjson.getString(JsonKeys.taxon);
			else
			{
				double amt=0;
				String taxof=tempjson.getString(JsonKeys.taxof);
				if(taxof.length()>0&&taxof.contains("*"))
				{
					String percentage=taxof.split("\\*")[0];
					String percentof[]=taxof.split("\\*")[1].replace("(", "").replace(")", "").split("\\+");
					for(int p=0;p<percentof.length;p++)
					{
						if(percentof[p].length()>0)
						{
							if(percentof[p].equalsIgnoreCase("0"))
							{
								amt+=Double.parseDouble(amount);
							}else
							{
								if(taxamt.containsKey(percentof[p]))
								{
									amt+=Double.parseDouble(taxamt.get(percentof[p]));
								}else
								{
									if(Taxformulasmap.has(percentof[p]))
									{
										String val=calculateValue(id,Taxformulasmap,taxamt,amount);
										amt+=Double.parseDouble(val);
									}else
										taxamt.put(percentof[p], "0");
								}
							}
						}
					}
					amt=(amt*Double.parseDouble(percentage))/100;
				}
				taxof=tempjson.getString(JsonKeys.taxof1);
				if(taxof.length()>0)
				{
					String percentof[]=taxof.split("\\+");
					for(int p=0;p<percentof.length;p++)
					{
						if(percentof[p].length()>0)
						{
							if(percentof[p].equalsIgnoreCase("0"))
							{
								amt+=Double.parseDouble(amount);
							}else
							{
								if(taxamt.containsKey(percentof[p]))
								{
									amt+=Double.parseDouble(taxamt.get(percentof[p]));
								}else
								{
									if(Taxformulasmap.has(percentof[p]))
									{
										String val=calculateValue(percentof[p],Taxformulasmap,taxamt,amount);
										amt+=Double.parseDouble(val);
									}else
										taxamt.put(percentof[p], "0");
								}
							}
						}
					}
				}
				return df.format(amt*Double.parseDouble(tempjson.getString(JsonKeys.taxon))/100);
			}
		}catch (Exception e) {
		}
		return null;
	}
}
