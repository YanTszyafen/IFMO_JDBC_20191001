package com.efimchick.ifmo.web.jdbc;


/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    public String select01 = "SELECT * from EMPLOYEE ORDER BY LASTNAME";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    public String select02 = "SELECT * from EMPLOYEE where LENGTH(LASTNAME)<=5 ORDER BY LASTNAME";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    public String select03 = "SELECT * from EMPLOYEE where SALARY>=2000 and SALARY<=3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    public String select04 = "SELECT * from EMPLOYEE where SALARY<=2000 or SALARY>=3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    public String select05 = "SELECT EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, DEPARTMENT.NAME from EMPLOYEE INNER JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.ID";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select06 = "SELECT EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, DEPARTMENT.NAME AS depname from EMPLOYEE LEFT JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.ID";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    public String select07 = "SELECT SUM(EMPLOYEE.SALARY) AS total from EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    public String select08 = "SELECT DEPARTMENT.NAME AS depname, COUNT(DEPARTMENT.NAME) AS staff_size from EMPLOYEE JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.ID GROUP BY DEPARTMENT.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select09 = "SELECT DEPARTMENT.NAME AS depname, SUM(EMPLOYEE.SALARY) AS total, AVG(EMPLOYEE.SALARY) AS average from EMPLOYEE JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.ID GROUP BY DEPARTMENT.NAME";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    public String select10 = "SELECT employee_.LASTNAME AS employee, manager_.LASTNAME AS manager from EMPLOYEE employee_ LEFT JOIN EMPLOYEE manager_ ON employee_.MANAGER = manager_.ID";


}
