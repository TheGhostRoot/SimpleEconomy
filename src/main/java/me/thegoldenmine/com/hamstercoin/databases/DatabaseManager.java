package me.thegoldenmine.com.hamstercoin.databases;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static Connection connection;

    public static void setConnection(Connection connection1) {
        connection = connection1;
    }

    @Deprecated
    public static boolean createTableSQL(String table, List<String> colums) {
        try {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            stringBuilder.append(table).append(" ( ");
            if (colums != null) {
                for (String col : colums) {
                    stringBuilder.append(col);
                }
            }

            connection.prepareStatement(stringBuilder.append(" );").toString()).executeUpdate();
            return true;

        } catch (Exception  e) {
            return false;
        }
    }

    public static Map<String, List<Object>> readOutputSQL(ResultSet resultSet) {
        Map<String, List<Object>> result = new HashMap<>();

        if (null == resultSet) {
            return null;
        }

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                result.put(metaData.getColumnName(i), new ArrayList<>());
            }

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.get(metaData.getColumnName(i)).add(resultSet.getObject(i));
                }
            }

            return result;

        } catch (Exception e) {
            return null;
        }
    }

    public static List<Object> setDataSQL(short parameterIndex, List<Object> changes, PreparedStatement preparedStatement)
            throws Exception {
        List<Object> list = new ArrayList<>();

        if (null != changes) {
            for (Object value : changes) {
                if (value == null) {
                    preparedStatement.setNull(parameterIndex, Types.NULL);
                    continue;
                }
                switch (value.getClass().getSimpleName()) {
                    case "String":
                        preparedStatement.setString(parameterIndex, String.valueOf(value));
                        break;

                    case "Integer":
                        preparedStatement.setInt(parameterIndex, Integer.parseInt(String.valueOf(value)));
                        break;

                    case "Long":
                        preparedStatement.setLong(parameterIndex, Long.parseLong(String.valueOf(value)));
                        break;

                    case "Short":
                        preparedStatement.setShort(parameterIndex, Short.parseShort(String.valueOf(value)));
                        break;

                    case "LocalDateTime":
                        preparedStatement.setTimestamp(parameterIndex, Timestamp.valueOf((LocalDateTime) value));
                        break;

                    default:
                        preparedStatement.setObject(parameterIndex, value);
                        break;
                }
                parameterIndex++;
            }
        }
        list.add(parameterIndex);
        list.add(preparedStatement);

        return list;
    }


    public static boolean deleteDataSQL(String table, String condition, List<Object> conditionData) {
        if (condition == null) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ").append(condition);

        try {
            ((PreparedStatement) setDataSQL((short) 1, conditionData,
                    connection.prepareStatement(stringBuilder.toString())).get(1)).executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean addDataSQL(String table, String fields, String values, List<Object> data) {
        if (connection == null) {
            return false;
        }

        try {
            ( (PreparedStatement) setDataSQL((short) 1, data, connection
                    .prepareStatement(new StringBuilder("INSERT INTO ")
                            .append(table).append(" (").append(fields).append(") VALUES (").append(values).append(");")
                            .toString())).get(1) ).executeUpdate();

            return true;

        } catch (Exception e) {
            return false;
        }

    }

    public static boolean editDataSQL(String table, String set_expression,
                                  List<Object> set_data, String condition, List<Object> conditionData) {
        if (connection == null) {
            return false;
        }

        try {
            List<Object> data = setDataSQL((short) 1, set_data, connection
                    .prepareStatement(new StringBuilder("UPDATE ").append(table).append(" SET ").append(set_expression)
                            .append(" WHERE ").append(condition).append(";").toString()));

            ((PreparedStatement) setDataSQL((short) data.get(0), conditionData, (PreparedStatement) data.get(1)).get(1))
                    .executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, List<Object>> getDataSQL(String table, String data_to_get, String condition,
                                                   List<Object> conditionData, Map<String, String> join_data,
                                                   String order, int limit) {

        if (connection == null) {
            return null;
        }

        StringBuilder select_query = new StringBuilder("SELECT ").append(data_to_get).append(" FROM ").append(table);

        // join_data ->  key = table name ; value = condition
        if (null != join_data && !join_data.isEmpty()) {
            for (Map.Entry<String, String> entry : join_data.entrySet()) {
                select_query.append(" JOIN ").append(entry.getKey()).append(" ON ").append(entry.getValue());
            }

        }

        if (!condition.isBlank()) { select_query.append(" WHERE ").append(condition); }

        if (!order.isBlank()) { select_query.append(" ORDER BY ").append(order); }

        if (0 < limit) { select_query.append(" LIMIT ").append(limit); }

        try {

            // We use Java 17 so the hashmap is ordered, and we assume that it is.

            PreparedStatement preparedStatement = connection.prepareStatement(select_query.append(";")
                    .toString());

            return readOutputSQL(((PreparedStatement) setDataSQL((short) 1, conditionData, preparedStatement).get(1)).executeQuery());

        } catch (Exception e) {
            return null;
        }

    }
}
