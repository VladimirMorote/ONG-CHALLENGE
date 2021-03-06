package com.alkemy.ong.mapper.attribute;

public enum NewsAttributes {

  NEWS_ID("newsId"),
  NAME("name"),
  TEXT("text"),
  IMAGE("image"),
  CATEGORY_NAME("categoryName");

  private final String fieldName;

  NewsAttributes(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return this.fieldName;
  }
}
