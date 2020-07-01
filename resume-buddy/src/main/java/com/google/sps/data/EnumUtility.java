package com.google.sps.data;

public class EnumUtility {

  enum Degree {
    HIGHSCHOOL,
    ASSOCIATE,
    BACHELOR,
    MASTER,
    DOCTORATE,
    NOPREF;
  }

  enum Career {
    COMPSCI,
    HEALTHCARE,
    EDUCATION,
    ENGINEERING,
    BUSINESS,
    OTHER;
  }

  enum NumYears {
    LESSTHANFIVE,
    FIVETOTEN,
    GREATERTHANTEN,
    NOPREF;
  }

  enum SchoolYear {
    HIGHSCHOOL,
    FIRSTYEAR,
    SECONDYEAR,
    THIRDYEAR,
    FOURTHYEAR,
    POSTGRAD;
  }

  private EnumUtility() {}
}
