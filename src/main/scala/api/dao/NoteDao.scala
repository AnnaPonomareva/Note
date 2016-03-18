package api.dao

import ErrType.{CANT_DELETE, CANT_UPDATE, DOESNT_EXIST, TRY_AGAIN}
import api.model.Note
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author anna
 * @since 11.03.16
 */

trait NoteDao {
  val db: DefaultDB

  private val collection: BSONCollection = db[BSONCollection]("note")

  def addNote(note: Note): Future[String] = {
    collection.insert(note).map {
      case result if result.ok => note._id
      case _ => throw new Exception(TRY_AGAIN)
    }
  }

  def deleteNote(id: String): Future[Boolean] = {
    val selector = BSONDocument(
      "_id" -> id)
    collection.remove(selector).map {
      case result if result.ok => true
      case _ => throw new Exception(CANT_DELETE)
    }
  }

  def getAllNotes: Future[List[Note]] = {
    val selector = BSONDocument()
    collection.find(selector).cursor[Note]().collect[List]()
  }

  def getNoteBody(id: String): Future[Note] = {
    val selector = BSONDocument(
      "_id" -> id)
    getNoteById(id)
  }

  def update(id: String, updatedNote: Note): Future[Boolean] = {
    //  json have to contain all filds exept _id
    val selector = BSONDocument("_id" -> id)
    val note = getNoteById(id)
    note.flatMap(n => {
      collection.update(selector, n.copy(title = updatedNote.title, body = updatedNote.body)).map {
        case result if result.ok => true
        case _ => throw new Exception(CANT_UPDATE)
      }
    })
  }

  private def getNoteById(id: String): Future[Note] = {
    val selector = BSONDocument(
      "_id" -> id)
    collection.find(selector).cursor[Note]().collect[List]().map(note => {
      note.headOption match {
        case Some(n) => n
        case None => throw new Exception(DOESNT_EXIST)
      }
    })
  }

}
