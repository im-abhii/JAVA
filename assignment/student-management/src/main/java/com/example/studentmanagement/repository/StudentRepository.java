package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // finds all students of a course (ignores upper/lower case)
    List<Student> findByCourseIgnoreCase(String course);

    boolean existsByEmail(String email);
}
