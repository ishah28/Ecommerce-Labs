package com.ihanchate.studentapp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.sql.*;
import java.util.*;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private Connection connect() throws SQLException {
        // Adjust path to match your new location
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/src/main/resources/students.db";
        return DriverManager.getConnection(url);
    }

    // ======== GET ========
    @GET
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("major")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // ======== PUT (JSON version) ========
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateStudentJson(@PathParam("id") int id, Student student) {
        String sql = "UPDATE students SET name=?, age=?, major=? WHERE id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getAge());
            pstmt.setString(3, student.getMajor());
            pstmt.setInt(4, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                return Response.ok("Student updated via JSON").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
    }

    // ======== PUT (Query Param version) ========
    @PUT
    public Response updateStudentQuery(
            @QueryParam("id") int id,
            @QueryParam("name") String name,
            @QueryParam("age") int age,
            @QueryParam("major") String major) {

        if (id == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing 'id' parameter").build();
        }

        String sql = "UPDATE students SET name=?, age=?, major=? WHERE id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, major);
            pstmt.setInt(4, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                return Response.ok("Student updated via Query Params").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
    }

    // ======== DELETE (Path Param version) ========
    @DELETE
    @Path("/{id}")
    public Response deleteStudentById(@PathParam("id") int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                return Response.ok("Student deleted via /{id}").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
    }

    // ======== DELETE (Query Param version) ========
    @DELETE
    public Response deleteStudentQuery(@QueryParam("id") int id) {
        if (id == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing 'id' parameter").build();
        }

        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                return Response.ok("Student deleted via Query Params").build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
    }
}
