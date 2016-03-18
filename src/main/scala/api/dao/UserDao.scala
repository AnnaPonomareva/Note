package api.dao

import ErrType.{AUTH_TRY_AGAIN, WRONG_USR}
import api.model.User
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * @author anna
 * @since 11.03.16
 */
trait UserDao {
  val db: DefaultDB

  private val collection: BSONCollection = db[BSONCollection]("user")

  def checkToken(accessToken: String): Boolean = {
    Await.result(
      getUserByToken(accessToken).map {
        case Some(n) => true
        case None => false
      }, 5.seconds
    )
  }

  def newToken(userName: String, password: String): Future[String] = {
    getUserByLoginParole(userName, password).flatMap {
      case Some(user) =>
        val accessToken = BSONObjectID.generate.stringify
        val selector = BSONDocument(
          "userName" -> userName,
          "password" -> password
        )
        collection.update(selector, user.copy(accessToken = accessToken)).map {
          case result if result.ok => accessToken
          case _ => throw new Exception(AUTH_TRY_AGAIN)
        }
      case None => Future.failed(new Exception(WRONG_USR))
    }
  }


  private def getUserByLoginParole(userName: String, password: String): Future[Option[User]] = {
    val selector = BSONDocument(
      "userName" -> userName,
      "password" -> password
    )
    collection.find(selector).cursor[User]().collect[List]().map(_.headOption)
  }

  private def getUserByToken(accessToken: String): Future[Option[User]] = {
    val selector = BSONDocument(
      "accessToken" -> accessToken
    )
    collection.find(selector).cursor[User]().collect[List]().map(_.headOption)
  }

}

