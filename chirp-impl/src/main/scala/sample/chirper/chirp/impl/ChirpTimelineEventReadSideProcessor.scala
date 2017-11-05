package sample.chirper.chirp.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import sample.chirper.chirp.api.Chirp

import scala.concurrent.{ExecutionContext, Future}

class ChirpTimelineEventReadSideProcessor(
                                           db: CassandraSession,
                                           readSide: CassandraReadSide
                                         )(implicit ec: ExecutionContext) extends ReadSideProcessor[ChirpTimelineEvent] {

  @volatile
  private var insertChirp: PreparedStatement = _

  override def buildHandler() = readSide.builder[ChirpTimelineEvent]("ChirpTimelineEventReadSideProcessor")
    .setGlobalPrepare(createTable)
    .setPrepare(_ => prepareInsertChirp())
    .setEventHandler[ChirpAdded](ese => insertChirp(ese.event.chirp))
    .build()

  override def aggregateTags = ???

  // Helpers -----------------------------------------------------------------------------------------------------------

  private def createTable() = {
    db.executeCreateTable(
      """CREATE TABLE IF NOT EXISTS chirp (
        |userId text, timestamp bigint, uuid text, message text,
        |PRIMARY KEY (userId, timestamp, uuid)
        |)""".stripMargin)
  }

  private def prepareInsertChirp(): Future[Done] =
    db.prepare("INSERT INTO chirp (userId, uuid, timestamp, message) VALUES (?, ?, ?, ?)")
      .map { ps =>
        insertChirp = ps
        Done
      }

  private def insertChirp(chirp: Chirp): Future[List[BoundStatement]] = {
    val bindInsertChirp = insertChirp.bind(
      chirp.userId,
      chirp.uuid,
      chirp.timestamp.toEpochMilli,
      chirp.message
    )
    Future.successful(List(bindInsertChirp))
  }
}
