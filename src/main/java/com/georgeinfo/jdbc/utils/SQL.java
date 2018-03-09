package com.georgeinfo.jdbc.utils;

import com.georgeinfo.jdbc.utils.sql.Delete;
import com.georgeinfo.jdbc.utils.sql.Insert;
import com.georgeinfo.jdbc.utils.sql.Select;
import com.georgeinfo.jdbc.utils.sql.Update;

public class SQL extends AbstractSQL<SQL> {

    @Override
    public SQL getSelf() {
        return this;
    }

    /**
     * select语法提示，没有实际用途
     *
     * @return SQL语法提示类，没有实际用途
     */
    public Select selectPrompts() {
        return new Select();
    }

    /**
     * Insert语法提示，没有实际用途
     *
     * @return SQL语法提示类，没有实际用途
     */
    public Insert insertPrompts() {
        return new Insert();
    }

    /**
     * Update语法提示，没有实际用途
     *
     * @return SQL语法提示类，没有实际用途
     */
    public Update updatePrompts() {
        return new Update();
    }

    /**
     * Delete语法提示，没有实际用途
     *
     * @return SQL语法提示类，没有实际用途
     */
    public Delete deletePrompts() {
        return new Delete();
    }

    private String testSelectPersonSql() {
        return new SQL() {
            {
                SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
                SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
                FROM("PERSON P");
                FROM("ACCOUNT A");
                INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
                INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
                WHERE("P.ID = A.ID");
                WHERE("P.FIRST_NAME like ?");
                OR();
                WHERE("P.LAST_NAME like ?");
                GROUP_BY("P.ID");
                HAVING("P.LAST_NAME like ?");
                OR();
                HAVING("P.FIRST_NAME like ?");
                ORDER_BY("P.ID");
                ORDER_BY("P.FULL_NAME");
            }
        }.toString();
    }

    // Anonymous inner class
    private String testDeletePersonSql() {
        return new SQL() {
            {
                DELETE_FROM("PERSON");
                WHERE("ID = ${id}");
            }
        }.toString();
    }

// Builder / Fluent style
    private String testInsertPersonSql() {
        String sql = new SQL()
                .INSERT_INTO("PERSON")
                .VALUES("ID, FIRST_NAME", "${id}, ${firstName}")
                .VALUES("LAST_NAME", "${lastName}")
                .toString();
        return sql;
    }

// With conditionals (note the final parameters, required for the anonymous inner class to access them)
    private String testSelectPersonLike(final String id, final String firstName, final String lastName) {
        return new SQL() {
            {
                SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME");
                FROM("PERSON P");
                if (id != null) {
                    WHERE("P.ID like ${id}");
                }
                if (firstName != null) {
                    WHERE("P.FIRST_NAME like ${firstName}");
                }
                if (lastName != null) {
                    WHERE("P.LAST_NAME like ${lastName}");
                }
                ORDER_BY("P.LAST_NAME");
            }
        }.toString();
    }

    private String testDeletePersonSql2() {
        return new SQL() {
            {
                DELETE_FROM("PERSON");
                WHERE("ID = ${id}");
            }
        }.toString();
    }

    private String testInsertPersonSql2(final int id, final String firstName) {
        return new SQL() {
            {
                INSERT_INTO("PERSON");
                if (id > 0 && firstName != null) {
                    VALUES("ID, FIRST_NAME", "${id}, ${firstName}");
                }
                VALUES("LAST_NAME", "${lastName}");
            }
        }.toString();
    }

    private String testUpdatePersonSql() {
        final String firstName = "George";
        final String lastName = "G";
        return new SQL() {
            {
                UPDATE("PERSON");
                if (firstName != null && !firstName.trim().isEmpty()) {
                    SET("FIRST_NAME = ${firstName}");
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                    SET("LAST_NAME = ${lastName}");
                }
                WHERE("ID = ${id}");
            }
        }.toString();
    }

//    public static void main(String[] args) {
//        SQL test = new SQL();
//        System.out.println(test.testInsertPersonSql2(-1,"George"));
//    }
}
