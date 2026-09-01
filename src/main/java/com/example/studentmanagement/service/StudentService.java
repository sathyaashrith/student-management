package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequestDTO;
import com.example.studentmanagement.dto.StudentResponseDTO;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.StudentDuplicateException;
import com.example.studentmanagement.exception.StudentNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudentService {
    private static final Logger log =
            LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // CREATE
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        log.info("Creating new student with email: {}",
                request.getEmail());
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new StudentNotFoundException(
                    "Student with email " + request.getEmail() + " already exists"
            );
        }

        Student student = new Student();

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        student.setPhone(request.getPhone());
        student.setCity(request.getCity());

        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with id: {}",
                savedStudent.getId());

        return convertToResponseDTO(savedStudent);
    }

    // GET ALL
    public List<StudentResponseDTO> getAllStudents() {
        log.info("Fetching all students");
        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public StudentResponseDTO getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with id " + id + " not found"));

        return convertToResponseDTO(student);
    }

    // UPDATE
    public StudentResponseDTO updateStudent(
            Long id,
            StudentRequestDTO request) {
        log.info("Updating student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with Id " + id + " not found"));

        if(student.getEmail().equals(request.getEmail()) && studentRepository.existsByEmail(request.getEmail())) {
            throw new StudentDuplicateException(
                    "Student with email " + request.getEmail() + " already exists"
            );
        }

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        student.setPhone(request.getPhone());
        student.setCity(request.getCity());

        Student updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully with id: {}", id);
        return convertToResponseDTO(updatedStudent);
    }

    // DELETE
    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with Id " + id + " not found"));

        studentRepository.delete(student);
        log.info("Student deleted successfully with id: {}", id);
    }

    // Entity → Response DTO
    private StudentResponseDTO convertToResponseDTO(Student student) {

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getEmail(),
                student.getCourse(),
                student.getPhone(),
                student.getCity()
        );
    }

    public Page<StudentResponseDTO> getStudents(
            int page,
            int size,
            String sortBy,
            String direction) {
        log.info(
                "Fetching students with page={}, size={}, sortBy={}, direction={}",
                page,
                size,
                sortBy,
                direction
        );
        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }
}