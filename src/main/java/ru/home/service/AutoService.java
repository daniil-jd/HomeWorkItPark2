package ru.home.service;

import ru.home.domain.Auto;
import ru.home.exception.RuntimeSQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AutoService {
    private final DataSource ds;
    private PreparedStatementExecutable psExecutable;
    private AutoBuildable autoBuildable;
    private final String SELECT_ALL = "SELECT id, name, description, year, power, color, image FROM autos";
    private final String SELECT_BY_FILED = "SELECT id, name, description, year, power, color, image FROM autos WHERE lower(%s) LIKE ?";
    private final String SELECT_BY_ID = "SELECT id, name, description, year, power, color, image FROM autos WHERE id = ?";
    private final String INSERT = "INSERT INTO autos (id, name, description, year, power, color, image) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private final String UPDATE = "UPDATE autos SET name=?, description=?, year=?, power=?, color=?, image=? WHERE id=?";
    private final String DELETE = "DELETE FROM autos WHERE id=?";
    private final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS autos (id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, year INTEGER , power DOUBLE, color TEXT, image TEXT);";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String YEAR = "year";
    private static final String POWER = "power";
    private static final String COLOR = "color";
    private static final String IMAGE = "image";

    public AutoService() throws NamingException {
        var context = new InitialContext();
        ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        try (var connection = ds.getConnection()) {
            try (var stmt = connection.createStatement()) {
                stmt.execute(CREATE_TABLE_IF_NOT_EXIST);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        psExecutable = ((query, ds, auto) -> {
            try (var conn = ds.getConnection()) {
                if (query.contains("INSERT")) {
                    try (var stmt = conn.prepareStatement(query)) {

                        stmt.setString(1, UUID.randomUUID().toString());
                        stmt.setString(2, auto.getName());
                        stmt.setString(3, auto.getDescription());
                        stmt.setString(4, auto.getYear());
                        stmt.setDouble(5, auto.getPower());
                        stmt.setString(6, auto.getColor());
                        stmt.setString(7, auto.getImage());
                        stmt.execute();
                    }
                } else {
                    try (var stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, auto.getName());
                        stmt.setString(2, auto.getDescription());
                        stmt.setString(3, auto.getYear());
                        stmt.setDouble(4, auto.getPower());
                        stmt.setString(5, auto.getColor());
                        stmt.setString(6, auto.getImage());
                        stmt.setString(7, auto.getId());
                        stmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeSQLException(e);
            }
        });
        autoBuildable = (rs -> {
            try {
                var autoId = rs.getString(ID);
                var name = rs.getString(NAME);
                var description = rs.getString(DESCRIPTION);
                var image = rs.getString(IMAGE);
                var year = rs.getString(YEAR);
                var power = rs.getDouble(POWER);
                var color = rs.getString(COLOR);

                return new Auto(autoId, name, description, year, power, color, image);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeSQLException(e);
            }
        });
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
            throw new RuntimeSQLException(e); // rethrowing
        }
    }

    public void create(Auto auto) {
        psExecutable.executePreparedStatement(INSERT, ds, auto);
    }

    public void update(Auto auto) {
        psExecutable.executePreparedStatement(UPDATE, ds, auto);
    }

    public void delete(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(DELETE)) {
                stmt.setString(1, id);
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeSQLException(e);
        }
    }

    public Optional<Auto> getById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(SELECT_BY_ID)) {

                stmt.setString(1, id);
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    return Optional.of(autoBuildable.buildAuto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Auto> findByField(String searchField, String searchValue) {
        searchField = String.format(SELECT_BY_FILED, searchField);
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(searchField)) {
                stmt.setString(1, searchValue.toLowerCase());
                try (var rs = stmt.executeQuery()) {
                    return buildListFormResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeSQLException(e); // rethrowing
        }
    }

    private List<Auto> buildListFormResultSet(ResultSet rs)
            throws SQLException {
        var list = new ArrayList<Auto>();
        while (rs.next()) {
            list.add(autoBuildable.buildAuto(rs));
        }
        return list;
    }

}
