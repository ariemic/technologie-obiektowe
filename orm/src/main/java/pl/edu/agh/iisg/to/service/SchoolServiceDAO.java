package pl.edu.agh.iisg.to.service;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.session.TransactionService;

import java.util.*;

public class SchoolServiceDAO {

    private final TransactionService transactionService;

    private final StudentDao studentDao;

    private final CourseDao courseDao;

    private final GradeDao gradeDao;

    public SchoolServiceDAO(TransactionService transactionService, StudentDao studentDao, CourseDao courseDao, GradeDao gradeDao) {
        this.transactionService = transactionService;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
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
        return transactionService.doAsTransaction(() ->
                        studentDao.findByIndexNumber(indexNumber)
                                .map(student -> {
                                    studentDao.remove(student);

                                    student.courseSet().forEach((course) -> {
                                        course.studentSet().remove(student);
                                    });

                                    student.courseSet().clear();

                                    return true;
                                }).orElse(false))
                .orElse(false);
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

//    public Map<String, List<Float>> getStudentGrades(String courseName) {
//        Map<String, List<Float>> studentsGradesMap = new HashMap<>();
//
//        return transactionService.doAsTransaction(() -> {
//
//            studentDao.findAll().forEach((student -> {
//                String studentName = student.firstName() + " " + student.lastName();
//
//                List<Float> grades = student.courseSet()
//                        .stream()
//                        .filter(course -> course.name().equals(courseName))
//                        .flatMap(course -> course.gradeSet()
//                                .stream()
//                                .filter(grade -> grade.student().equals(student))
//                                .map(grade -> grade.grade()))
//                        .sorted()
//                        .toList();
//                studentsGradesMap.put(studentName, grades);
//            }));
//
//            return studentsGradesMap;
//        }).orElseGet(HashMap::new);
//    }

    public Map<String, List<Float>> getStudentGrades(String courseName) {

        Map<String, List<Float>> studentsGradesMap = new HashMap<>();
        courseDao.findByName(courseName).get().studentSet()
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
