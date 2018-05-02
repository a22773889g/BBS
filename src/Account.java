import java.sql.*;

public class Account {
	private String classname = "com.mysql.jdbc.Driver";
	private String jdbcURL = "jdbc:mysql://db.mis.kuas.edu.tw/s1104137246";
	private String UID = "s1104137246";
	private String PWD = "3015";
	private Connection conn = null;
	private String pwd;
	String user;
	public Account(String user, String pwd) {
		this.user = user;
		this.pwd = pwd;
	}
//	public static void main(String[] args) {
//		Account s=new Account("mis","123");
//		s.delete("2");
//		s.list();
//	}
	void createAccount() {
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String insert = "INSERT INTO `account` (`user`, `pwd`) VALUES ('" + this.user + "','" + this.pwd + "')";
			aStatement.executeUpdate(insert);
			aStatement.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	boolean check() {
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "Select user,pwd from account where user='" + this.user + "' && pwd='" + this.pwd + "'";
			ResultSet rs = aStatement.executeQuery(query);
			return rs.next();
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}
	boolean checkUser() {
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "Select user from account where user='" + this.user + "'";
			ResultSet rs = aStatement.executeQuery(query);
			return rs.next();
		} catch (Exception e) {
			System.out.println(e);
		}
		return true;
	}

	String list() {
		String list = "";
		int count=1;
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "Select author,title from articles";
			ResultSet rs = aStatement.executeQuery(query);
			list += "文章\t作者\t標題\r\n";
			while (rs.next()) {
				list +=count;
				for (int i = 1; i <= 2; i++) {
					if (i >= 1) {
						list += "\t";
					}
					list += rs.getString(i);
				}
				count++;
				list += "\r\n";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.print(list);
		return list;
	}

	void article(String title, String content) {
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "INSERT INTO `articles` (`author`, `title`, `body`) VALUES ('" + this.user + "','" + title
					+ "','" + content + "')";
			aStatement.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	boolean authenticate(String id) {
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "Select author from articles where id=(select id from articles limit "+(Integer.parseInt(id)-1)+", 1)";
			ResultSet rs = aStatement.executeQuery(query);
			rs.next();
			if(this.user.equals(rs.getString(1))){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}
	String look(String id) {
		String content = "";
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query = "Select body from articles where id=(select id from articles limit "+(Integer.parseInt(id)-1)+", 1)";
			ResultSet rs = aStatement.executeQuery(query);
			rs.next();
			content += rs.getString(1);
			content += "\r\n";
		} catch (Exception e) {
			System.out.println(e);
		}
		return content;
	}
	void delete(String id){
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query ="select id from articles limit "+(Integer.parseInt(id)-1)+", 1";
			ResultSet rs = aStatement.executeQuery(query);
			rs.next();
			System.out.println(rs.getString(1));
			String delete="delete from articles where id='"+rs.getString(1)+"'";
			aStatement.executeUpdate(delete);
			query="ALTER TABLE articles AUTO_INCREMENT=1";
			aStatement.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	void update(String id,String content){
		try {
			Class.forName(classname).newInstance();
			conn = DriverManager.getConnection(jdbcURL, UID, PWD);
			Statement aStatement = conn.createStatement();
			String query ="select id from articles limit "+(Integer.parseInt(id)-1)+", 1";
			ResultSet rs = aStatement.executeQuery(query);
			rs.next();
			String update="update articles set body = '"+content+"' where id= '"+rs.getString(1)+"'";
			aStatement.executeUpdate(update);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
