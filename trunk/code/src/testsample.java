import org.json.JSONObject;


public class testsample {

	public static void main(String[] args) throws Exception
	{
		String s=" = 4=4";
		String[] sarray=s.split("\\ \\=");
		for(int p=0;p<sarray.length;p++)
			System.out.println(sarray[p]);
		
		String temp="123a";
		if(temp.matches("^[0-9]{3}"))
			System.out.println("wrong number");
		else
			System.out.println("right format");
		String tax="{\"1\":{\"taxoftext\":\"14.5% On+Aditional Charges+DC+SC\",\"taxof\":\"\",\"taxof1\":\"+4+5+2\",\"type\":\"1\",\"taxon\":\"14.5\"},\"5\":{\"type\":\"0\",\"taxon\":\"10\"},\"4\":{\"taxoftext\":\"10% On100% Of(Total Bill)\",\"taxof\":\"100*(0)\",\"taxof1\":\"\",\"type\":\"1\",\"taxon\":\"10\"}}";
		TaxCalculation cal=new TaxCalculation();
		System.out.println(cal.getTaxAmounts(new JSONObject(tax), "100", "0", "0", ""));
		
	}
}
