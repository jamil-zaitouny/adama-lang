/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.mysql.DataBase;

import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeMap;

public class Secrets {
  /** This is fundamentally unsafe as it puts both the public and private key (granted, encrypted) in the datbase. Instead, we should only put the public key in, store private keys in memory, and then index the keys so public keys can be looked up by id */
  @Deprecated
  public static SigningKeyPair getOrCreateDocumentSigningKey(DataBase dataBase, String masterKey, String space, String key) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String resultSecret = null;
      {
        String sql = "SELECT `secret` FROM `" + dataBase.databaseName + "`.`document_secrets` WHERE `space`=? AND `key`=? AND `name`='signing'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, space);
          statement.setString(2, key);
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              return new SigningKeyPair(masterKey, rs.getString(1));
            }
          }
        }
      }
      resultSecret = SigningKeyPair.generate(masterKey);
      {
        String sql = "INSERT INTO `" + dataBase.databaseName + "`.`document_secrets` (`space`, `key`, `name`, `secret`) VALUES (?,?, 'signing', ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, space);
          statement.setString(2, key);
          statement.setString(3, resultSecret);
          statement.execute();
        }
      }

      return new SigningKeyPair(masterKey, resultSecret);
    });
  }


  /** insert a secret key */
  public static int insertSecretKey(DataBase dataBase, String space, String privateKeyEncrypted) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "INSERT INTO `" + dataBase.databaseName + "`.`secrets` (`space`, `encrypted_private_key`) VALUES (?,?)";
      try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, space);
        statement.setString(2, privateKeyEncrypted);
        statement.execute();
        return DataBase.getInsertId(statement);
      }
    });
  }

  /** get the private key by an id */
  @Deprecated
  public static String getPrivateKey(DataBase dataBase, String space, int keyId) throws Exception {
    return dataBase.transactSimple((connection) -> {
      String sql = "SELECT `encrypted_private_key` FROM `" + dataBase.databaseName + "`.`secrets` WHERE `id`=? AND `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, keyId);
        statement.setString(2, space);
        try (ResultSet rs = statement.executeQuery()) {
          if (rs.next()) {
            return rs.getString(1);
          }
        }
      }
      throw new ErrorCodeException(ErrorCodes.MYSQL_FAILED_FINDING_SECRET_KEY);
    });
  }

  public static TreeMap<Integer, PrivateKeyBundle> getKeys(DataBase dataBase, String masterKey, String space) throws Exception {
    return dataBase.transactSimple((connection) -> {
      TreeMap<Integer, PrivateKeyBundle> keys = new TreeMap<>();
      String sql = "SELECT `id`, `encrypted_private_key` FROM `" + dataBase.databaseName + "`.`secrets` WHERE `space`=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, space);
        try (ResultSet rs = statement.executeQuery()) {
          while (rs.next()) {
            keys.put(rs.getInt(1), PrivateKeyBundle.fromDisk(rs.getString(2), masterKey));
          }
        }
      }
      return keys;
    });
  }
}
