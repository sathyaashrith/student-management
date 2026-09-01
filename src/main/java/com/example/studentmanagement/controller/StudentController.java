package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequestDTO;
import com.example.studentmanagement.dto.StudentResponseDTO;
import com.example.studentmanagement.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.studentmanagement.dto.ApiResponse;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO student =
                studentService.createStudent(request);

        ApiResponse<StudentResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Student created successfully",
                        student
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents() {

        List<StudentResponseDTO> students =
                studentService.getAllStudents();

        ApiResponse<List<StudentResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Students retrieved successfully",
                        students
                );

        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(
            @PathVariable Long id) {

        StudentResponseDTO student =
                studentService.getStudentById(id);

        ApiResponse<StudentResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Student retrieved successfully",
                        student
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<StudentResponseDTO>>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<StudentResponseDTO> students =
                studentService.getStudents(
                        page,
                        size,
                        sortBy,
                        direction
                );

        ApiResponse<Page<StudentResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Students retrieved successfully",
                        students
                );

        return ResponseEntity.ok(response);
    }
    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO student =
                studentService.updateStudent(id, request);

        ApiResponse<StudentResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Student updated successfully",
                        student
                );

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Student deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
}
