package testvite

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import com.raquo.laminar.api.L.{*, given}
import fullstack.PageState
import fullstack.Endpoints
import org.scalajs.dom
import zio.http.*
import zio.json.*
import zio.*

object Main extends ZIOAppDefault{

  def run =
    (for
      client <- ZIO.service[Client]
      initialServerState <- Endpoints.getState(client)

      stateVar = Var[PageState](initialServerState)
      _ <- ZIO.attempt:
        render(dom.document.querySelector("#app"), appElement(stateVar, client))
    yield ())
      .provide( Client.default, zio.Scope.default)

  def appElement(stateVarLocal: Var[PageState], client: Client): HtmlElement = {
    val topPrioritySignal = stateVarLocal.signal.map(_.priority)
    
    val clickObserver = Observer[dom.MouseEvent](
      onNext = {
        ev =>
          Unsafe.unsafe { 
            implicit unsafe =>
              runtime.unsafe.runToFuture(
                Endpoints.updateState(
                  client, stateVarLocal.now()
                ).provide(Scope.default)
              )
          }
          println("Page state: " + stateVarLocal.now())
          dom.console.log(ev.screenX)
      }
    )
    val element: Div = div(
      onClick --> clickObserver,
      "Save"
    )

    val billboard =
      div(
        h2("Top Priority: ", child.text <-- topPrioritySignal.map(_.getOrElse("None"))),
        input(
          typ := "text",
          controlled(
            value <-- topPrioritySignal.map(_.getOrElse("")),
            onInput.mapToValue --> stateVarLocal.updater[String]((state, newTopPriority) => state.copy(priority = Some(newTopPriority))),
          ),
        ),
      )

    div(
      h1("Hello Vite!"),
      element,
      billboard,
    )
  }



}
