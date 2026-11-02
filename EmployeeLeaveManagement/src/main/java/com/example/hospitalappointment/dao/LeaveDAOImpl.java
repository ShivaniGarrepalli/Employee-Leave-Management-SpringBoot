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
import com.example.hospitalappointment.model.LeaveApplication;
import com.example.hospitalappointment.model.LeaveStatus;
import com.example.hospitalappointment.model.LeaveType;

@Repository
public class LeaveDAOImpl implements LeaveDAO {

    private final String url = "jdbc:mysql://localhost:3306/employee_leave_management";
    private final String username = "root";
    private final String password = "root";

    private final EmployeeDAO employeeDAO;

    public LeaveDAOImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public LeaveApplication save(LeaveApplication leave) {
        if (leave.getEmployee() == null
                || leave.getEmployee().getEmployeeId() == null) {
            throw new IllegalArgumentException(
                    "A valid employee is required to save a leave application");
        }

        if (leave.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type is required");
        }

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (leave.getAppliedDate() == null) {
            leave.setAppliedDate(java.time.LocalDate.now());
        }

        if (leave.getStatus() == null) {
            leave.setStatus(LeaveStatus.PENDING);
        }

        String sql;

        if (leave.getLeaveId() == null) {
            sql = "INSERT INTO leave_applications "
                    + "(employee_id, leave_type, start_date, end_date, reason, "
                    + "status, total_days, manager_comment, applied_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE leave_applications SET employee_id=?, leave_type=?, "
                    + "start_date=?, end_date=?, reason=?, status=?, total_days=?, "
                    + "manager_comment=?, applied_date=? WHERE leave_id=?";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, leave.getEmployee().getEmployeeId());
            ps.setString(2, leave.getLeaveType().name());
            ps.setDate(3, java.sql.Date.valueOf(leave.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(leave.getEndDate()));
            ps.setString(5, leave.getReason());
            ps.setString(6, leave.getStatus().name());
            ps.setInt(7, leave.getTotalDays());
            ps.setString(8, leave.getManagerComment());
            ps.setDate(9, java.sql.Date.valueOf(leave.getAppliedDate()));

            if (leave.getLeaveId() == null) {
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        leave.setLeaveId(rs.getLong(1));
                    }
                }
            } else {
                ps.setLong(10, leave.getLeaveId());

                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException(
                            "Leave application not found with ID: "
                                    + leave.getLeaveId());
                }
            }

            return leave;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error saving leave: " + e.getMessage(), e);
        }
    }

    @Override
    public LeaveApplication findById(Long id) {
        String sql = "SELECT * FROM leave_applications WHERE leave_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLeave(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding leave: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<LeaveApplication> findAll() {
        List<LeaveApplication> leaves = new ArrayList<>();
        String sql = "SELECT * FROM leave_applications";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                leaves.add(mapLeave(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error retrieving leaves: " + e.getMessage(), e);
        }

        return leaves;
    }

    @Override
    public List<LeaveApplication> findByEmployee(Employee employee) {
        if (employee == null || employee.getEmployeeId() == null) {
            throw new IllegalArgumentException("A valid employee is required");
        }

        return findByEmployeeEmployeeId(employee.getEmployeeId());
    }

    @Override
    public List<LeaveApplication> findByEmployeeEmployeeId(Long employeeId) {
        List<LeaveApplication> leaves = new ArrayList<>();
        String sql = "SELECT * FROM leave_applications WHERE employee_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    leaves.add(mapLeave(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding employee leaves: " + e.getMessage(), e);
        }

        return leaves;
    }

    @Override
    public List<LeaveApplication> findByStatus(LeaveStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Leave status is required");
        }

        List<LeaveApplication> leaves = new ArrayList<>();
        String sql = "SELECT * FROM leave_applications WHERE status=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    leaves.add(mapLeave(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding leaves by status: " + e.getMessage(), e);
        }

        return leaves;
    }

    @Override
    public boolean existsByEmployeeEmployeeIdAndStatus(
            Long employeeId, LeaveStatus status) {

        String sql = "SELECT COUNT(*) FROM leave_applications "
                + "WHERE employee_id=? AND status=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, employeeId);
            ps.setString(2, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error checking leave status: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public void delete(LeaveApplication leave) {
        String sql = "DELETE FROM leave_applications WHERE leave_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, leave.getLeaveId());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException(
                        "Leave application not found with ID: "
                                + leave.getLeaveId());
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting leave: " + e.getMessage(), e);
        }
    }

    private LeaveApplication mapLeave(ResultSet rs) throws SQLException {
        LeaveApplication leave = new LeaveApplication();

        leave.setLeaveId(rs.getLong("leave_id"));

        long employeeId = rs.getLong("employee_id");
        Employee employee = employeeDAO.findById(employeeId);

        if (employee == null) {
            throw new SQLException(
                    "Employee not found for ID: " + employeeId);
        }

        leave.setEmployee(employee);

        String leaveType = rs.getString("leave_type");
        if (leaveType != null) {
            leave.setLeaveType(LeaveType.valueOf(leaveType));
        }

        java.sql.Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            leave.setStartDate(startDate.toLocalDate());
        }

        java.sql.Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            leave.setEndDate(endDate.toLocalDate());
        }

        leave.setReason(rs.getString("reason"));

        String status = rs.getString("status");
        if (status != null) {
            leave.setStatus(LeaveStatus.valueOf(status));
        }

        leave.setTotalDays(rs.getInt("total_days"));
        leave.setManagerComment(rs.getString("manager_comment"));

        java.sql.Date appliedDate = rs.getDate("applied_date");
        if (appliedDate != null) {
            leave.setAppliedDate(appliedDate.toLocalDate());
        }

        return leave;
    }
}