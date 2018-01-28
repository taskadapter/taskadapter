package com.taskadapter.connector.mantis.editor

import java.math.BigInteger
import java.rmi.RemoteException
import java.util

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.mantis.{MantisConfig, MantisManagerFactory, MantisUtils}
import com.taskadapter.model.{NamedKeyedObject, NamedKeyedObjectImpl}
import com.taskadapter.web.callbacks.DataProvider

class MantisQueryListLoader(config: MantisConfig, setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  @throws[ConnectorException]
  override def loadData(): util.List[_ <: NamedKeyedObject] = {
    val mgr = MantisManagerFactory.createMantisManager(setup)
    try {
      val pkey = if (config.getProjectKey == null) null
      else new BigInteger(config.getProjectKey)
      val fis = mgr.getFilters(pkey)
      val res = new util.ArrayList[NamedKeyedObject](fis.length)
      for (fi <- fis) {
        res.add(new NamedKeyedObjectImpl(fi.getId.toString, fi.getName))
      }
      res
    } catch {
      case e: RemoteException =>
        throw MantisUtils.convertException(e)
    }
  }
}
