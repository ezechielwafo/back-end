package tech.getarrays.employeemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.getarrays.employeemanager.exception.UserNotFoundException;
import tech.getarrays.employeemanager.model.Employee;
import tech.getarrays.employeemanager.service.repo.EmployeeRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
//import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepo employeeRepo;
    @Autowired
//    ici on injecte un objet de type eEmployerRepo dans le constructeur de EmployerService
    public EmployeeService(EmployeeRepo employeeRepo){
        this.employeeRepo = employeeRepo;
    }
    public Employee addEmployee(Employee employee){
    employee.setEmployeeCode(UUID.randomUUID().toString());
    return employeeRepo.save(employee);
    }
    public List<Employee> findAllEmployees(){
        return employeeRepo.findAll();
    }
    public Employee updateEmployee(Employee employee){
        Employee emp = findEmployeeById(employee.getId());
        if (emp != null) {
            emp.setEmployeeCode(employee.getEmployeeCode());
            emp.setEmail(employee.getEmail());
            emp.setName(employee.getName());
            emp.setPhone(employee.getPhone());
            emp.setImageUrl(employee.getImageUrl());
        }
        return employeeRepo.save(emp);
    }
    public Employee findEmployeeById(Long id){
        return employeeRepo.findEmployeeById(id).
                orElseThrow( () -> new UserNotFoundException("User by id " +id+ "was not found"));
    }
    public void deleteEmployee(Long id){
        System.out.println("\n\n\n id "+ id);
        employeeRepo.deleteById(id);
    }

}
