package api

import api.model.Note

import scala.concurrent.Future

/**
 * @author anna
 * @since 15.03.16
 */

trait Api {
  def checkToken(accessToken: String): Boolean

  def newToken(userName: String, password: String): Future[String]

  def addNote(note: Note): Future[String]

  def deleteNote(id: String): Future[Boolean]

  def getAllNotes: Future[List[Note]]

  def getNoteBody(id: String): Future[Note]

  def update(id: String, updatedNote: Note): Future[Boolean]
}