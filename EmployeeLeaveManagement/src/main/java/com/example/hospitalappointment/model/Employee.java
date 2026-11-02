package com.example.hospitalappointment.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Employee {

    private Long employeeId;

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role = "EMPLOYEE";

    @Min(value = 0, message = "Casual leave balance cannot be negative")
    private int casualLeaveBalance = 12;

    @Min(value = 0, message = "Sick leave balance cannot be negative")
    private int sickLeaveBalance = 10;

    @Min(value = 0, message = "Earned leave balance cannot be negative")
    private int earnedLeaveBalance = 15;

    public Employee() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCasualLeaveBalance() {
        return casualLeaveBalance;
    }

    public void setCasualLeaveBalance(int casualLeaveBalance) {
        this.casualLeaveBalance = casualLeaveBalance;
    }

    public int getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public void setSickLeaveBalance(int sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }

    public int getEarnedLeaveBalance() {
        return earnedLeaveBalance;
    }

    public void setEarnedLeaveBalance(int earnedLeaveBalance) {
        this.earnedLeaveBalance = earnedLeaveBalance;
    }
}