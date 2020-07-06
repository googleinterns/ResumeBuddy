package com.google.sps.data;

public class EnumUtility {

  enum Degree {
    HIGH_SCHOOL,
    ASSOCIATE,
    BACHELOR,
    MASTER,
    DOCTORATE,
    NO_PREFERENCE;
  }

  enum Career {
    COMPUTER_SCIENCE,
    HEALTHCARE,
    EDUCATION,
    ENGINEERING,
    BUSINESS,
    OTHER;
  }

  enum NumYears {
    LESS_THAN_5,
    GREATER_THAN_5,
    GREATER_THAN_10,
    NO_PREFERENCE;
  }

  enum SchoolYear {
    HIGH_SCHOOL,
    YEAR_1,
    YEAR_2,
    YEAR_3,
    YEAR_4,
    POST_GRAD,
    OTHER;
  }

  private EnumUtility() {}
}
