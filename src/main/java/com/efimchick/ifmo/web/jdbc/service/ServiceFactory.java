package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceFactory {

    public EmployeeService employeeService() {

        EmployeeService empService = new EmployeeService() {

            private ResultSet getRS(String sql) throws SQLException {
                Statement statement = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery(sql);
                return resultSet;
            }

            private List<Employee> getEmployee(ResultSet resultSet, boolean chain){
                try{
                    List<Employee> employees = new ArrayList<Employee>();
                    while (resultSet.next()){
                        int row = resultSet.getRow();
                        BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
                        Employee employee = employeeFactory(resultSet, id, chain);
                        employees.add(employee);
                        resultSet.absolute(row);
                    }
                    return employees;
                }catch (SQLException ex){
                    return null;
                }
            }

            private Employee employeeFactory(ResultSet resultSet, BigInteger id, boolean chain){
                try{
                    FullName fullName = new FullName(resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("middlename"));
                    Position position = Position.valueOf(resultSet.getString("position"));
                    LocalDate hireDate = LocalDate.parse(String.valueOf(resultSet.getDate("hiredate")));
                    BigDecimal salary = resultSet.getBigDecimal("salary");
                    Department department = getDepartment(resultSet);
                    if(resultSet.getInt("manager") == 0){
                        return new Employee(id, fullName, position, hireDate, salary, null, department);
                    }
                    else {
                        BigInteger manager_id = new BigInteger(String.valueOf(resultSet.getInt("manager")));
                        Employee manager = null;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            if (resultSet.getInt("id") == Integer.valueOf(String.valueOf(manager_id))) {
                                if(!chain) manager = getManager(resultSet, manager_id);
                                else manager = employeeFactory(resultSet, manager_id, chain);
                            }
                        }
                        return new Employee(id, fullName, position, hireDate, salary, manager, department);
                    }
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
                return null;
            }

            private Department getDepartment(ResultSet resultSet) throws SQLException {
                return resultSet.getInt("department") !=0 ? new Department(BigInteger.valueOf(resultSet.getInt("department")),
                        resultSet.getString("name"),
                        resultSet.getString("location")) : null;
            }

            private Employee getManager(ResultSet resultSet, BigInteger id){
                try{
                    return (new Employee(id, new FullName(resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("middlename")),
                            Position.valueOf(resultSet.getString("position")),
                            LocalDate.parse(String.valueOf(resultSet.getDate("hiredate"))),
                            resultSet.getBigDecimal("salary"),
                            null,
                            getDepartment(resultSet))
                    );

                }catch (SQLException ex){
                    return null;
                }
            }

            private List<Employee> getList(Paging paging, String sql, boolean chain) throws SQLException {
                List<Employee> allSortByHireDate = new ArrayList<Employee>();
                List<Employee> all = getEmployee(getRS(sql), chain);
                int start = paging.itemPerPage*(paging.page-1);
                int number = paging.itemPerPage;
                while(number>0 && start<14){
                    allSortByHireDate.add(all.get(start));
                    start ++;
                    number --;
                }
                return allSortByHireDate;
            }

            private String getSQL(String sort){
                return "SELECT EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.MIDDLENAME, " +
                        "EMPLOYEE.POSITION, EMPLOYEE.MANAGER, EMPLOYEE.HIREDATE, EMPLOYEE.SALARY, EMPLOYEE.DEPARTMENT, " +
                        "DEPARTMENT.NAME, DEPARTMENT.LOCATION FROM EMPLOYEE LEFT JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT" +
                        " = DEPARTMENT.ID ORDER BY " + sort;
            }

            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                try {
                    return getList(paging,getSQL("EMPLOYEE.HIREDATE"), false);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                try {
                    return getList(paging,getSQL("EMPLOYEE.LASTNAME"), false);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                try {
                    return getList(paging,getSQL("EMPLOYEE.SALARY"), false);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                try {
                    return getList(paging,getSQL("DEPARTMENT.NAME, EMPLOYEE.LASTNAME"), false);
                } catch (SQLException e) {
                    return null;
                }
            }

            private List<Employee> getByDep(Department department, Paging paging, String sql, boolean chain) throws SQLException {
                List<Employee> empAllBySort = getEmployee(getRS(sql), chain);
                List<Employee> empByDep = new ArrayList<Employee>();
                int i = 0;
                while (i<14){
                    if(empAllBySort.get(i).getDepartment() != null && empAllBySort.get(i).getDepartment().equals(department)){
                        empByDep.add(empAllBySort.get(i));
                    }
                    i++;
                }
                List<Employee> empPage = new ArrayList<Employee>();
                int start = paging.itemPerPage*(paging.page-1);
                int number = paging.itemPerPage;
                while(number>0 && start<empByDep.size()){
                    empPage.add(empByDep.get(start));
                    start ++;
                    number --;
                }
                return empPage;
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                try{
                    return getByDep(department, paging, getSQL("EMPLOYEE.HIREDATE"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                try{
                    return getByDep(department, paging, getSQL("EMPLOYEE.SALARY"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                try{
                    return getByDep(department, paging, getSQL("EMPLOYEE.LASTNAME"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            private List<Employee> getByManager(Employee manager, Paging paging, String sql, boolean chain) throws SQLException {
                List<Employee> empAllBySort = getEmployee(getRS(sql), chain);
                List<Employee> empByManager = new ArrayList<Employee>();
                int i = 0;
                while (i<14){
                    if(empAllBySort.get(i).getManager() != null && empAllBySort.get(i).getManager().getId().equals(manager.getId())){
                        empByManager.add(empAllBySort.get(i));
                    }
                    i++;
                }
                List<Employee> empPage = new ArrayList<Employee>();
                int start = paging.itemPerPage*(paging.page-1);
                int number = paging.itemPerPage;
                while(number>0 && start<empByManager.size()){
                    empPage.add(empByManager.get(start));
                    start ++;
                    number --;
                }
                return empPage;
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                try{
                    return getByManager(manager, paging, getSQL("EMPLOYEE.LASTNAME"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                try{
                    return getByManager(manager, paging, getSQL("EMPLOYEE.HIREDATE"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                try{
                    return getByManager(manager, paging, getSQL("EMPLOYEE.SALARY"), false);
                }catch (SQLException ex){
                    return null;
                }
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    List<Employee> allemp = getEmployee(getRS(getSQL("EMPLOYEE.LASTNAME")), true);
                    int i = 0;
                    while (i<14){
                        if(allemp.get(i).getId().equals(employee.getId())){
                            return allemp.get(i);
                        }
                        i++;
                    }
                    return null;
                }catch (SQLException e){
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    List<Employee> empBySalary = getEmployee(getRS(getSQL("EMPLOYEE.SALARY DESC")), false);
                    List<Employee> empTop = new ArrayList<Employee>();
                    int i = 0;
                    while (i < 14){
                        if(empBySalary.get(i).getDepartment() != null && empBySalary.get(i).getDepartment().equals(department)){
                            empTop.add(empBySalary.get(i));
                        }
                        i++;
                    }
                    return empTop.get(salaryRank-1);
                }catch (SQLException e){
                    return null;
                }
            }
        };
        return empService;
    }
}
