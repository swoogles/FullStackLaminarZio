package fullstack

import zio.schema.{Schema, derived}

case class PageState(
                      priority: Option[String]
                    ) derives Schema
