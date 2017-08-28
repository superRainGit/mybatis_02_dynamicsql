package cn.zhang.beans;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias(value = "employee")
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String lastName;
	private String email;
	private String gender;
	private Department dept;

	public Department getDept() {
		return dept;
	}

	public void setDept(Department dept) {
		this.dept = dept;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLastname() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Employee() {
	}

	public Employee(Integer id, String lastName, String email, String gender) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", dept="
				+ dept + "]";
	}

}
