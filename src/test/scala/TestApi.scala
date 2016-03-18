import ErrType.WRONG_USR
import api.Api
import api.model.{Note, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author anna
 * @since 14.03.16
 */

class TestApi extends Api {
  val testUserList = List(User("56e594830d00000d00646676", "xyz", "xyz", "56e665c00100000100a84169"))
  val testCollection = List(
    Note("56e2f4a6ab550ef9528836f9", "my note", "very interesting text"),
    Note("56e5507c0100000100678d03", "note", "very boring text")
  )

  def checkToken(accessToken: String): Boolean = {
    getUserByToken(accessToken) match {
      case Some(u) => true
      case none => false
    }
  }

  def newToken(userName: String, password: String): Future[String] = {
    getUserByLoginParole(userName, password) match {
      case Some(u) =>
        Future {
          "56c2411285eff2f03ec6b340"
        }
      case None => throw new Exception(WRONG_USR)
    }
  }

  def addNote(note: Note): Future[String] = {
    Future {
      "56e9507c0100000100678d09"
    }
  }

  def deleteNote(id: String): Future[Boolean] = {
    Future {
      true
    }
  }

  def getAllNotes: Future[List[Note]] = {
    Future {
      testCollection
    }
  }

  def getNoteBody(id: String): Future[Note] = {
    Future {
      testCollection.find(note => note._id == id).get
    }
  }

  def update(id: String, updatedNote: Note): Future[Boolean] = {
    Future {
      true
    }
  }

  private def getUserByToken(accessToken: String): Option[User] = {
    testUserList.find(user => user.accessToken == accessToken)
  }

  private def getUserByLoginParole(userName: String, password: String): Option[User] = {
    testUserList.find(user => user.userName == userName && user.password == password)
  }
}

