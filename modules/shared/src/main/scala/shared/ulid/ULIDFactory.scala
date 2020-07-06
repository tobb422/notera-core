package shared.ulid

import net.petitviolet.ulid4s.ULID

trait ULIDFactory {
  def generate(): String
}

class ULIDFactoryImpl() extends ULIDFactory {
  override def generate(): String = ULID.generate
}
