package gateway.id

import net.petitviolet.ulid4s.ULID
import shared.ddd.IdGenerator

class ULIDGenerator extends IdGenerator[String] {
  override def generate(): String = ULID.generate
}
