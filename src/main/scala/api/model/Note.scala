package api.model

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

/**
 * @author anna
 * @since 13.03.16
 */

case class Note(_id: String = BSONObjectID.generate.stringify, title: String, body: String)

object Note {

  implicit object NoteWriter extends BSONDocumentWriter[Note] {
    def write(note: Note): BSONDocument = BSONDocument(
      "_id" -> note._id,
      "title" -> note.title,
      "body" -> note.body)
  }

  implicit object NoteReader extends BSONDocumentReader[Note] {
    def read(doc: BSONDocument): Note = {
      Note(
        doc.getAs[String]("_id").get,
        doc.getAs[String]("title").get,
        doc.getAs[String]("body").get)
    }
  }

}
