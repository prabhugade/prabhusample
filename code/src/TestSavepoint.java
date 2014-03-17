import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class TestSavepoint 
{
	public static void main(String[] args) 
	{
		Connection con=null;
		Statement st=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","silica");
			st=con.createStatement();
			con.setAutoCommit(false);
			int i=st.executeUpdate("INSERT INTO `data`(dataname2, dataname1) VALUES ('2','3')");
			System.out.println(i);
			ResultSet rs=st.executeQuery("select sum(`dataname2`) as sum from data");
			while(rs.next())
				System.out.println(rs.getString("sum"));
		}catch (Exception e) {
			System.out.println(e);
		}
	}
}
