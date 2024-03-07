package fullstack

import zio.http.Method
import zio.http.endpoint.Endpoint

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

}