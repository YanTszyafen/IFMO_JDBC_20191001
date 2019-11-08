package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        SetMapper<Set<Employee>> setMapper = new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                try {
                    Set<Employee> employees = new HashSet<>();
                    while (resultSet.next()) {
                        int row = resultSet.getRow();
                        BigInteger id = new BigInteger(String.valueOf(resultSet.getInt("id")));
                        Employee employee_ = employeeFactory(resultSet, id);
                        employees.add(employee_);
                        resultSet.absolute(row);
                    }
                    return employees;
                } catch (SQLException e) {
                    return null;
                }
            }

            private Employee employeeFactory(ResultSet resultSet, BigInteger ID) {
                try{
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String middleName = resultSet.getString("middleName");
                    FullName fullName = new FullName(firstName, lastName, middleName);
                    Position position = Position.valueOf(resultSet.getString("position"));
                    LocalDate hired = LocalDate.parse(String.valueOf(resultSet.getDate("hireDate")));
                    BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getBigDecimal("salary")));
                    if(resultSet.getInt("manager") == 0){
                        return new Employee(ID, fullName, position, hired, salary, null);
                    }
                    else {
                        BigInteger manager_id = new BigInteger(String.valueOf(resultSet.getInt("manager")));
                        Employee manager = null;
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            if (resultSet.getInt("id") == Integer.valueOf(String.valueOf(manager_id))) {
                                manager = employeeFactory(resultSet, manager_id);
                            }
                        }
                        return new Employee(ID, fullName, position, hired, salary, manager);
                    }
                }catch (SQLException e){
                    return null;
                }
            }
        };
        return setMapper;
    }
}
