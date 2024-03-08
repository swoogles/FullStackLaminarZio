package testvite

import com.raquo.laminar.api.L.{*, given}
import fullstack.PageState
import org.scalajs.dom
import zio.http.*
import zio.*

object Main extends ZIOAppDefault{

  def run =
    (for
      client <- ZIO.service[Client].debug
      zioClient = ZioClient(client, runtime)
      initialServerState <- zioClient.getState()

      stateVar = Var[PageState](initialServerState)
      _ <- ZIO.attempt:
        render(
          dom.document.querySelector("#app"),
          appElement(stateVar, zioClient)
        )
    yield ())
      .provide( Client.default, zio.Scope.default)

  def appElement(stateVarLocal: Var[PageState], client: ZioClient): HtmlElement = {
    val topPrioritySignal = stateVarLocal.signal.map(_.priority)

    val clickObserver = Observer[dom.MouseEvent](
      onNext = {
        _ =>
          client.updateState(stateVarLocal.now())
      }
    )
    val saveButton = button(
      cls:="button is-primary",
      onClick --> clickObserver,
      "Upload Priority to Server"
    )

    val billboard =
      div(
        input(
          cls:="input is-primary",
          typ := "text",
          controlled(
            value <-- topPrioritySignal.map(_.getOrElse("")),
            onInput.mapToValue -->
              stateVarLocal.updater[String](
                (state, newTopPriority) =>
                  state.copy(priority = Some(newTopPriority))),
          ),
        ),
      )

    div(
      cls := "container",
      div(
        cls:= "box",
        h1(
          cls:="title",
          "Priority: ",
          child.text <-- topPrioritySignal.map(_.getOrElse("None"))),
        div(
          cls:="section",
          saveButton
        ),
        billboard,
      )
    )
  }


}
