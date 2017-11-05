package sample.chirper.chirp.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

class ChirpTimelineEntity extends PersistentEntity {

  override type Command = ChirpTimelineCommand[_]
  override type Event = ChirpTimelineEvent
  override type State = this.type

  override def initialState = ???

  override def behavior = ???



}

object ChirpTimelineSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[AddChirp]
  )
}