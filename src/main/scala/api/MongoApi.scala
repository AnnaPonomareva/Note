package api

import api.dao.{NoteDao, UserDao}
import reactivemongo.api.{DefaultDB, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author anna
 * @since 15.03.16
 */

class MongoApi extends {
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db: DefaultDB = connection("notesDB")
} with UserDao with NoteDao with Api
