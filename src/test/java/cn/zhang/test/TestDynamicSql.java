package cn.zhang.test;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import cn.zhang.beans.Employee;
import cn.zhang.dao.EmployeeMapper;

public class TestDynamicSql {
	
	private static final Logger logger = LogManager.getLogger(TestDynamicSql.class.getName());

	public SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream in = Resources.getResourceAsStream(resource);
		return new SqlSessionFactoryBuilder().build(in);
	}
	
	/**
	 * 测试IF标签
	 * @throws IOException
	 */
	@Test
	public void test01() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = new Employee(null, "Jack2", null, null);
			List<Employee> emps = mapper.getEmpsByConditionIf(emp);
			logger.info(emps);
			// 查询的时候如果某些条件可能没有  这样硬拼接的sql就会有问题
			// 1、给where后面加上1=1，以后的条件都需要增加上and 或者 or[此时拼出的sql语句就是select * from table where 1=1 and ...]
			// 2、使用mybatis自带的where标签来将所有的查询条件包括在内[此时拼出的sql语句就是我们希望的语句，mybatis会自动的将是需要的and或者or连接符拿掉]
			// 		但是where标签只会将前面的and或者or连接符去掉   如果将连接符放到后面  则不会去掉   所以希望使用where标签是还是将and或者or连接符放到开始
		} finally {
			session.close();
		}
	}
	
	/**
	 * 测试TRIM标签
	 * @throws IOException 
	 */
	@Test
	public void test02() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = new Employee(null, "Jack2", null, null);
			List<Employee> emps = mapper.getEmpsByConditionTrim(emp);
			logger.info(emps);
		} finally {
			session.close();
		}
	}
	
	/**
	 * 测试CHOOSE分支语句
	 * @throws IOException
	 */
	@Test
	public void test03() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = new Employee(1, "Ja%", null, null);
			List<Employee> emps = mapper.getEmpsByConditionChoose(emp);
			logger.info(emps);
		} finally {
			session.close();
		}
	}
	
	/**
	 * 测试SET更新语句
	 * @throws IOException 
	 */
	@Test
	public void test04() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = new Employee(1, "Tom", null, null);
			mapper.updateEmp(emp);
			session.commit();
		} finally {
			session.close();
		}
	}
	
	/**
	 * 测试简单的FOR-EACH查询
	 * @throws IOException
	 */
	@Test
	public void test05() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			List<Employee> emps = mapper.getEmpsByConditionForEach(Arrays.asList(1, 3, 4));
			logger.info(emps);
		} finally {
			session.close();
		}
	}
	
	/**
	 * 使用FOR-EACH进行简单的批量保存
	 * @throws IOException 
	 */
	@Test
	public void test06() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			List<Employee> emps = new ArrayList<>();
			emps.add(new Employee(null, "Tom", "Tom@163.com", "1"));
			emps.add(new Employee(null, "Smith", "Smith@sina.com", "0"));
			emps.add(new Employee(null, "Tony", "Tony@163.com", "1"));
			mapper.addEmps(emps);
			session.commit();
		} finally {
			session.close();
		}
	}
	
	/**
	 * 进行内置参数的使用的测试
	 */
	@Test
	public void test07() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp = new Employee();
			emp.setLastName("o");
			List<Employee> emps = mapper.getEmpsByConditionInnerParam(emp);
			logger.info(emps);
		} finally {
			session.close();
		}
	}
}
