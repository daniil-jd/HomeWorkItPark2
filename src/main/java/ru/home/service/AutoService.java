package ru.home.service;

import ru.home.domain.Auto;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoService {
    private final DataSource ds;
    private final String SELECT_ALL = "SELECT id, name, description, image FROM autos";
    private final String SELECT_BY_NAME = "SELECT id, name, description, image FROM autos WHERE name = ?";
    private final String SELECT_BY_DESCRIPTION = "SELECT id, name, description, image FROM autos WHERE description = ?";
    private final String SELECT_BY_ID = "SELECT id, name, description, image FROM autos WHERE id = ?";
    private final String INSERT = "INSERT INTO autos (id, name, description, image) VALUES (?, ?, ?, ?);";
    private final String UPDATE = "UPDATE autos SET name=?, description=?, image=? WHERE id=?";
    private final String DELETE = "DELETE FROM autos WHERE id=?";

    public static final String NAME = "search-name";
    public static final String DESCRIPTION = "search-description";

    public AutoService() throws NamingException {
        var context = new InitialContext();
        ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
    }

    public String saveImageAndGetUid(Part file) throws IOException {
        var fileName = UUID.randomUUID().toString();
        file.write(fileName);
        return fileName;
    }

    /**
     * Получить все автомобили.
     * @return лист авто
     */
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

    /**
     * Создать авто.
     * @param name имя
     * @param description описание
     * @param file фото
     * @throws IOException в случае ошибки при записи в бд
     */
    public void create(String name, String description, Part file) throws IOException {
        var fileName = saveImageAndGetUid(file); // Check before jump vs Try then sorry

        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(INSERT)) {

                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, name);
                stmt.setString(3, description);
                stmt.setString(4, fileName);
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void update(String name, String description, String fileName, String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(UPDATE)) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, fileName);
                stmt.setString(4, id);
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

    /**
     * Поиск авто по айди.
     * @param id айди
     * @return авто
     */
    public Auto getById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(SELECT_BY_ID)) {

                stmt.setString(1, id);
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("404");
                    }

                    var name = rs.getString("name");
                    var description = rs.getString("description");
                    var image = rs.getString("image");
                    return new Auto(id, name, description, image);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Поиск авто по имени.
     * @param name имя
     * @return список авто
     */
    public List<Auto> findByName(String name) {
        return findByField(name, SELECT_BY_NAME);
    }

    /**
     * Поиск авто по описанию.
     * @param desc описание
     * @return список авто
     */
    public List<Auto> findByDescription(String desc) {
        return findByField(desc, SELECT_BY_DESCRIPTION);
    }

    /**
     * Поиск авто по заданному полю.
     * @param field поле
     * @param select запрос
     * @return список авто
     */
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

    /**
     * Собрать ответ из ResultSet.
     * @param resultSet resultSet
     * @return список авто
     * @throws SQLException в случае ошибки в бд
     */
    private List<Auto> buildListFormResultSet(ResultSet resultSet)
            throws SQLException {
        var list = new ArrayList<Auto>();
        while (resultSet.next()) {
            var id = resultSet.getString("id");
            var name = resultSet.getString("name");
            var description = resultSet.getString("description");
            var image = resultSet.getString("image");
            list.add(new Auto(id, name, description, image));
        }
        return list;
    }

}
