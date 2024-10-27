package pl.edu.agh.iisg.to.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

public class Student {
    private final int id;

    private final String firstName;

    private final String lastName;

    private final int indexNumber;

    Student(final int id, final String firstName, final String lastName, final int indexNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
    }

    public static Student fromResultSet(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("index_number")
        );
    }

    public static Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        String sql = "INSERT INTO student (first_name, last_name, index_number) VALUES (?,?,?)";
        Object[] args = {firstName, lastName, indexNumber};
        try {
            int studentId = QueryExecutor.createAndObtainId(sql, args);
            return Student.findById(studentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Student> findByIndexNumber(final int indexNumber) {
        String sql = "SELECT * FROM student WHERE index_number = ?";
        Object[] args = {indexNumber};
        try {
            ResultSet resultSet = QueryExecutor.read(sql, args);
            if (resultSet.next()) {
                String firstName = resultSet.getString(Columns.FIRST_NAME);
                String lastName = resultSet.getString(Columns.LAST_NAME);
                int studentId = resultSet.getInt(Columns.ID);
                return Optional.of(new Student(studentId, firstName, lastName, indexNumber));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Optional<Student> findById(final int id) {
        String sql = "SELECT * FROM student WHERE id = (?)";
        return find(id, sql);
    }

    private static Optional<Student> find(int value, String sql) {
        Object[] args = {value};
        try (ResultSet rs = QueryExecutor.read(sql, args)) {
            if (rs.next()) {
                return Optional.of(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("index_number")
                ));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Map<Course, Float> createReport() {
        // TODO additional task
        return Collections.emptyMap();
    }

    public int id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public int indexNumber() {
        return indexNumber;
    }


    public static class Columns {

        public static final String ID = "id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String INDEX_NUMBER = "index_number";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Student student = (Student) o;

        if (id != student.id)
            return false;
        if (indexNumber != student.indexNumber)
            return false;
        if (!firstName.equals(student.firstName))
            return false;
        return lastName.equals(student.lastName);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + indexNumber;
        return result;
    }
}
