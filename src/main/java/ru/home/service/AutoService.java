package ru.home.service;

import ru.home.domain.Auto;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoService {
    private final DataSource ds;
    private final String SELECT_ALL = "SELECT id, name, description, year, power, color, image FROM autos";
    private final String SELECT_BY_NAME = "SELECT id, name, description, year, power, color, image FROM autos WHERE name = ?";
    private final String SELECT_BY_DESCRIPTION = "SELECT id, name, description, year, power, color, image FROM autos WHERE description = ?";
    private final String SELECT_BY_ID = "SELECT id, name, description, year, power, color, image FROM autos WHERE id = ?";
    private final String INSERT = "INSERT INTO autos (id, name, description, year, power, color, image) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private final String UPDATE = "UPDATE autos SET name=?, description=?, year=?, power=?, color=?, image=? WHERE id=?";
    private final String DELETE = "DELETE FROM autos WHERE id=?";
    private final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS autos (id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, year INTEGER , power DOUBLE, color TEXT, image TEXT);";

    public static final String NAME = "search-name";
    public static final String DESCRIPTION = "search-description";

    public AutoService() throws NamingException {
        var context = new InitialContext();
        ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        try (var connection = ds.getConnection()) {
            try (var stmt = connection.createStatement()) {
                stmt.execute(CREATE_TABLE_IF_NOT_EXIST);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Auto> getAll() {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.createStatement()) {
                try (var rs = stmt.executeQuery(SELECT_ALL)) {
                    return buildListFormResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // rethrowing
        }
    }

    public void create(Auto auto) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(INSERT)) {

                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, auto.getName());
                stmt.setString(3, auto.getDescription());
                stmt.setString(4, auto.getYear());
                stmt.setDouble(5, auto.getPower());
                stmt.setString(6, auto.getColor());
                stmt.setString(7, auto.getImage());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void update(Auto auto) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(UPDATE)) {
                stmt.setString(1, auto.getName());
                stmt.setString(2, auto.getDescription());
                stmt.setString(3, auto.getYear());
                stmt.setDouble(4, auto.getPower());
                stmt.setString(5, auto.getColor());
                stmt.setString(6, auto.getImage());
                stmt.setString(7, auto.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void delete(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(DELETE)) {
                stmt.setString(1, id);
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Auto getById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(SELECT_BY_ID)) {

                stmt.setString(1, id);
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    var name = rs.getString("name");
                    var description = rs.getString("description");
                    var image = rs.getString("image");
                    var year = rs.getString("year");
                    var power = rs.getDouble("power");
                    var color = rs.getString("color");
                    return new Auto(id, name, description, year, power, color, image);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Auto> findByName(String name) {
        return findByField(name, SELECT_BY_NAME);
    }

    public List<Auto> findByDescription(String desc) {
        return findByField(desc, SELECT_BY_DESCRIPTION);
    }

    private List<Auto> findByField(String field, String select) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(select)) {
                stmt.setString(1, field);
                try (var rs = stmt.executeQuery()) {
                    return buildListFormResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // rethrowing
        }
    }

    private List<Auto> buildListFormResultSet(ResultSet resultSet)
            throws SQLException {
        var list = new ArrayList<Auto>();
        while (resultSet.next()) {
            var id = resultSet.getString("id");
            var name = resultSet.getString("name");
            var description = resultSet.getString("description");
            var image = resultSet.getString("image");
            var year = resultSet.getString("year");
            var power = resultSet.getDouble("power");
            var color = resultSet.getString("color");
            list.add(new Auto(id, name, description, year, power, color, image));
        }
        return list;
    }

}
