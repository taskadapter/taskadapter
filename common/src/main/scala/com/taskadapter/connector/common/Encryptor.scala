package com.taskadapter.connector.common

trait Encryptor {
  def encrypt(string: String, key: String): String

  def encrypt(string: String): String

  def decrypt(string: String, key: String): String

  def decrypt(string: String): String
}
