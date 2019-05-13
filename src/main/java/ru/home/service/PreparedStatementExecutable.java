package ru.home.service;

import ru.home.domain.Auto;

import javax.sql.DataSource;

public interface PreparedStatementExecutable {
    void executePreparedStatement(String query, DataSource ds, Auto auto);
}
