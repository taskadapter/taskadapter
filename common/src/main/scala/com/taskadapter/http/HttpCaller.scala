package com.taskadapter.http

import com.taskadapter.connector.common.ConfigUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpPut, HttpRequestBase}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

object HttpCaller {
  val httpclient = new DefaultHttpClient
  val gson = ConfigUtils.createDefaultGson

  def get[C](url: String, c: Class[C]): C = {
    val request = new HttpGet(url)

    val httpResponse = httpclient.execute(request)
    val responseEntity: HttpEntity = httpResponse.getEntity
    val responseBody = EntityUtils.toString(responseEntity)
    val result = gson.fromJson(responseBody, c)
    result
  }

  def post(url: String, obj: String): String = {
    val entity = new StringEntity(obj)
    val request = new HttpPost(url)
    request.setEntity(entity)
    execute(request, classOf[String])
  }

  def post[C](url: String, c: Class[C]): C = {
    val request = new HttpPost(url)
    execute(request, c)
  }

  def put[C](url: String, c: Class[C]): C = {
    val request = new HttpPut(url)
    execute(request, c)
  }

  def execute[C](request: HttpRequestBase, c: Class[C]): C = {
    val httpResponse = httpclient.execute(request)
    val responseEntity: HttpEntity = httpResponse.getEntity
    val responseBody = EntityUtils.toString(responseEntity)
    val result = gson.fromJson(responseBody, c)
    result
  }
}
