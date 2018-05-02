import java.sql.*;
public class SQL {
	public static void main(String[] args){
		String classname="com.mysql.jdbc.Driver";
		String jdbcURL="jdbc:mysql://db.mis.kuas.edu.tw/s1104137246";
		String UID="s1104137246";
		String PWD="3015";
		Connection conn=null;
		try{
			Class.forName(classname).newInstance();
			conn=DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement=conn.createStatement();
			String query="Select * from student";
			String insert="INSERT INTO `student` (`id`, `name`, `age`, `gender`) VALUES ('9', '9', '9', '7')";
			String delete="delete from student where gender=7";
			aStatement.executeUpdate(delete);
			aStatement.executeUpdate(insert);
			ResultSet rs=aStatement.executeQuery(query);
			ResultSetMetaData meta=rs.getMetaData();
			int col=meta.getColumnCount();
			for(int i=1;i<=col;i++){
				if(i>1){
					System.out.print("\t");
				}
				System.out.print(meta.getColumnLabel(i));
			}
			System.out.print("\n");
			while(rs.next()){
				for(int i=1;i<=4;i++){
					if(i>1){
						System.out.print("\t");
					}
					System.out.print(rs.getString(i));
				}
				System.out.print("\n");
			}
			
			aStatement.close();
			conn.close();
		}catch(Exception e){
			System.out.println("shit");
		}
	}
}
