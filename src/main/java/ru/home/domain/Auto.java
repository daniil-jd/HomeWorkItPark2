package ru.home.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auto {
  private String id;
  private String name;
  private String description;
  private String year;
  private double power;
  private String color;
  private String image;

  public Auto(String name, String description, String year, double power, String color, String image) {
    this.name = name;
    this.description = description;
    this.year = year;
    this.power = power;
    this.color = color;
    this.image = image;
  }
}
