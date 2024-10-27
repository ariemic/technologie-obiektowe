package pl.edu.agh.iisg.to.service;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.repository.StudentRepository;
import pl.edu.agh.iisg.to.session.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchoolService {

    private final TransactionService transactionService;

    private final StudentRepository studentRepository;


    private final GradeDao gradeDao;

    public SchoolService(TransactionService transactionService, StudentRepository studentRepository, GradeDao gradeDao) {
        this.transactionService = transactionService;
        this.studentRepository = studentRepository;
        this.gradeDao = gradeDao;
    }

    public boolean enrollStudent(final Course course, final Student student) {
        return transactionService.doAsTransaction(() -> {
            if (course.studentSet().contains(student)) {
                return false;
            }
            course.studentSet().add(student);
            student.courseSet().add(course);
            return true;
        }).orElse(false);

    }

    public boolean removeStudent(int indexNumber) {
        //it's better to search for a student directly in a transaction because otherwise the state of db can change between looking for a student and removing him
        return transactionService.doAsTransaction(() -> {
            Optional<Student> studentOpt = studentRepository.getByIndexNumber(indexNumber);
            return studentOpt.map(student -> {
                studentRepository.remove(student);
                return true;
            }).orElse(false);
        }).orElse(false); // Return false if the transaction fails

    }

    public boolean gradeStudent(final Student student, final Course course, final float gradeValue) {
        return transactionService.doAsTransaction(() -> {
            //create grade and add to persistent context
            Optional<Grade> savedGrade = gradeDao.save(new Grade(student, course, gradeValue));

            return savedGrade.map(grade -> {
                student.gradeSet().add(grade);
                course.gradeSet().add(grade);
                return true;
            }).orElse(false);


        }).orElse(false);

    }


    public Map<String, List<Float>> getStudentGrades(String courseName) {

        Map<String, List<Float>> studentsGradesMap = new HashMap<>();
        studentRepository.findAllByCourseName(courseName)
                .forEach(student -> {
                    String studentName = student.firstName() + " " + student.lastName();
                    List<Float> grades = student.gradeSet()
                            .stream()
                            .map(grade -> grade.grade())
                            .sorted()
                            .toList();
                    studentsGradesMap.put(studentName, grades);
                });
        return studentsGradesMap;
    }


}
