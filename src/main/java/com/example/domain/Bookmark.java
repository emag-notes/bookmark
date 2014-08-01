package com.example.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Bookmark {

  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @Size(min = 1, max = 255)
  private String name;

  @NotNull
  @Size(min = 1, max = 255)
  private String url;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Bookmark bookmark = (Bookmark) o;

    if (url != null ? !url.equals(bookmark.url) : bookmark.url != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Bookmark{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", url='" + url + '\'' +
      '}';
  }

}


