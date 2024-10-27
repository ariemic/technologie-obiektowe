package pl.edu.agh.iisg.to.model;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Grade {

    public static final String TABLE_NAME = "grade";

    private final int id;

    private final float grade;

    Grade(final int id, final float grade) {
        this.id = id;
        this.grade = grade;
    }

    public static boolean gradeStudent(final Student student, final Course course, final float grade) {
        //if there is grade for a given course already then return false else assign grade and return true

        String checkIfGradeExistSql = "SELECT grade from grade WHERE student_id = ? AND course_id = ?";
        Object[] checkArgs = {student.id(), course.id()};

        try {
            ResultSet resultSet = QueryExecutor.read(checkIfGradeExistSql, checkArgs);
            if(!resultSet.next()){
                String gradeStudentSql = "INSERT INTO grade (grade, student_id, course_id) VALUES (?, ?, ?)";

                Object[] args = {grade, student.id(), course.id()};
                int gradeId = QueryExecutor.createAndObtainId(gradeStudentSql, args);
                return gradeId > -1 ;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int id() {
        return id;
    }

    public float grade() {
        return grade;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String GRADE = "grade";

        public static final String STUDENT_ID = "student_id";

        public static final String COURSE_ID = "course_id";

    }

}
