import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats, _}
import org.scalatest.{FlatSpec, Matchers}
import spray.http.{BasicHttpCredentials, StatusCodes}
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration._
import scala.io.Source

/**
 * @author anna
 * @since 14.03.16
 */

class CoreServiceTest extends FlatSpec with Matchers with ScalatestRouteTest with CoreService {
  implicit val timeout = RouteTestTimeout(5.seconds)

  implicit def json4sFormats: Formats = DefaultFormats

  def actorRefFactory = system

  val api = new TestApi()

  val wrongToken = BasicHttpCredentials("56e5c6c20d00000d00239ae0")
  val rightToken = BasicHttpCredentials("56e665c00100000100a84169")
  "CoreService" should "add new note" in {
    Put("/api/note?accessToken=56e665c00100000100a84169", parse(Source.fromURL(this.getClass.getResource("new_note.json")).mkString)) ~> addCredentials(rightToken) ~> noteRoute ~> check {
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("response/add_note.json")).mkString))
      status should be(StatusCodes.OK)
    }

    Put("/api/note?accessToken=56e665c00100000100a84169", parse(Source.fromURL(getClass.getResource("invalid_new_note.json")).mkString)) ~> addCredentials(rightToken) ~> noteRoute ~> check {
      status should be(StatusCodes.BadRequest)
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("response/bad_request.json")).mkString))
    }

    Put("/api/note?accessToken=56e5c6c20d00000d00239ae0", parse(Source.fromURL(getClass.getResource("new_note.json")).mkString)) ~> addCredentials(wrongToken) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.Unauthorized)
    }
  }

  it should "return note list" in {
    Get("/api/note?accessToken=56e665c00100000100a84169") ~> addCredentials(rightToken) ~> noteRoute ~> check {
      status should be(StatusCodes.OK)
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("response/noteList.json")).mkString))
    }
    Get("/api/note?accessToken=56e5c6c20d00000d00239ae0") ~> addCredentials(wrongToken) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.Unauthorized)
    }
  }

  it should "return note by id" in {
    Get("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e665c00100000100a84169") ~> addCredentials(rightToken) ~> noteRoute ~> check {
      status should be(StatusCodes.OK)
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("/response/note/56e2f4a6ab550ef9528836f9.json")).mkString))
    }

    Get("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e5c6c20d00000d00239ae0") ~> addCredentials(wrongToken) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.Unauthorized)
    }
  }

  it should "delete note by id" in {
    Delete("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e665c00100000100a84169") ~> addCredentials(rightToken) ~> noteRoute ~> check {
      status should be(StatusCodes.NoContent)
    }
    Delete("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e5c6c20d00000d00239ae0") ~> addCredentials(wrongToken) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.Unauthorized)
    }
  }

  it should "update note" in {
    Post("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e665c00100000100a84169", parse(Source.fromURL(this.getClass.getResource("updated_note.json")).mkString)) ~> addCredentials(rightToken) ~> noteRoute ~> check {
      status should be(StatusCodes.NoContent)
    }
    Post("/api/note/56e2f4a6ab550ef9528836f9?accessToken=56e5c6c20d00000d00239ae0", parse(Source.fromURL(this.getClass.getResource("updated_note.json")).mkString)) ~> addCredentials(rightToken) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.Unauthorized)
    }
  }

  it should "return token is user exists" in {
    Post("/api/login", parse(Source.fromURL(this.getClass.getResource("existed_user.json")).mkString)) ~> noteRoute ~> check {
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("response/get_token.json")).mkString))
      status should be(StatusCodes.OK)
    }

    Post("/api/login", parse(Source.fromURL(this.getClass.getResource("not_existed_user.json")).mkString)) ~> sealRoute(noteRoute) ~> check {
      status should be(StatusCodes.BadRequest)
      parse(entity.data.asString) should be(parse(Source.fromURL(getClass.getResource("response/wrong_user.json")).mkString))
    }
  }

}
