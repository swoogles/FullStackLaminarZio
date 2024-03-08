package testvite

import fullstack.Endpoints
import fullstack.PageState
import zio.http.{Client, URL}
import zio.http.endpoint.{EndpointExecutor, EndpointLocator}
import zio.{Runtime, Scope, Unsafe, ZIO, ZNothing}

case class ZioClient(client: Client, runtime: Runtime[Any]):
  val locator =
    EndpointLocator.fromURL:
      URL.decode:
         "http://localhost:8080"
      .toOption.get

  private val executor: EndpointExecutor[Unit] =
    EndpointExecutor(client, locator, ZIO.unit)

  def zioHelper[E <: Throwable, A](z: ZIO[Scope, E, A]) =
    Unsafe.unsafe {
      implicit unsafe =>
        runtime.unsafe.runToFuture(
          z.provide(Scope.default)
        )
    }

  def updateState(pageState: PageState) =
    zioHelper:
      executor:
        Endpoints.post:
          pageState

  def getState(): ZIO[Scope, ZNothing, PageState] =
    executor:
      Endpoints.getPageState(())
