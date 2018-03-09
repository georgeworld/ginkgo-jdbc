/*
 * Programming by: George <GeorgeNiceWorld@gmail.com>
 * Copyright (C) George (www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author George
 */
public final class DatabaseTypeDef {

    /**
     * 数据库类型
     * @author George
     */
    public static final class DbType {

        private static final LinkedHashMap<String, DbType> dbTypes = new LinkedHashMap<String, DbType>();
        private String dbCode;
        private String dbName;
        private String dbDriver;
        public static final DbType ORACLE = new DbType("ORACLE", "Oracle", "oracle.jdbc.OracleDriver");
        public static final DbType DB2 = new DbType("DB2", "IBM DB2", "COM.ibm.db2.jdbc.app.DB2Driver");
        public static final DbType MYSQL = new DbType("MYSQL", "MySQL", "com.mysql.jdbc.Driver");
        public static final DbType MS_SQL_SERVER = new DbType("MS_SQL_SERVER", "MS Sql Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        public static final DbType MS_SQL_SERVER2000 = new DbType("MS_SQL_SERVER2000", "MS Sql Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        public static final DbType SYBASE = new DbType("SYBASE", "Sybase", "com.sybase.jdbc2.jdbc.SybDriver");
        public static final DbType DERBY = new DbType("DERBY", "Derby", "org.apache.derby.jdbc.EmbeddedDriver");
        public static final DbType JAVADB = new DbType("JAVADB", "JavaDB", "org.apache.derby.jdbc.EmbeddedDriver");
        public static final DbType H2 = new DbType("H2", "H2", "org.h2.Driver");
        public static final DbType POSTGRE_SQL = new DbType("POSTGRE_SQL", "PostgreSQL", "org.postgresql.Driver");
        public static final DbType SAP = new DbType("SAP", "SAP", "com.sap.dbtech.jdbc.DriverSapDB");
        public static final DbType JTDS = new DbType("JTDS", "JTDS", "net.sourceforge.jtds.jdbc.Driver");
        public static final DbType SQLITE = new DbType("SQLITE", "sqlite", "org.sqlite.JDBC");
        public static final DbType HSQLDB = new DbType("HSQLDB", "hsqldb", "org.hsqldb.jdbcDriver");

        public DbType(String dbCode, String dbName, String dbDriver) {
            this.dbCode = dbCode;
            this.dbName = dbName;
            this.dbDriver = dbDriver;
            dbTypes.put(dbCode, this);
        }

        public static HashMap<String, String> getMap() {
            HashMap<String, String> returnValue = new HashMap<String, String>();
            for (Map.Entry<String, DbType> entry : dbTypes.entrySet()) {
                returnValue.put(entry.getKey(), entry.getValue().getDbName());
            }

            return returnValue;
        }

        public static DbType findNameByCode(String dbCode) {
            return dbTypes.get(dbCode);
        }

        public static String findDbCodeByJdbcDriver(String jdbcDriver) {
            for (DbType bean : dbTypes.values()) {
                if (bean.getDbDriver().equals(jdbcDriver)) {
                    return bean.getDbCode();
                }
            }

            return null;
        }

        public String getDbCode() {
            return dbCode;
        }

        public String getDbDriver() {
            return dbDriver;
        }

        public String getDbName() {
            return dbName;
        }
    }

    /**
     * 数据库数据分类(与具体数据库类型无关)
     * @author George
     */
    public static final class DataTypeCategoryDef {

        public static final String INTEGER = "INTEGER";
        public static final String FLOAT = "FLOAT";
        public static final String STRING = "STRING";
        public static final String DATE_TIME = "DATE_TIME";
        public static final String LARGE_DATA = "LARGE_DATA";
        public static final String OTHER = "OTHER";
        public static final String INTEGER_NAME = "整数";
        public static final String FLOAT_NAME = "浮点数";
        public static final String STRING_NAME = "字符串";
        public static final String DATE_TIME_NAME = "时间日期";
        public static final String LARGE_DATA_NAME = "大数据";
        public static final String OTHER_NAME = "其他";

        public static HashMap<String, String> getMap() {
            HashMap<String, String> returnValue = new HashMap<String, String>();
            returnValue.put(INTEGER, INTEGER_NAME);
            returnValue.put(FLOAT, FLOAT_NAME);
            returnValue.put(STRING, STRING_NAME);
            returnValue.put(DATE_TIME, DATE_TIME_NAME);
            returnValue.put(LARGE_DATA, LARGE_DATA_NAME);
            returnValue.put(OTHER, OTHER_NAME);

            return returnValue;
        }

        public static String getName(String code) {
            HashMap<String, String> map = getMap();
            return map.get(code);
        }
    }

    /**
     * 数据库数据类型
     * 全部数据类型，Oracle的定义，可参考：oracle.jdbc.OracleTypes
     * @author George
     */
    public static final class FieldDataTypeDef {

        private static final LinkedHashMap<String, FieldDataTypeDef> dbFieldTypes = new LinkedHashMap<String, FieldDataTypeDef>();
        private String dbCode;
        private String fieldType;
        private String fieldTypeName;
        /** Oracle数据类型名称定义 */
        public static final FieldDataTypeDef ORACLE_CHAR = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "CHAR", "Oracle.(CHAR) ---------------------------- 固定长度字符串，最大长度2000 bytes");// 固定长度字符串 最大长度2000 bytes
        public static final FieldDataTypeDef ORACLE_VARCHAR2 = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "VARCHAR2", "Oracle.(VARCHAR2) ------------------------ 可变长度字符串，最大长度4000 bytes，可做索引的最大长度749");// 可变长度的字符串 最大长度4000 bytes 可做索引的最大长度749
        public static final FieldDataTypeDef ORACLE_NCHAR = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "NCHAR", "Oracle.(NCHAR) --------------------------- 根据字符集而定的固定长度字符串 最大长度2000 bytes");// 根据字符集而定的固定长度字符串 最大长度2000 bytes
        public static final FieldDataTypeDef ORACLE_NVARCHAR2 = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "NVARCHAR2", "Oracle.(NVARCHAR2) ----------------------- 根据字符集而定的可变长度字符串 最大长度4000 bytes");// 根据字符集而定的可变长度字符串 最大长度4000 bytes
        public static final FieldDataTypeDef ORACLE_DATE = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "DATE", "Oracle.(DATE) ---------------------------- 日期时间，中文格式为：yyyy-MM-dd HH24:mi:ss");// 日期（日-月-年） DD-MM-YY（HH-MI-SS） 经过严格测试，无千虫问题
        public static final FieldDataTypeDef ORACLE_LONG = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "LONG", "Oracle.(LONG) ---------------------------- 超长字符串 最大长度2G");// 超长字符串 最大长度2G（231-1） 足够存储大部头著作
        public static final FieldDataTypeDef ORACLE_RAW = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "RAW", "Oracle.(RAW) ----------------------------- 固定长度的二进制数据 最大长度2000 bytes 可存放多媒体图象声音等");// 固定长度的二进制数据 最大长度2000 bytes 可存放多媒体图象声音等
        public static final FieldDataTypeDef ORACLE_LONG_RAW = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "LONG_RAW", "Oracle.(LONG RAW) ------------------------ 可变长度的二进制数据 最大长度2G 可存放多媒体图象声音等");// 可变长度的二进制数据 最大长度2G 同上
        public static final FieldDataTypeDef ORACLE_BLOB = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "BLOB", "Oracle.(BLOB) ---------------------------- 二进制数据 最大长度4G");// 二进制数据 最大长度4G
        public static final FieldDataTypeDef ORACLE_CLOB = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "CLOB", "Oracle.(CLOB) ---------------------------- 字符数据 最大长度4G");// 字符数据 最大长度4G
        public static final FieldDataTypeDef ORACLE_NCLOB = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "NCLOB", "Oracle.(NCLOB) --------------------------- 根据字符集而定的字符数据 最大长度4G");// 根据字符集而定的字符数据 最大长度4G
        public static final FieldDataTypeDef ORACLE_BFILE = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "BFILE", "Oracle.(BFILE) --------------------------- 存放在数据库外的二进制数据 最大长度4G");// 存放在数据库外的二进制数据 最大长度4G
        public static final FieldDataTypeDef ORACLE_ROWID = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "ROWID", "Oracle.(ROWID) --------------------------- 数据表中记录的唯一行号 10 bytes ***.*****.***格式，*为0或1");// 数据表中记录的唯一行号 10 bytes ********.****.****格式，*为0或1
        public static final FieldDataTypeDef ORACLE_NROWID = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "NROWID", "Oracle.(NROWID) -------------------------- 二进制数据表中记录的唯一行号 最大长度4000 bytes");// 二进制数据表中记录的唯一行号 最大长度4000 bytes
        public static final FieldDataTypeDef ORACLE_NUMBER = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "NUMBER", "Oracle.(NUMBER(P,S)) --------------------- 数字类型 P为整数位，S为小数位,P、S都可省略");// 数字类型 P为整数位，S为小数位
        public static final FieldDataTypeDef ORACLE_DECIMAL = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "DECIMAL", "Oracle.(DECIMAL(P,S)) -------------------- 数字类型 P为整数位，S为小数位,P、S都可省略");// 数字类型 P为整数位，S为小数位
        public static final FieldDataTypeDef ORACLE_INTEGER = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "INTEGER", "Oracle.(INTEGER) ------------------------- 整数类型 小的整数");// 整数类型 小的整数
        public static final FieldDataTypeDef ORACLE_FLOAT = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "FLOAT", "Oracle.(FLOAT) --------------------------- 浮点数类型 =NUMBER(38)，双精度");// 浮点数类型 NUMBER(38)，双精度
        public static final FieldDataTypeDef ORACLE_REAL = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "REAL", "Oracle.(REAL) ---------------------------- 实数类型 =NUMBER(63)，精度更高");// 实数类型 NUMBER(63)，精度更高
        public static final FieldDataTypeDef ORACLE_BINARY_FLOAT = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "BINARY_FLOAT", "Oracle.(BINARY_FLOAT) -------------------- IEEE固有单精度浮点数。6位精度。范围~±1038.25,>= Oracle 10g"); //是一种IEEE固有的单精度浮点数。可存储6位精度，取值范围在~±1038.25的数值。  >= Oracle 10g
        public static final FieldDataTypeDef ORACLE_BINARY_DOUBLE = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "BINARY_DOUBLE", "Oracle.(BINARY_DOUBLE) ------------------- IEEE固有双精度浮点数。12位精度。范围~±10308.25，>= Oracle 10g"); //是一种IEEE固有的双精度浮点数。可存储12位精度。取值范围在~±10308.25的数值 >= Oracle 10g
        public static final FieldDataTypeDef ORACLE_INTERVAL_DAY_TO_SECOND = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "INTERVAL_DAY_TO_SECOND", "Oracle.(INTERVAL DAY TO SECOND) ---------- 存储单位为天和秒的时间间隔"); //类型可以用来存储单位为天和秒的时间间隔
        public static final FieldDataTypeDef ORACLE_INTERVAL_YEAR_TO_MONTH = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "INTERVAL_YEAR_TO_MONTH", "Oracle.(INTERVAL YEAR TO MONTH) ---------- 存储一段时间差, 精确到年和月"); //该数据类型常用来表示一段时间差, 注意时间差只精确到年和月. precision为年或月的精确域, 有效范围是0到9, 默认值为2.
        public static final FieldDataTypeDef ORACLE_TIMESTAMP = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "TIMESTAMP", "Oracle.(TIMESTAMP) ----------------------- DATE + 小数秒");//它包括了所有DATE数据类型的年月日时分秒的信息，而且包括了小数秒的信息
        public static final FieldDataTypeDef ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "TIMESTAMP_WITH_LOCAL_TIME_ZONE", "Oracle.(TIMESTAMP WITH LOCAL TIME ZONE) -- TIMESTAMP + 当地时区");//精确时间类型，在用户提交时间给数据库时，该类型会转换成数据库的时区来保存数据，即数据库保存的时间是数据库本地时区，当别的用户访问数据库时oracle会自动将该时间转换成当前客户端的时间。
        public static final FieldDataTypeDef ORACLE_TIMESTAMP_WITH_TIME_ZONE = new FieldDataTypeDef(DbType.ORACLE.getDbCode(), "TIMESTAMP_WITH_TIME_ZONE", "Oracle.(TIMESTAMP WITH TIME ZONE) -------- TIMESTAMP + 时区，时区时差(TZH)范围：-12 ~ 13");//精确时间类型，该类型在TIMESTAMP类型的基础上增加了时区的信息. 注意时区时差(TZH)的范围是"-12 ~ 13",
        /** MySQL数据类型定义
         *数值类型 类型 大小 范围（有符号） 范围（无符号） 用途
         */
        public static final FieldDataTypeDef MYSQL_TINYINT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TINYINT", "小整数值，1字节，范围(-128，127)，无符号范围(0，255)");
        public static final FieldDataTypeDef MYSQL_SMALLINT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "SMALLINT", "2 字节 (-32 768，32 767) (0，65 535) 大整数值");
        public static final FieldDataTypeDef MYSQL_MEDIUMINT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "MEDIUMINT", "3 字节 (-8 388 608，8 388 607) (0，16 777 215) 大整数值");
        public static final FieldDataTypeDef MYSQL_INT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "INT", "INT或INTEGER 4 字节 (-2 147 483 648，2 147 483 647) (0，4 294 967 295) 大整数值");
        public static final FieldDataTypeDef MYSQL_BIGINT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "BIGINT", "8 字节 (-9 233 372 036 854 775 808，9 223 372 036 854 775 807) (0，18 446 744 073 709 551 615) 极大整数值");
        public static final FieldDataTypeDef MYSQL_FLOAT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "FLOAT", "4 字节 范围(-3.402 823 466 E+38，1.175 494 351 E-38)，0，无符号范围(1.175 494 351 E-38，3.402 823 466 351 E+38) 单精度"); //0，(1.175 494 351 E-38，3.402 823 466 E+38)
        /** 浮点数值 */
        public static final FieldDataTypeDef MYSQL_DOUBLE = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "DOUBLE", "8 字节 双精度"); //(1.797 693 134 862 315 7 E+308，2.225 073 858 507 201 4 E-308)，0，(2.225 073 858 507 201 4 E-308，1.797 693 134 862 315 7 E+308) 0，(2.225 073 858 507 201 4 E-308，1.797 693 134 862 315 7 E+308)
        public static final FieldDataTypeDef MYSQL_DECIMAL = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "DECIMAL", "对DECIMAL(M,D) ，如果M>D，为M+2否则为D+2 依赖于M和D的值 依赖于M和D的值 小数值");
        //字符串类型
        public static final FieldDataTypeDef MYSQL_CHAR = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "CHAR", "0-255字节 定长字符串");
        public static final FieldDataTypeDef MYSQL_VARCHAR = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "VARCHAR", "0-255字节 变长字符串");
        public static final FieldDataTypeDef MYSQL_TINYBLO = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TINYBLOB", "0-255字节 不超过 255 个字符的二进制字符串");
        public static final FieldDataTypeDef MYSQL_TINYTEXT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TINYTEXT", "0-255字节 短文本字符串");
        public static final FieldDataTypeDef MYSQL_BLOB = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "BLOB", "0-65 535字节 二进制形式的长文本数据");
        public static final FieldDataTypeDef MYSQL_TEXT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TEXT", "0-65 535字节 长文本数据");
        public static final FieldDataTypeDef MYSQL_MEDIUMBLOB = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "MEDIUMBLOB", "0-16 777 215字节 二进制形式的中等长度文本数据");
        public static final FieldDataTypeDef MYSQL_MEDIUMTEXT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "MEDIUMTEXT", "0-16 777 215字节 中等长度文本数据");
        public static final FieldDataTypeDef MYSQL_LOGNGBLOB = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "LOGNGBLOB", "0-4 294 967 295字节 二进制形式的极大文本数据");
        public static final FieldDataTypeDef MYSQL_LONGTEXT = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "LONGTEXT", "0-4 294 967 295字节 极大文本数据");
        /** 时间日期类型 */
        public static final FieldDataTypeDef MYSQL_DATE = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "DATE", "3字节 1000-01-01/9999-12-31 YYYY-MM-DD 日期值");
        public static final FieldDataTypeDef MYSQL_TIME = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TIME", "3字节 '-838:59:59'/'838:59:59' HH:MM:SS 时间值或持续时间");
        public static final FieldDataTypeDef MYSQL_YEAR = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "YEAR", "1字节 1901/2155 YYYY 年份值");
        public static final FieldDataTypeDef MYSQL_DATETIME = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "DATETIME", "8字节 1000-01-01 00:00:00/9999-12-31 23:59:59 YYYY-MM-DD HH:MM:SS 混合日期和时间值");
        public static final FieldDataTypeDef MYSQL_TIMESTAMP = new FieldDataTypeDef(DbType.MYSQL.getDbCode(), "TIMESTAMP", "8字节 1970-01-01 00:00:00/2037 年某时 YYYYMMDD HHMMSS 混合日期和时间值，时间戳");

        public FieldDataTypeDef(String dbCode, String fieldType, String fieldTypeName) {
            this.dbCode = dbCode;
            this.fieldType = fieldType;
            this.fieldTypeName = fieldTypeName;

            dbFieldTypes.put(dbCode + "." + fieldType, this);
        }

        public static LinkedHashMap<String, String> getMap() {
            LinkedHashMap<String, String> returnValue = new LinkedHashMap<String, String>();
            for (FieldDataTypeDef bean : dbFieldTypes.values()) {
                returnValue.put(bean.getDbCode() + "." + bean.getFieldType(), "(" + bean.getDbCode() + ").(" + bean.getFieldType() + ")-->" + bean.getFieldTypeName());
            }

            return returnValue;
        }

        public static FieldDataTypeDef findEntityByDbCodeAndFieldTypeCode(String dbCode, String fieldTypeCode) {
            return dbFieldTypes.get(dbCode + "." + fieldTypeCode);
        }

        public String getDbCode() {
            return dbCode;
        }

        public String getFieldType() {
            return fieldType;
        }

        public String getFieldTypeName() {
            return fieldTypeName;
        }

        public static LinkedHashMap<String, FieldDataTypeDef> getDbFieldTypes() {
            return dbFieldTypes;
        }
    }
}
