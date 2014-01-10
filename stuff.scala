package com.tradeshift.proxy.external

import com.tradeshift.proxy.main.SystemComponent
import com.tradeshift.scala.IDFormats
import BackendServiceComponent._
import java.util.UUID
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import com.tradeshift.scala.UUIDTools.toUUID
import com.tradeshift.proxy.ProxyServiceComponent
import com.tradeshift.proxy.config.Configuration
import akka.io.IO
import spray.can.Http

trait BackendServiceComponent {
  self: SystemComponent with ProxyServiceComponent =>
    
  val backendService: BackendService

  class BackendService(serviceConfig: Configuration.Service) extends ExternalService(serviceConfig, config, system, IO(Http)(system)) with IDFormats {
<<<<<<< HEAD
    override def prefix = super.prefix + "/rest"
=======
    override def prefix = super.prefix + "/restmaster"
>>>>>>> Master
    
    lazy val proxyServiceComponent = BackendServiceComponent.this
    
    implicit val companyAccountFormat = jsonFormat2(CompanyAccount)
    implicit val userFormat = jsonFormat1(User)

  
    private val companies = makeCache[Option[CompanyAccount]]
    def getCompany(id: UUID) =
      companies(id) {
        val call = asOption[CompanyAccount]
        call(Get("/allcompanies/" + id.toString))
      }

    private val users = makeCache[Option[User]]
    def getUser(id: UUID) =
      users(id) {
        val call = asOption[User]
        call(Get("/users/" + id.toString))
      }

    private val parents = makeCache[Option[UUID]]
    def getParent(tenantId: UUID) =
      parents(tenantId) {
        val call = asOption[CompanyAccount]
        call(Get("/companies/" + tenantId.toString + "/parent")) map { opt =>
          opt map (_.Id) flatMap toUUID
        }
      }

    def clusterId = {
      val call = performRequest() ~> unmarshal[String]
      call(Get("/cluster"))
    }
    
  }
  
}

object BackendServiceComponent {
  case class CompanyAccount(Id: String, State: String) {
    def locked = State == "Locked"
  }
  case class User(State: String) {
    def locked = State == "Locked"
  }
  
}