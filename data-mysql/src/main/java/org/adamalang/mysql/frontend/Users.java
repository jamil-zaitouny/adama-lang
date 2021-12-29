/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.mysql.frontend;

import org.adamalang.mysql.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Users {
    public static int getOrCreateUserId(DataBase dataBase, String email) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            {
                String sql = new StringBuilder("SELECT `id` FROM `").append(dataBase.databaseName).append("`.`emails` WHERE email=?").toString();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, email);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }

            {
                String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`emails` (`email`) VALUES (?)").toString();
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, email);
                    statement.execute();
                    return DataBase.getInsertId(statement);
                }
            }
        }
    }

    public static List<String> listKeys(DataBase dataBase, int userId) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            ArrayList<String> keys = new ArrayList<>();
            String sql = new StringBuilder().append("SELECT `public_key` FROM `").append(dataBase.databaseName).append("`.`email_keys` WHERE `user`=").append(userId).toString();
            DataBase.walk(connection, (rs) -> {
                keys.add(rs.getString(1));
            }, sql);
            return keys;
        }
    }

    public static void addKey(DataBase dataBase, int userId, String publicKey, Date expires) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`email_keys` (`user`,`public_key`,`expires`) VALUES (?,?,?)").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setString(2, publicKey);
                statement.setDate(3, new java.sql.Date(expires.getTime()));
                statement.execute();
                return;
            }
        }
    }

    public static int removeAllKeys(DataBase dataBase, int userId) throws Exception {
        try (Connection connection = dataBase.pool.getConnection()) {
            String sql = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`email_keys` WHERE `user`=?").toString();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                return statement.executeUpdate();
            }
        }
    }
}
