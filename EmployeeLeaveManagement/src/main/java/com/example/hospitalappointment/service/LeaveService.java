package com.example.hospitalappointment.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hospitalappointment.dao.EmployeeDAO;
import com.example.hospitalappointment.dao.LeaveDAO;
import com.example.hospitalappointment.exception.EmployeeNotFoundException;
import com.example.hospitalappointment.exception.InvalidLeaveException;
import com.example.hospitalappointment.exception.LeaveNotFoundException;
import com.example.hospitalappointment.model.Employee;
import com.example.hospitalappointment.model.LeaveApplication;
import com.example.hospitalappointment.model.LeaveStatus;
import com.example.hospitalappointment.model.LeaveType;

@Service
public class LeaveService {

    private final LeaveDAO leaveDAO;
    private final EmployeeDAO employeeDAO;

    public LeaveService(LeaveDAO leaveDAO, EmployeeDAO employeeDAO) {
        this.leaveDAO = leaveDAO;
        this.employeeDAO = employeeDAO;
    }

    public LeaveApplication applyLeave(Long employeeId, LeaveApplication leave) {
        if (leave == null) {
            throw new InvalidLeaveException("Leave details are required");
        }

        Employee employee = employeeDAO.findById(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found");
        }

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new InvalidLeaveException("Enter both leave dates");
        }

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new InvalidLeaveException("End date cannot be before start date");
        }

        if (leave.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidLeaveException("Start date cannot be in the past");
        }

        if (leave.getLeaveType() == null) {
            throw new InvalidLeaveException("Select a leave type");
        }

        if (leave.getReason() == null || leave.getReason().trim().length() < 5) {
            throw new InvalidLeaveException("Reason must contain at least 5 characters");
        }

        LocalDate startDate = leave.getStartDate();
        LocalDate endDate = leave.getEndDate();
        int days = 1;

        while (startDate.isBefore(endDate)) {
            days++;
            startDate = startDate.plusDays(1);
        }

        leave.setEmployee(employee);
        leave.setTotalDays(days);
        leave.setStatus(LeaveStatus.PENDING);
        leave.setAppliedDate(LocalDate.now());

        return leaveDAO.save(leave);
    }

    public List<LeaveApplication> getAllLeaves() {
        return leaveDAO.findAll();
    }

    public LeaveApplication getLeaveById(Long id) {
        LeaveApplication leave = leaveDAO.findById(id);

        if (leave == null) {
            throw new LeaveNotFoundException("Leave request not found with ID: " + id);
        }

        return leave;
    }

    public List<LeaveApplication> getLeavesByEmployee(Long employeeId) {
        Employee employee = employeeDAO.findById(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found");
        }

        return leaveDAO.findByEmployee(employee);
    }

    public List<LeaveApplication> getLeavesByStatus(LeaveStatus status) {
        if (status == null) {
            throw new InvalidLeaveException("Leave status is required");
        }

        return leaveDAO.findByStatus(status);
    }

    public LeaveApplication approveLeave(Long id, String comment) {
        LeaveApplication leave = getLeaveById(id);

        if (LeaveStatus.PENDING.equals(leave.getStatus()) == false) {
            throw new InvalidLeaveException("Only pending leave can be approved");
        }

        Employee employee = leave.getEmployee();
        LeaveType type = leave.getLeaveType();
        int days = leave.getTotalDays();

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found for this leave");
        }

        if (type == LeaveType.CASUAL_LEAVE) {
            if (employee.getCasualLeaveBalance() < days) {
                throw new InvalidLeaveException("Insufficient casual leave balance");
            }
            employee.setCasualLeaveBalance(employee.getCasualLeaveBalance() - days);

        } else if (type == LeaveType.SICK_LEAVE) {
            if (employee.getSickLeaveBalance() < days) {
                throw new InvalidLeaveException("Insufficient sick leave balance");
            }
            employee.setSickLeaveBalance(employee.getSickLeaveBalance() - days);

        } else if (type == LeaveType.EARNED_LEAVE) {
            if (employee.getEarnedLeaveBalance() < days) {
                throw new InvalidLeaveException("Insufficient earned leave balance");
            }
            employee.setEarnedLeaveBalance(employee.getEarnedLeaveBalance() - days);
        }

        employeeDAO.save(employee);

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setManagerComment(comment);

        return leaveDAO.save(leave);
    }

    public LeaveApplication rejectLeave(Long id, String comment) {
        LeaveApplication leave = getLeaveById(id);

        if (LeaveStatus.PENDING.equals(leave.getStatus()) == false) {
            throw new InvalidLeaveException("Only pending leave can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerComment(comment);

        return leaveDAO.save(leave);
    }

    public LeaveApplication cancelLeave(Long id) {
        LeaveApplication leave = getLeaveById(id);

        if (LeaveStatus.PENDING.equals(leave.getStatus()) == false) {
            throw new InvalidLeaveException("Only pending leave can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);

        return leaveDAO.save(leave);
    }
}