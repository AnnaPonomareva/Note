import ErrType.{AUTH_TRY_AGAIN, WRONG_USR}
import akka.actor.Actor
import api._
import api.model.Note
import message.{ErrorMessage, ReturnId}
import org.json4s.JsonAST.JObject
import org.json4s.{DefaultFormats, Formats}
import spray.http.StatusCodes
import spray.httpx.Json4sSupport
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author anna
 * @since 09.03.16
 */

class NoteActor extends Actor with CoreService {
  implicit def json4sFormats: Formats = DefaultFormats

  def actorRefFactory = context

  def receive = runRoute(noteRoute)

  val api = new MongoApi()
}


trait CoreService extends HttpService with Json4sSupport {

  protected implicit val eh = ExceptionHandler {
    case e: Throwable =>
//      e.printStackTrace()
      val eMsg = e.getMessage
      val err = if ((eMsg == WRONG_USR) || (eMsg == AUTH_TRY_AGAIN)) {
        "AUTH_ERROR"
      } else {
        "BAD_REQUEST"
      }
      complete(StatusCodes.BadRequest, ErrorMessage(error = err, message = eMsg))
  }

  protected implicit val rh = RejectionHandler {
    case AuthorizationFailedRejection :: _ =>
      complete(StatusCodes.Unauthorized, ErrorMessage(error = "AUTH_ERROR", message = "User or password wrong"))
    case Nil => complete(StatusCodes.NotFound, "The requested resource could not be found.")
  }

  protected val api: Api

  val noteRoute =
    pathPrefix("api") {
      pathPrefix("login") {
        pathEnd {
          post {
            entity(as[JObject]) { json =>
              complete(
                api.newToken(
                  userName = json.values("userName").toString,
                  password = json.values("password").toString
                ).map {
                  ReturnToken
                }
              )

            }
          }
        }
      } ~
        pathPrefix("note") {
          parameters('accessToken.as[String] ? "") { access_token =>
            authorize(api.checkToken(access_token)) {
              pathEnd {
                put {
                  entity(as[JObject]) { json =>
                    complete(
                      api.addNote(json.extract[Note]).map {
                        ReturnId
                      }
                    )
                  }
                } ~
                  get {
                    complete(api.getAllNotes)
                  }
              } ~ pathPrefix(Segment) { noteId =>
                get {
                  complete(api.getNoteBody(noteId))
                } ~
                  delete {
                    complete(api.deleteNote(noteId).map(status => if (status) {
                      StatusCodes.NoContent
                    } else {
                      StatusCodes.BadRequest
                    }))
                  } ~
                  post {
                    entity(as[JObject]) { json =>
                      complete(api.update(noteId, json.extract[Note]).map(status => if (status) {
                        StatusCodes.NoContent
                      } else {
                        StatusCodes.BadRequest
                      }))
                    }
                  }
              }
            }
          }
        }
    }
}
