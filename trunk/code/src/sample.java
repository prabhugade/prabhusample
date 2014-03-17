import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONObject;

public class sample {

	TimeZone tz=null;	
	public sample()
	{
		tz=TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
	}
	public static void main(String[] args) 
	{
		
		String ordid="REst123456";
		if(ordid.toLowerCase().startsWith("rest"))
			System.out.println("contains");
		else
			System.out.println("not contains");
		String target = "FooBar";
	     target = target.replaceAll("(?i)foo", "");
	     System.out.println(target);
		//HashMap<String,HashMap<String, String>> dates=sam.getWeekDates("com.mysql.jdbc.Driver", "hydhouse_jb", "root", "silica", "2013-08-01 00:00:01", "2013-08-18 00:00:01");
		//System.out.println(dates);
		//System.out.println(sam.getTimeIntervels("2013-08-01 00:00:01", "2013-08-02 00:00:01","00", "10", "2"));
		//sam.updateTest();
	}
	public boolean isDouble(String Qty) 
	{
		return Qty.matches("^[+]?\\d{0,10}+(\\.{0,1}(\\d{0,3}))?$");
	}
	public void updateTest()
	{
		Connection con=null;
		PreparedStatement ps=null;
		try
		{
			con=getConnection("com.mysql.jdbc.Driver", "test1", "root","silica");
			if(con!=null)
			{
				ps=con.prepareStatement("update table1 as t1,table2 as t2 set t1.name=? where t1.productid=t2.productid and t2.productname='prod2'" , PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setString(1, "testingitem2");
				int k=ps.executeUpdate();
				if(k>0)
				{
					ResultSet reset=ps.getGeneratedKeys();
					if(reset.next())
						System.out.println(reset.getInt(1));
				}
			}else
				System.out.println("connection failed");
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	
	public HashMap<String,HashMap<String, String>> getWeekDates(String driver,String url,String user,String pwd,String fromdate,String todate)
	{
		HashMap<String,HashMap<String, String>> result=new HashMap<String,HashMap<String,String>>();
		try
		{
			HashMap<String,String> fromdatesmap=new HashMap<String,String>();
			HashMap<String,String> todatesmap=new HashMap<String,String>();
			HashMap<String,String> weeksmap=new HashMap<String, String>();
			weeksmap.put("1", "Sunday");
			weeksmap.put("2", "Monday");
			weeksmap.put("3", "Tuesday");
			weeksmap.put("4", "Wednesday");
			weeksmap.put("5", "Thursday");
			weeksmap.put("6", "Friday");
			weeksmap.put("7", "Saturday");
			String nextdate=nextDate(fromdate);
			String nextinvadddate=getInventoryAddDate(driver, url, user, pwd, nextdate);
			while(Double.parseDouble(fromdate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", ""))<Double.parseDouble(todate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", ""))&&Double.parseDouble(fromdate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", ""))!=Double.parseDouble(nextinvadddate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "")))
			{
				Calendar calendar = Calendar.getInstance(tz);
				Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromdate);
				calendar.setTime(date1);
				int i=calendar.get(Calendar.DAY_OF_WEEK);
				if(fromdatesmap.containsKey(i+""))
				{
					String fdate=fromdatesmap.get(""+i)+"_$_"+fromdate;
					String edate=todatesmap.get(""+i)+"_$_"+nextinvadddate;
					fromdatesmap.put(""+i, fdate);
					todatesmap.put(""+i, edate);
				}else
				{
					fromdatesmap.put(""+i, fromdate);
					todatesmap.put(""+i, nextinvadddate);
				}
				fromdate=nextinvadddate;
				nextdate=nextDate(fromdate);
				nextinvadddate=getInventoryAddDate(driver, url, user, pwd, nextdate);
				System.out.println(fromdate+" "+nextinvadddate);
			}
			result.put("fromdate",fromdatesmap);
			result.put("todate",todatesmap);
			result.put("week", weeksmap);
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return result;
	}
	
	public HashMap<String,HashMap<String, String>> getTimeIntervels(String fromdate,String todate,String from,String to,String intervel)
	{
		ArrayList<String> intervelsarray=new ArrayList<String>();
		HashMap<String, HashMap<String, String>> result=new HashMap<String, HashMap<String,String>>();
		int fromtime=Integer.parseInt(from);
		int totime=Integer.parseInt(to);
		int interveltime=Integer.parseInt(intervel);
		int nexttime=fromtime+interveltime;
		boolean check=true;
		if(fromtime>=totime)
			check=false;
		while(check&&fromtime<totime)
		{
			if(nexttime<=totime)
				intervelsarray.add((fromtime>9 ? fromtime:"0"+fromtime)+":00:00_$_"+((nexttime-1)>9 ? (nexttime-1):"0"+(nexttime-1))+":59:59_$_"+(fromtime>9 ? fromtime:"0"+fromtime)+"-"+(nexttime>9 ? nexttime:"0"+nexttime));
			else if((totime-fromtime)<interveltime)
			{
				check=false;
				intervelsarray.add((fromtime>9 ? fromtime:"0"+fromtime)+":00:00_$_"+((totime-1)>9 ? (totime-1):"0"+(totime-1))+":59:59_$_"+(fromtime>9 ? fromtime:"0"+fromtime)+"-"+(totime>9 ? totime:"0"+totime));
			}
			else
				check=false;
			fromtime=nexttime;
			nexttime+=interveltime;
		}
		HashMap<String,String> fromdatesmap=new HashMap<String,String>();
		HashMap<String,String> todatesmap=new HashMap<String,String>();
		boolean mapscheck=true;
		while(Double.parseDouble(fromdate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", ""))<Double.parseDouble(todate.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "")))
		{
			String datee=fromdate.split(" ")[0];
			for(int p=0;p<intervelsarray.size();p++)
			{
				String arr[]=intervelsarray.get(p).split("\\_\\$\\_");
				String interver=arr[2];
				if(fromdatesmap.size()==todatesmap.size()&&fromdatesmap.containsKey(interver))
				{
					fromdatesmap.put(interver, fromdatesmap.get(interver)+"_$_"+datee+" "+arr[0]);
					if(todatesmap.containsKey(interver))
					{
						todatesmap.put(interver, todatesmap.get(interver)+"_$_"+datee+" "+arr[1]);
					}else
						mapscheck=false;
				}else
				{
					fromdatesmap.put(interver, datee+" "+arr[0]);
					todatesmap.put(interver, datee+" "+arr[1]);
				}
			}
			fromdate=nextDate(fromdate);
		}
		if(mapscheck)
		{
			result.put("fromdate",fromdatesmap);
			result.put("todate",todatesmap);
		}
		return result;
	}
	
	public Connection getConnection(String driver,String url,String uname,String pwd)
	{
		Connection con=null;
		try
		{
			if(con==null)
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"+url,uname,pwd);
			}
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return con;
	}
	public JSONObject jsonDataconvertion(JSONObject data)
	{
		JSONObject jdata=new JSONObject();
		try
		{
		Iterator<String> it=data.keys();
		while(it.hasNext())
		{
			String key=it.next();
			jdata.put(key.toLowerCase(), data.getString(key));
		}
		}catch (Exception e) {
		}
		return jdata;
	}
	public String nextDate(String date)
	{
		String resultdate=null;
		Calendar calendar=Calendar.getInstance(tz);
		Date date1;
		try {
			//System.out.println(date);
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			calendar.setTime(date1);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			resultdate=new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())+" 00:00:01"; 
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resultdate;
	}
	public String getInventoryAddDate(String driver,String url,String user,String pwd,String date)
	{
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		String result=null;
		Connection con=null;
		Statement st=null;
		String curdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance(tz).getTime());
		try
		{
			con=getConnection(driver,url,user,pwd);
			if(con!=null)
			{
				st=con.createStatement();
				String fromdate=null;
				String lastinvdate=getDate(driver, url, user, pwd);
				if(Double.parseDouble(date.replaceAll("[^0-9]+", ""))<=Double.parseDouble(lastinvdate.replaceAll("[^0-9]+", "")))
				{
					String sd=date.substring(0, 10)+" 00:00:00";
					String ed=date.substring(0, 10)+" 23:59:59";
					String sql123="SELECT min(DateTime) as fromdate FROM `Inventory` WHERE DateTime BETWEEN '"+sd+"' AND '"+ed+"' AND kitchenid='kitchen'";
					errorlog=sql123;
					ResultSet rs=st.executeQuery(sql123);
					if(rs.next())
					{
						fromdate=rs.getString("fromdate");
						if(!rs.wasNull())
							fromdate=rs.getString("fromdate");
						else
						{
							boolean check=true;
							while(check)
							{
								date=nextDate(date);
								sd=date.substring(0, 10)+" 00:00:00";
								ed=date.substring(0, 10)+" 23:59:59";
								sql123="SELECT min(DateTime) as fromdate FROM `Inventory` WHERE DateTime BETWEEN '"+sd+"' AND '"+ed+"' AND kitchenid='kitchen'";
								errorlog=sql123;
								rs=st.executeQuery(sql123);
								if(rs.next())
								{
									fromdate=rs.getString("fromdate");
									if(!rs.wasNull())
									{
										fromdate=rs.getString("fromdate");
										check=false;
									}
								}
							}
						}
					}else
					{
						boolean check=true;
						while(check)
						{
							date=nextDate(date);
							sd=date.substring(0, 10)+" 00:00:00";
							ed=date.substring(0, 10)+" 23:59:59";
							sql123="SELECT min(DateTime) as fromdate FROM `Inventory` WHERE kitchenid='kitchen' AND DateTime BETWEEN '"+sd+"' AND '"+ed+"'";
							errorlog=sql123;
							rs=st.executeQuery(sql123);
							if(rs.next())
							{
								fromdate=rs.getString("fromdate");
								if(!rs.wasNull())
								{
									fromdate=rs.getString("fromdate");
									check=false;
								}
							}
						}
					}
				}else
					fromdate=curdate;
				result=fromdate;
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}catch(Exception e)
		{
			errorlogfinal=errorlog+"getInventoryAddDate"+e.toString();
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();
				if(!errorlogfinal.equals("")||errcheck)
				{
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("getInventoryAddDate"+e.toString());
			}
		}
		return result;
	}
	public String getDate(String driver,String url,String user,String pwd)
	{
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		String result=null;
		Connection con=null;
		Statement st=null;
		String curdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance(tz).getTime());
		try
		{

			con=getConnection(driver,url,user,pwd);
			if(con!=null)
			{
				st=con.createStatement();
				String fromdate=null;
				String sql123="select substr(max(DateTime),1,10) as maxdate from `Inventory` where kitchenid='kitchen'";
				errorlog=sql123;
				ResultSet rs=st.executeQuery(sql123);
				if(rs.next())
				{
					fromdate=rs.getString("maxdate");
					if(!rs.wasNull())
					{
						String sd=rs.getString("maxdate");
						sd=rs.getString("maxdate")+" 00:00:00";
						String ed=rs.getString("maxdate")+" 23:59:59";
						sql123="SELECT min(DateTime) as fromdate FROM `Inventory` WHERE DateTime BETWEEN '"+sd+"' AND '"+ed+"' AND kitchenid='kitchen'";
						errorlog=sql123;
						rs=st.executeQuery(sql123);
						if(rs.next())
						{
							fromdate=rs.getString("fromdate");
							if(!rs.wasNull())
								fromdate=rs.getString("fromdate");
							else
								fromdate=curdate;
						}else
						{
							fromdate=curdate;
						}
					}else
					{
						fromdate=curdate;
					}
				}else
				{
					fromdate=curdate;
				}
				result=fromdate;
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}catch(Exception e)
		{
			errorlogfinal=errorlog+" getDate "+e.toString();
		}finally
		{
			try
			{
				if(st!=null)
					st.close();
				if(con!=null)
					con.close();
				if(!errorlogfinal.equals("")||errcheck)
				{
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("getDate"+e.toString());
			}
		}
		return result;
	}
}
