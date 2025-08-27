package com.creditcard.service;

import com.creditcard.model.Employee;
import com.creditcard.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        log.debug("Fetching all employees from repository");
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        log.debug("Fetching employee by id={}", id);
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        log.debug("Saving employee email={}", employee.getEmail());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        log.debug("Deleting employee id={}", id);
        employeeRepository.deleteById(id);
    }

    public List<Employee> getEmployeesByFirstName(String firstName) {
        log.debug("Fetching employees by firstName={}", firstName);
        return employeeRepository.findByFirstName(firstName);
    }
}
