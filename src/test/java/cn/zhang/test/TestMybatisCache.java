package cn.zhang.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.zhang.beans.Employee;
import cn.zhang.dao.EmployeeMapper;

public class TestMybatisCache {
	
	private static final Logger logger = LogManager.getLogger(TestMybatisCache.class.getName());

	public SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream in = Resources.getResourceAsStream(resource);
		return new SqlSessionFactoryBuilder().build(in);
	}
	
	/**
	 * 两级缓存
	 * 	一级缓存[本地缓存]:sqlSession级别的缓存。一级缓存是一直开启的。[sqlSession级别是一个Map]
	 * 		与数据库同一次会话期间查询到的数据会放在本地缓存中
	 * 		以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库
	 * 		一级缓存失效的情况(没有使用到当前一级缓存的情况,效果就是,还需要再向数据库发出查询):
	 * 		1、sqlSession不同
	 * 		2、sqlSession相同，查询条件不同(当前一级缓存中没有这个数据)
	 * 		3、sqlSession相同，两次查询之间进行了增删改操作(这次增删改可能对当前数据有影响)
	 * 		4、sqlSession相同，手动清除了一级缓存
	 * 	二级缓存[全局缓存]:基于namespace级别的缓存:一个namespace对应一个二级缓存
	 * 		工作机制:
	 * 		1、一个会话，查询一条数据，这个数据就会被放在当前会话的一级缓存中
	 * 		2、如果会话关闭[或者被提交]，一级缓存中的数据会被保存到二级缓存中；新的会话查询信息，就可以参照二级缓存中的内容
	 * 		3、sqlSession-->EmployeeMapper-->Employee
	 * 					-->DepartmentMapper-->Department
	 * 			这样查出来的数据即使使用的是同一个sqlSession，但是不同namespace查出的数据会放在自己对应的缓存(map)中
	 * 			注意:查出的数据都会默认先放在一级缓存中，只有会话提交或者关闭以后，一级缓存中的数据才会转移到二级缓存中
	 * 		使用步骤:
	 * 		1、开启全局二级缓存的配置:<setting name="cacheEnabled" value="true"/>
	 * 		2、去XxxMapper.xml中配置使用二级缓存:<cache></cache>
	 * 		3、我们的POJO需要实现序列化接口
	 * 和缓存有关的设置/属性
	 * 		1、cacheEnabled=true/false:默认是true，如果改为false，则是关闭缓存(二级缓存关闭)[注:一级缓存一直是可用的]
	 * 		2、每一个select标签都有useCache="true"属性:如果改为false，则不使用的哪个缓存呢?一级缓存是可用的，关闭的也是二级缓存
	 * 		3、每个增删改标签都会有一个标签:flushCache="true":
	 * 			默认增删改执行完之后会清除一级缓存，二级缓存也会被清空
	 * 		       每个查询标签默认flushCache="false":默认是false,如果改为true，每次查询之后都会清空缓存；缓存是没有被使用的[包括一级缓存和二级缓存]
	 * 		4、sqlSession.clearCache():只是清除当前session的一级缓存
	 * 		5、localCacheScope:本地缓存作用域(默认是SESSION级别的缓存，会缓存在当前会话中)
	 * 			 STATEMENT:可以禁用一级缓存[但是一般没人去设置,使用的非常少]
	 * 第三方缓存整合:
	 * 		1、导入第三方缓存的Jar包
	 * 		2、导入与第三方缓存整合的适配包
	 * 		3、mapper.xml文件中使用自定义缓存<cache></cache>
	 */
	@Test
	public void testSecondLevelCache() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		SqlSession openSession2 = sqlSessionFactory.openSession();
		SqlSession openSession3 = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			EmployeeMapper mapper2 = openSession2.getMapper(EmployeeMapper.class);
			EmployeeMapper mapper3 = openSession3.getMapper(EmployeeMapper.class);
			Employee emp01 = mapper.getEmpById(1);
			logger.info(emp01);
			openSession.clearCache();
			openSession.close();
			// 第二次查询是从二级缓存中拿到的数据，并没有发送新的sql
			Employee emp02 = mapper2.getEmpById(1);
			logger.info(emp02);
			openSession2.close();
			Employee emp03 = mapper3.getEmpById(1);
			logger.info(emp03);
		} finally {
			openSession3.close();
		}
	}
	
	@Test
	public void testFirstLevelCache() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			Employee emp01 = mapper.getEmpById(1);
			logger.info(emp01);
			Employee emp02 = mapper.getEmpById(1);
			logger.info(emp02);
		} finally {
			openSession.close();
		}
	}
	
}
