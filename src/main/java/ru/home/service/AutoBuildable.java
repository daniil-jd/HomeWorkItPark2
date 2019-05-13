package ru.home.service;

import ru.home.domain.Auto;

import java.sql.ResultSet;

public interface AutoBuildable {

    Auto buildAuto(ResultSet rs);

}
