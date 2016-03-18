package api.model

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

/**
 * @author anna
 * @since 13.03.16
 */

case class User(_id: String = BSONObjectID.generate.stringify, userName: String, password: String, accessToken: String = "")

object User {

  implicit object UserWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument = BSONDocument(
      "_id" -> user._id,
      "userName" -> user.userName,
      "password" -> user.password,
      "accessToken" -> user.accessToken)
  }

  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
      User(
        doc.getAs[String]("_id").get,
        doc.getAs[String]("userName").get,
        doc.getAs[String]("password").get,
        doc.getAs[String]("accessToken").get)
    }
  }

}
