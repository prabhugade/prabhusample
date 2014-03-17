import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class tttttttt {

	public static void main(String[] args) throws Exception 
	{
		/*JSONObject temp1=new JSONObject();
		JSONObject temp2=new JSONObject();
		JSONObject temp3=new JSONObject();
		temp2.put("5%", "10");
		temp2.put("10%", "20");
		temp3.put("taxamt", temp2);
		temp3.put("include", "0");
		temp1.put("1", temp3);
		System.out.println(temp1.toString());*/
		System.out.println(amtformat(10.));
	}
	public static String amtformat(double value)
	{
		String returnamt=value+"";
		String[] amt=(returnamt).split("\\.");
		if(amt.length>1)
		{
			if(amt[1].length()==1)
				returnamt+="0";
			if(amt[1].length()==0)
				returnamt+="00";
		}else
			returnamt+=".00";
		return returnamt;
	}
}
