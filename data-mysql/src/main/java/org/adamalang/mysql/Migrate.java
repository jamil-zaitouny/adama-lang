/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql;

import org.adamalang.mysql.contracts.MigrationStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;

public class Migrate {
  public static void copy(DataBase from, DataBase to, MigrationStatus status) throws Exception {
    try (Connection _from = from.pool.getConnection()) {
      try (Connection _to = to.pool.getConnection()) {
      {
        status.table("directory");
        String _walk = "SELECT `id`, `space`, `key`, `created`, `updated`, `head_seq`, `need_gc`, `type`, `region`, `machine`, `archive`, `deleted`, `delta_bytes`, `asset_bytes` FROM `" + from.databaseName + "`.`directory` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`directory` (`space`, `key`, `created`, `updated`, `head_seq`, `need_gc`, `type`, `region`, `machine`, `archive`, `deleted`, `delta_bytes`, `asset_bytes`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setString(1, rs.getString(2));
            _ins.setString(2, rs.getString(3));
            _ins.setDate(3, rs.getDate(4));
            _ins.setDate(4, rs.getDate(5));
            _ins.setInt(5, rs.getInt(6));
            _ins.setInt(6, rs.getInt(7));
            _ins.setInt(7, rs.getInt(8));
            _ins.setString(8, rs.getString(9));
            _ins.setString(9, rs.getString(10));
            _ins.setString(10, rs.getString(11));
            _ins.setInt(11, rs.getInt(12));
            _ins.setLong(12, rs.getLong(13));
            _ins.setLong(13, rs.getLong(14));
            _ins.execute();
          }
        }, _walk);
      }
      HashMap<Integer, Integer> _index_emails = new HashMap<>();
      {
        status.table("emails");
        String _walk = "SELECT `id`, `email`, `profile`, `password`, `balance`, `credit_carry_limit`, `payment_info_json`, `created`, `validations`, `last_validated` FROM `" + from.databaseName + "`.`emails` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`emails` (`email`, `profile`, `password`, `balance`, `credit_carry_limit`, `payment_info_json`, `created`, `validations`, `last_validated`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setString(1, rs.getString(2));
            _ins.setString(2, rs.getString(3));
            _ins.setString(3, rs.getString(4));
            _ins.setInt(4, rs.getInt(5));
            _ins.setInt(5, rs.getInt(6));
            _ins.setString(6, rs.getString(7));
            _ins.setDate(7, rs.getDate(8));
            _ins.setInt(8, rs.getInt(9));
            _ins.setDate(9, rs.getDate(10));
            _ins.execute();
            _index_emails.put(rs.getInt(1), DataBase.getInsertId(_ins));
          }
        }, _walk);
      }
      {
        status.table("initiations");
        String _walk = "SELECT `id`, `user`, `hash`, `created`, `expires` FROM `" + from.databaseName + "`.`initiations` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`initiations` (`user`, `hash`, `created`, `expires`) VALUES (?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_emails.get(rs.getInt(2)));
            _ins.setString(2, rs.getString(3));
            _ins.setDate(3, rs.getDate(4));
            _ins.setDate(4, rs.getDate(5));
            _ins.execute();
          }
        }, _walk);
      }
      {
        status.table("email_keys");
        String _walk = "SELECT `id`, `user`, `public_key`, `created`, `expires` FROM `" + from.databaseName + "`.`email_keys` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`email_keys` (`user`, `public_key`, `created`, `expires`) VALUES (?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_emails.get(rs.getInt(2)));
            _ins.setString(2, rs.getString(3));
            _ins.setDate(3, rs.getDate(4));
            _ins.setDate(4, rs.getDate(5));
            _ins.execute();
          }
        }, _walk);
      }
      HashMap<Integer, Integer> _index_spaces = new HashMap<>();
      {
        status.table("spaces");
        String _walk = "SELECT `id`, `owner`, `name`, `enabled`, `storage_bytes`, `plan`, `rxhtml`, `policy`, `hash`, `created`, `updated` FROM `" + from.databaseName + "`.`spaces` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`spaces` (`owner`, `name`, `enabled`, `storage_bytes`, `plan`, `rxhtml`, `policy`, `hash`, `created`, `updated`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_emails.get(rs.getInt(2)));
            _ins.setString(2, rs.getString(3));
            _ins.setInt(3, rs.getInt(4));
            _ins.setLong(4, rs.getLong(5));
            _ins.setString(5, rs.getString(6));
            _ins.setString(6, rs.getString(7));
            _ins.setString(7, rs.getString(8));
            _ins.setString(8, rs.getString(9));
            _ins.setDate(9, rs.getDate(10));
            _ins.setDate(10, rs.getDate(11));
            _ins.execute();
            _index_spaces.put(rs.getInt(1), DataBase.getInsertId(_ins));
          }
        }, _walk);
      }
      {
        status.table("grants");
        String _walk = "SELECT `id`, `space`, `user`, `role` FROM `" + from.databaseName + "`.`grants` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`grants` (`space`, `user`, `role`) VALUES (?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_spaces.get(rs.getInt(2)));
            _ins.setInt(2, _index_emails.get(rs.getInt(3)));
            _ins.setInt(3, rs.getInt(4));
            _ins.execute();
          }
        }, _walk);
      }
      {
        status.table("authorities");
        String _walk = "SELECT `id`, `owner`, `authority`, `keystore`, `created` FROM `" + from.databaseName + "`.`authorities` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`authorities` (`owner`, `authority`, `keystore`, `created`) VALUES (?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_emails.get(rs.getInt(2)));
            _ins.setString(2, rs.getString(3));
            _ins.setString(3, rs.getString(4));
            _ins.setDate(4, rs.getDate(5));
            _ins.execute();
          }
        }, _walk);
      }
      HashMap<Integer, Integer> _index_secrets = new HashMap<>();
      {
        status.table("secrets");
        String _walk = "SELECT `id`, `space`, `encrypted_private_key` FROM `" + from.databaseName + "`.`secrets` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`secrets` (`space`, `encrypted_private_key`) VALUES (?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setString(1, rs.getString(2));
            _ins.setString(2, rs.getString(3));
            _ins.execute();
            _index_secrets.put(rs.getInt(1), DataBase.getInsertId(_ins));
          }
        }, _walk);
      }
      {
        status.table("domains");
        String _walk = "SELECT `id`, `owner`, `space`, `key`, `route`, `domain`, `certificate`, `automatic`, `automatic_timestamp`, `created`, `updated` FROM `" + from.databaseName + "`.`domains` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`domains` (`owner`, `space`, `key`, `route`, `domain`, `certificate`, `automatic`, `automatic_timestamp`, `created`, `updated`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setInt(1, _index_emails.get(rs.getInt(2)));
            _ins.setString(2, rs.getString(3));
            _ins.setString(3, rs.getString(4));
            _ins.setInt(4, rs.getInt(5));
            _ins.setString(5, rs.getString(6));
            _ins.setString(6, rs.getString(7));
            _ins.setInt(7, rs.getInt(8));
            _ins.setLong(8, rs.getLong(9));
            _ins.setDate(9, rs.getDate(10));
            _ins.setDate(10, rs.getDate(11));
            _ins.execute();
          }
        }, _walk);
      }
      {
        status.table("metrics");
        String _walk = "SELECT `id`, `space`, `key`, `metrics` FROM `" + from.databaseName + "`.`metrics` ORDER BY `id`";
        String _insert = "INSERT INTO `" + to.databaseName + "`.`metrics` (`space`, `key`, `metrics`) VALUES (?, ?, ?)";
        DataBase.walk(_from, (rs) -> {
          try (PreparedStatement _ins = _to.prepareStatement(_insert, Statement.RETURN_GENERATED_KEYS)) {
            _ins.setString(1, rs.getString(2));
            _ins.setString(2, rs.getString(3));
            _ins.setString(3, rs.getString(4));
            _ins.execute();
          }
        }, _walk);
      }
      }
    }
  }
}
