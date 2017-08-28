package cn.zhang.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.zhang.beans.Employee;

public interface EmployeeMapper {
	
	public Employee getEmpById(Integer id);
	
	public List<Employee> getEmpsByConditionInnerParam(Employee e);

	// 携带了哪个字段查询条件就带上这个字段
	public List<Employee> getEmpsByConditionIf(Employee e);
	
	public List<Employee> getEmpsByConditionTrim(Employee e);
	
	public List<Employee> getEmpsByConditionChoose(Employee e);
	
	public void updateEmp(Employee e);
	
	// 查询员工id在给定集合中的员工
	public List<Employee> getEmpsByConditionForEach(List<Integer> ids);
	
	public void addEmps(@Param("emps")List<Employee> emps);
}
