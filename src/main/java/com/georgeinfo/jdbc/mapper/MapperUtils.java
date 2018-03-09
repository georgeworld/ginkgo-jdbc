/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.georgeinfo.jdbc.mapper;

import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.mapper.exception.MapperException;
import com.georgeinfo.jdbc.mapper.exception.TypeMismatchOrIndexInvalidException;
import gbt.config.GeorgeLoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

/**
 * 数据集与数据对象之间的映射处理相关工具方法类<br>
 * 此类的工具方法，很多是参考Spring框架中的方法，因为本Georgeinfo-JDBC框架的设计初衷，就是
 * 尽量减少对外部的依赖，所以并没有为了使用Spring中的几个工具方法而增加对Spring的依赖，而是
 * 把相关的几个工具方法复制了过来。
 */
public class MapperUtils {
    private static final GeorgeLogger LOG = GeorgeLoggerFactory.getLogger(MapperUtils.class);
    private static final Map<Class<?>, PrimitiveType> primitiveTypeMap = new HashMap<Class<?>, PrimitiveType>();
    // 当前数据库驱动，是否实现了JDBC规范的getObject(int,class)方法(JDK7及更高版本的规范)
    private static final boolean jdbcDriverImplementGetObjectMethod
            = hasMethod(ResultSet.class, "getObject", int.class, Class.class);

    static {
        primitiveTypeMap.put(Boolean.class, new PrimitiveType(Boolean.class, boolean.class));
        primitiveTypeMap.put(boolean.class, new PrimitiveType(Boolean.class, boolean.class));
        primitiveTypeMap.put(Byte.class, new PrimitiveType(Byte.class, byte.class));
        primitiveTypeMap.put(byte.class, new PrimitiveType(Byte.class, byte.class));
        primitiveTypeMap.put(Character.class, new PrimitiveType(Character.class, char.class));
        primitiveTypeMap.put(char.class, new PrimitiveType(Character.class, char.class));
        primitiveTypeMap.put(Double.class, new PrimitiveType(Double.class, double.class));
        primitiveTypeMap.put(double.class, new PrimitiveType(Double.class, double.class));
        primitiveTypeMap.put(Float.class, new PrimitiveType(Float.class, float.class));
        primitiveTypeMap.put(float.class, new PrimitiveType(Float.class, float.class));
        primitiveTypeMap.put(Integer.class, new PrimitiveType(Integer.class, int.class));
        primitiveTypeMap.put(int.class, new PrimitiveType(Integer.class, int.class));
        primitiveTypeMap.put(Long.class, new PrimitiveType(Long.class, long.class));
        primitiveTypeMap.put(long.class, new PrimitiveType(Long.class, long.class));
        primitiveTypeMap.put(Short.class, new PrimitiveType(Short.class, short.class));
        primitiveTypeMap.put(short.class, new PrimitiveType(Short.class, short.class));
    }

    /**
     * 得到一个指定类型，所对应的基本类型，如果传入的类型不是基本类型或基本类型的包装类型，那么会返回传入的原始类型
     *
     * @param clazz 待被分析的传入类型
     * @return 得到的基本类型（或原样返回传入类型）
     **/
    public static Class<?> getPrimitiveType(Class<?> clazz) {
        if (clazz == null) {
            throw new MapperException("Parameter[clazz] must not be null.");
        }
        return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeMap.get(clazz).getPrimitiveClazz() : clazz);
    }


    /**
     * 从结果集中，提取指定字段，并转换成指定目标类型
     **/
    public static Object getResultSetValue(ResultSet rs, int index, Class<?> targetType)
            throws SQLException, TypeMismatchOrIndexInvalidException {
        if (targetType == null) {
            return getResultSetValue(rs, index);
        }

        Object value;

        // 判断目标类型，根据目标类型尝试从结果集中获取字段值
        if (String.class == targetType) {
            try {
                return rs.getString(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not string type.");
                throw exception;
            }
        } else if (boolean.class == targetType || Boolean.class == targetType) {
            try {
                value = rs.getBoolean(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not boolean type.");
                throw exception;
            }
        } else if (byte.class == targetType || Byte.class == targetType) {
            try {
                value = rs.getByte(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not byte type.");
                throw exception;
            }
        } else if (short.class == targetType || Short.class == targetType) {
            try {
                value = rs.getShort(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not short type.");
                throw exception;
            }
        } else if (int.class == targetType || Integer.class == targetType) {
            try {
                value = rs.getInt(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not integer type.");
                throw exception;
            }
        } else if (long.class == targetType || Long.class == targetType) {
            try {
                value = rs.getLong(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not long integer type.");
                throw exception;
            }
        } else if (float.class == targetType || Float.class == targetType) {
            try {
                value = rs.getFloat(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not float type.");
                throw exception;
            }
        } else if (double.class == targetType || Double.class == targetType
                || Number.class == targetType) {
            try {
                value = rs.getDouble(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not double type.");
                throw exception;
            }
        } else if (BigDecimal.class == targetType) {
            try {
                return rs.getBigDecimal(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not big decimal type.");
                throw exception;
            }
        } else if (java.sql.Date.class == targetType) {
            try {
                return rs.getDate(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not date type.");
                throw exception;
            }
        } else if (java.sql.Time.class == targetType) {
            try {
                return rs.getTime(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not time type.");
                throw exception;
            }
        } else if (java.sql.Timestamp.class == targetType || java.util.Date.class == targetType) {
            try {
                return rs.getTimestamp(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not timestamp type.");
                throw exception;
            }
        } else if (byte[].class == targetType) {
            try {
                return rs.getBytes(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not byte array type.");
                throw exception;
            }
        } else if (Blob.class == targetType) {
            try {
                return rs.getBlob(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not blob type.");
                throw exception;
            }
        } else if (Clob.class == targetType) {
            try {
                return rs.getClob(index);
            } catch (SQLException ex) {
                TypeMismatchOrIndexInvalidException exception
                        = new TypeMismatchOrIndexInvalidException("#Field actual type mismatch: field value of index [" + index + "] is not clob type.");
                throw exception;
            }
        } else {
            // 如果调用方传入的目标类型，是上面判断之外的其他类型，则按照Object来处理
            //如果当前数据库驱动，实现了JDBC规范的rs.getObject(int,class)方法，则返回Object
            if (jdbcDriverImplementGetObjectMethod) {
                try {
                    return rs.getObject(index, targetType);
                } catch (AbstractMethodError err) {
                    LOG.debug("JDBC driver does not implement JDBC 4.1 'getObject(int, Class)' method", err);
                } catch (SQLFeatureNotSupportedException ex) {
                    LOG.debug("JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method", ex);
                } catch (SQLException ex) {
                    LOG.debug("JDBC driver has limited support for JDBC 4.1 'getObject(int, Class)' method", ex);
                }
            }

            // 如果经过以上层层类型判断，都不符合要求，则直接以Object类型返回字段值
            return getResultSetValue(rs, index);
        }

        // Perform was-null check if necessary (for results that the JDBC driver returns as primitives).
        return (rs.wasNull() ? null : value);
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
    }

    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        } else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }

    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        if (clazz == null) {
            throw new MapperException("Class must not be null");
        }
        ;
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new MapperException("Method name must not be null");
        }

        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        } else {
            Set<Method> candidates = new HashSet<Method>(1);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {
                    candidates.add(method);
                }
            }
            if (candidates.size() == 1) {
                return candidates.iterator().next();
            }
            return null;
        }
    }


    public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass)
            throws IllegalArgumentException {

        if (number == null) {
            throw new MapperException("Number must not be null");
        }
        if (targetClass == null) {
            throw new MapperException("Target class must not be null");
        }

        if (targetClass.isInstance(number)) {
            return (T) number;
        } else if (Byte.class == targetClass) {
            long value = number.longValue();
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Byte.valueOf(number.byteValue());
        } else if (Short.class == targetClass) {
            long value = number.longValue();
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Short.valueOf(number.shortValue());
        } else if (Integer.class == targetClass) {
            long value = number.longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Integer.valueOf(number.intValue());
        } else if (Long.class == targetClass) {
            BigInteger bigInt = null;
            if (number instanceof BigInteger) {
                bigInt = (BigInteger) number;
            } else if (number instanceof BigDecimal) {
                bigInt = ((BigDecimal) number).toBigInteger();
            }
            // Effectively analogous to JDK 8's BigInteger.longValueExact()
            if (bigInt != null && (bigInt.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 || bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Long.valueOf(number.longValue());
        } else if (BigInteger.class == targetClass) {
            if (number instanceof BigDecimal) {
                // do not lose precision - use BigDecimal's own conversion
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                // original value is not a Big* number - use standard long conversion
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(number.floatValue());
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(number.doubleValue());
        } else if (BigDecimal.class == targetClass) {
            // always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return (T) new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                    number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
        }
    }

    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        if (text == null) {
            throw new MapperException("Text must not be null");
        }
        if (targetClass == null) {
            throw new MapperException("Target class must not be null");
        }
        String trimmed = trimAllWhitespace(text);

        if (Byte.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (Short.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (Integer.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (Long.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } else if (BigInteger.class == targetClass) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } else if (Float.class == targetClass) {
            return (T) Float.valueOf(trimmed);
        } else if (Double.class == targetClass) {
            return (T) Double.valueOf(trimmed);
        } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
            return (T) new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
        }
    }

    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static <T> T requiredSingleResult(Collection<T> results) throws MapperException {
        return requiredSingleResult(results, null);
    }

    public static <T> T requiredSingleResult(Collection<T> results,
                                             SingleColumnRowCallBack singleColumnRowCallBack)
            throws MapperException {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            throw new MapperException("#Empty result: results must not be empty.");
        }
        if (results.size() > 1) {
            MapperException exception = new MapperException("#Incorrect result size: the result set can only have one record,but found " + size);
            if (singleColumnRowCallBack != null) {
                return (T) singleColumnRowCallBack.whenIncorrectResultSize(results, exception);
            } else {
                throw exception;
            }
        }
        return results.iterator().next();
    }
}