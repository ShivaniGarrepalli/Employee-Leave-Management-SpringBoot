package com.example.hospitalappointment.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hospitalappointment.model.Employee;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    private final String url = "jdbc:mysql://localhost:3306/employee_leave_management";
    private final String username = "root";
    private final String password = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Employee save(Employee employee) {
        String sql;

        if (employee.getEmployeeId() == null) {
            sql = "INSERT INTO employees "
                    + "(employee_name, email, password, role, casual_leave_balance, "
                    + "sick_leave_balance, earned_leave_balance) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE employees SET employee_name=?, email=?, password=?, "
                    + "role=?, casual_leave_balance=?, sick_leave_balance=?, "
                    + "earned_leave_balance=? WHERE employee_id=?";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getEmployeeName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPassword());
            ps.setString(4, employee.getRole());
            ps.setInt(5, employee.getCasualLeaveBalance());
            ps.setInt(6, employee.getSickLeaveBalance());
            ps.setInt(7, employee.getEarnedLeaveBalance());

            if (employee.getEmployeeId() == null) {
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        employee.setEmployeeId(rs.getLong(1));
                    }
                }
            } else {
                ps.setLong(8, employee.getEmployeeId());

                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException(
                            "Employee not found with ID: " + employee.getEmployeeId());
                }
            }

            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving employee: " + e.getMessage(), e);
        }
    }

    @Override
    public Employee findById(Long id) {
        String sql = "SELECT * FROM employees WHERE employee_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Employee findByEmail(String email) {
        String sql = "SELECT * FROM employees WHERE email=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding employee by email: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM employees WHERE email=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error checking employee email: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error retrieving employees: " + e.getMessage(), e);
        }

        return employees;
    }

    @Override
    public void delete(Employee employee) {
        String sql = "DELETE FROM employees WHERE employee_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, employee.getEmployeeId());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException(
                        "Employee not found with ID: " + employee.getEmployeeId());
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting employee: " + e.getMessage(), e);
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();

        employee.setEmployeeId(rs.getLong("employee_id"));
        employee.setEmployeeName(rs.getString("employee_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPassword(rs.getString("password"));
        employee.setRole(rs.getString("role"));
        employee.setCasualLeaveBalance(rs.getInt("casual_leave_balance"));
        employee.setSickLeaveBalance(rs.getInt("sick_leave_balance"));
        employee.setEarnedLeaveBalance(rs.getInt("earned_leave_balance"));

        return employee;
    }
}