package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class RowMapperFactory {

    public RowMapper<Employee> employeeRowMapper() {
        ConnectionSource connectionSource = ConnectionSource.instance();
        RowMapper<Employee> rowMapper = resultSet -> {
            try(Connection conn = connectionSource.createConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select * from EMPLOYEE")){
                rs.next();
                BigInteger id = new BigInteger(String.valueOf(resultSet.getInt("id")));
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String middleName = resultSet.getString("middleName");
                FullName fullName = new FullName(firstName, lastName, middleName);
                Position position = Position.valueOf(resultSet.getString("position"));
                LocalDate hired = LocalDate.parse(String.valueOf(resultSet.getDate("hiredate")));
                BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getBigDecimal("salary")));
                Employee employee = new Employee(id, fullName, position, hired, salary);
                return employee;
            }catch (SQLException e){
                return null;
            }
        };
        return rowMapper;
    }
}
