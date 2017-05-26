package DbConnect.ConnectPool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PooledConnection {
	//真正的物理接连对象
	
	private Connection connection;
	
	//定义一个boolean类型参数,用来控制连接是否空闲
	
	private boolean isBusy = false;
	
	public PooledConnection(Connection connection,boolean isBusy){
		this.connection = connection;
		this.isBusy = isBusy;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}
	
	public void close(){
		this.isBusy = false;
	}
	
	/**
	 * 查询数据
	 * @throws SQLException 
	 */
	public ResultSet queryBySql(String sql) throws SQLException{
		Statement sm = null;
		ResultSet rs = null;
		try {
			sm = connection.createStatement();
			rs = sm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 数据更新
	 */
	public int updateBySql(String sql){
		Statement sm = null;
		int rows = -1;
		try {
			sm = connection.createStatement();
			rows = sm.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}
	
	public int deleteBySql(String sql){
		return updateBySql(sql);
	}
}
