package DbConnect.ConnectPool.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

public class IMyPoolImpl implements IMyPool {

	// 准备好连接数据库的连接参数
	private static String jdbcDirver = "";
	private static String jdbccurl = "";
	private static String userName = "";
	private static String passWord = "";
	private static int initCount;
	private static int stepSize;
	private static int poolMaxSize;
	private static int timeout = 2000;
	// 这个封装一个连接对象的池的数据结构
	private static Vector<PooledConnection> poolConections = new Vector<PooledConnection>();


	
	public IMyPoolImpl(){
		init();
	}
	
	public void init(){
		InputStream in = IMyPool.class.getResourceAsStream("/jdbc.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (Exception e) {
			// TODO: handle exception
		}
		jdbcDirver = pro.getProperty("jdbcDirver");
		jdbccurl = pro.getProperty("jdbccurl");
		userName = pro.getProperty("userName");
		passWord = pro.getProperty("passWord");
		initCount = Integer.valueOf(pro.getProperty("initCount"));
		stepSize = Integer.valueOf(pro.getProperty("stepSize"));
		poolMaxSize = Integer.valueOf(pro.getProperty("poolMaxSize"));

		// 创建数据库连接
		try {
			// 拿到驱动实例
			Driver driver = (Driver) Class.forName(jdbcDirver).newInstance();
			// 将驱动注册
			DriverManager.registerDriver(driver);

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createConnections(initCount);
	}

	/**
	 * 获取数据库连接对象
	 */
	public synchronized PooledConnection getConnection() {
		if(poolConections.size() <= 0){
			System.out.println("连接池中没有连接对象");
			throw new RuntimeException("获取连接对象失败，原因是数据库连接中没有对象");
		}
		
		//获取真实连接
		PooledConnection connections = getRealConnection();
		while(connections == null){
			//证明里面都是正在占用的 需要扩容,再拿一次
			createConnections(stepSize);
			//再拿一次
			connections = getRealConnection();
			try {
				//等待线程拿连接
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return connections;
	}

	/**
	 * 获取空闲连接
	 * 
	 * @return
	 */
	private synchronized PooledConnection getRealConnection() {
		// 判读是否有空闲对象
		for (PooledConnection poolConn : poolConections) {
			// 代表不是繁忙的
			if (!poolConn.isBusy()) {
				// 拿物理连接对象
				Connection conn = poolConn.getConnection();
				// 判断连接是否有效
				try {
					// 判断物理连接是否有效 ,timeout:等待返回的结果的时间,不能无休止的等待
					if (conn.isValid(timeout)) {
						System.out.println(timeout);
						Connection isValidConn = DriverManager.getConnection(jdbccurl, userName, passWord);
						System.out.println("获取线程池");
						poolConn.setConnection(isValidConn);
						System.out.println(isValidConn);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 设置为正在用
			poolConn.setBusy(true);
			return poolConn;
		}
		return null;
	}

	/**
	 * 创建连接 循环创建连接池 先判断最大连接池数量是否可用
	 * 
	 * @param count
	 *            创建连接的数量
	 */
	public   void createConnections(int count) {

		for (int i = 0; i < count; i++) {
			if (poolMaxSize > 0 && poolConections.size() + count > poolMaxSize) {
				System.out.println("创建数据库连接对象失败，原因是将超过上限值");
				throw new RuntimeException("创建数据库连接对象失败，原因是将超过上限值");
			}
		}
		// 满足条件，则创建连接对象情况
		try {
			Connection connection = DriverManager.getConnection(jdbccurl, userName, passWord);
			PooledConnection pooledConnection = new PooledConnection(connection, false);
			poolConections.add(pooledConnection);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
