package org.example;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
public class Fine {
  private String type;
  @SerializedName("first_name")
  private String name;
  @SerializedName("last_name")
  private String surname;
  @SerializedName("date_time")
  private String date;
  @SerializedName("fine_amount")
  private Double fineAmount;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Double getFineAmount() {
    return fineAmount;
  }

  public void setFineAmount(Double fineAmount) {
    this.fineAmount = fineAmount;
  }
}
