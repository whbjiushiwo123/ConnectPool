package DbConnect.ConnectPool.db;


/**
 * 定义连接池的规范
 * @author WHB
 *
 */
public interface IMyPool {
	//获取连接  物理连接
	
	PooledConnection getConnection();
	
	//创建连接
	void createConnections(int count);

}
