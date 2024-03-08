import fullstack.{Endpoints, PageState}
import zio.*
import zio.http.{Handler, Header, Middleware, Routes, Server}
import zio.http.Middleware.CorsConfig
import zio.http.codec.PathCodec
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}

object BackendServer extends ZIOAppDefault {

  def postRoute(backendState: Ref[PageState]) =
    Endpoints.post.implement:
      Handler.fromFunctionZIO:
        newState =>
          backendState
            .set:
              newState
            .as:
              "Updated state"

  def getRoute(backendState: Ref[PageState]) =
    fullstack.Endpoints
      .getPageState.implement:
        Handler.fromZIO:
          backendState.get

  val openAPI =
    OpenAPIGen.fromEndpoints(
      title = "Endpoint Example",
      version = "1.0",
      Endpoints.getPageState,
      Endpoints.post
    )

  import PathCodec._
  def run =
    for
      backendState <-
//        Ref.make(PageState(Some("**Default state from Server**")))
        Ref.make:
          PageState(
            Some:
              "**Default state from Server**"
          )
      _ <- Server
        .serve(
          (
            Routes(
              postRoute(backendState),
              getRoute(backendState)
            )++ SwaggerUI.routes("docs", openAPI)
            ).toHttpApp @@
          Middleware.debug @@
          Middleware.requestLogging( _ => LogLevel.Error) @@
          Middleware.cors(CorsConfig( allowedOrigin = whatever => Some(Header.AccessControlAllowOrigin.All)))
        )
        .provide(Server.default)
        .exitCode
    yield ()

}