package pl.edu.agh.iisg.to.repository;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.service.SchoolService;
import pl.edu.agh.iisg.to.service.SchoolServiceDAO;

import java.util.List;
import java.util.Optional;

public class StudentRepository implements Repository<Student>{
    private final StudentDao studentDao;
    private final CourseDao courseDao;

    public StudentRepository(StudentDao studentDao, CourseDao courseDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;

    }

    @Override
    public Optional<Student> add(Student student) {
        return studentDao.save(student);
    }

    @Override
    public Optional<Student> getById(int id) {
        return studentDao.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public void remove(Student student) {
//        schoolService.removeStudent(student.indexNumber());
        studentDao.remove(student);

        student.courseSet().forEach((course) -> {
            course.studentSet().remove(student);
        });

        student.courseSet().clear();
    }


    public Optional<Student> getByIndexNumber(int indexNumber) {
        return studentDao.findByIndexNumber(indexNumber);
    }


    public List<Student> findAllByCourseName(String courseName) {
        return courseDao.findByName(courseName).get().studentSet()
                .stream()
                .toList();
    }
}
