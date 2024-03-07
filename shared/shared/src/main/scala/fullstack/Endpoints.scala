package fullstack

import zio.{Scope, ZIO, ZNothing}
import zio.http.*
import zio.http.endpoint.{Endpoint, EndpointExecutor, EndpointLocator}

object Endpoints {
  val post =
    Endpoint(Method.POST / "pageState")
      .in[PageState]
      .out[String]
//      .codecErrorEmptyResponse
//      .codecErrorHandler(CodecError.emptyResponseHandler("Empty response"))
//      .logCodecError(error => s"Codec error: $error")

  val getPageState =
    Endpoint(Method.GET / "pageState")
      .out[PageState]




























  val locator =
    EndpointLocator.fromURL(URL.decode("http://localhost:8080").toOption.get)

  def updateState(client: Client, pageState: PageState) = {
    val executor: EndpointExecutor[Unit] =
      EndpointExecutor(client, locator, ZIO.unit)

    executor(post(pageState))
  }

  def getState(client: Client): ZIO[Scope, ZNothing, PageState] =
    val executor: EndpointExecutor[Unit] =
      EndpointExecutor(client, locator, ZIO.unit)

    executor:
      getPageState.apply(())
}