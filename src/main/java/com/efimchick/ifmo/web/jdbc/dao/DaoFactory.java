package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    public EmployeeDao employeeDAO() {
        EmployeeDao empDao_ = new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                String sql = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId();
                List<Employee> emp = new ArrayList<Employee>();
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()){
                        emp.add(getEmployee(rs));
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
                return emp;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                String sql = "SELECT * FROM EMPLOYEE WHERE MANAGER = " + employee.getId();
                List<Employee> emp = new ArrayList<Employee>();
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        if(rs.getInt("manager") == Integer.valueOf(String.valueOf(employee.getId()))){
                            emp.add(getEmployee(rs));
                        }
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
                return emp;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                String sql = "SELECT * FROM EMPLOYEE WHERE ID = " + Id;
                Employee emp = null;
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        emp = getEmployee(rs);
                    }
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return Optional.ofNullable(emp);
            }

            @Override
            public List<Employee> getAll() {
                String sql = "SELECT * FROM EMPLOYEE";
                List<Employee> empAll = new ArrayList<Employee>();
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        empAll.add(getEmployee(rs));
                    }
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return empAll;
            }

            @Override
            public Employee save(Employee employee) {
                BigInteger id = employee.getId();
                FullName fullName = employee.getFullName();
                String firstName = fullName.getFirstName();
                String lastName = fullName.getLastName();
                String middleName = fullName.getMiddleName();
                Position position = employee.getPosition();
                LocalDate hired = employee.getHired();
                BigDecimal salary = employee.getSalary();
                BigInteger managerId = employee.getManagerId();
                BigInteger departmentId = employee.getDepartmentId();
                while(employeeDAO().getById(id).isPresent()){
                    employeeDAO().delete(employeeDAO().getById(id).get());
                }
                String sql = "INSERT INTO EMPLOYEE(ID, FIRSTNAME, LASTNAME, MIDDLENAME, POSITION, HIREDATE, SALARY, MANAGER, DEPARTMENT) VALUES("
                        + id + ", '" + firstName + "', '" + lastName + "', '" + middleName + "', '" + position + "', '" + hired + "', '"
                        + salary + "', " + managerId + ", " + departmentId + ");";
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    st.executeUpdate(sql);
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                BigInteger id = employee.getId();
                String sql = "DELETE FROM EMPLOYEE WHERE id=" + id;
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    st.executeUpdate(sql);
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        return empDao_;
    }

    private Employee getEmployee(ResultSet rs) throws SQLException {
        return new Employee(BigInteger.valueOf(rs.getInt("id")),
                new FullName(rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("middleName")),
                Position.valueOf(rs.getString("position")),
                LocalDate.parse(String.valueOf(rs.getDate("hireDate"))),
                new BigDecimal(String.valueOf(rs.getBigDecimal("salary"))),
                BigInteger.valueOf(rs.getInt("manager")),
                BigInteger.valueOf(rs.getInt("department")));
    }

    public DepartmentDao departmentDAO(){
        DepartmentDao depDao_ = new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                String sql = "SELECT * FROM DEPARTMENT WHERE ID = " + Id;
                Department dep = null;
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        dep = new Department(
                                BigInteger.valueOf(rs.getInt("id")),
                                rs.getString("name"),
                                rs.getString("location")
                        );
                    }
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return Optional.ofNullable(dep);
            }

            @Override
            public List<Department> getAll() {
                String sql = "SELECT * FROM DEPARTMENT";
                List<Department> depAll = new ArrayList<Department>();
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        Department dep = new Department(
                                BigInteger.valueOf(rs.getInt("id")),
                                rs.getString("name"),
                                rs.getString("location")
                        );
                        depAll.add(dep);
                    }
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return depAll;
            }

            @Override
            public Department save(Department department) {
                BigInteger id = department.getId();
                String name = department.getName();
                String location = department.getLocation();
                while(departmentDAO().getById(id).isPresent()){
                    departmentDAO().delete(departmentDAO().getById(id).get());
                }
                String sql = "INSERT INTO DEPARTMENT(ID, NAME, LOCATION) VALUES(" + id + ", '" + name + "', '" + location + "');";
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    st.executeUpdate(sql);
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                BigInteger id = department.getId();
                String sql = "DELETE FROM DEPARTMENT WHERE id=" + id;
                try{
                    Statement st = ConnectionSource.instance().createConnection().createStatement();
                    st.executeUpdate(sql);
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        return depDao_;
    }

}
