package DbConnect.ConnectPool.db;

/**
 * 内部类的代理模式维护数据库连接类
 * @author WHB
 *
 */
public class PoolManager {
	private static class createPools{
		private static IMyPoolImpl poolImpl = new IMyPoolImpl();
		
	}
	
	/**
	 * 多个线程在记载这个内部类的时候 线程是互斥的，所以单利模式维护内部类的方式来避免线程混乱
	 * @return
	 */
	public static IMyPoolImpl getInstance(){
		return createPools.poolImpl;
	}
}
