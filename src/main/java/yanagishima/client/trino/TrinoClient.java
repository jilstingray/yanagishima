package yanagishima.client.trino;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

import static io.trino.client.OkHttpUtil.basicAuth;

@RequiredArgsConstructor
public class TrinoClient {
  private static final String TRINO_USER_HEADER = "X-Trino-User";

  private final OkHttpClient httpClient = new OkHttpClient();
  private final String coordinator;
  private final String userName;
  private final Optional<String> user;
  private final Optional<String> password;

  public Response get() throws IOException {
    Request.Builder request = new Request.Builder().url(coordinator + "/v1/query");
    return execute(request);
  }

  public Response get(String queryId) throws IOException {
    Request.Builder request = new Request.Builder().url(coordinator + "/v1/query/" + queryId);
    return execute(request);
  }

  public Response kill(String queryId) throws IOException {
    Request.Builder request = new Request.Builder().url(coordinator + "/v1/query/" + queryId).delete();
    return execute(request);
  }

  private Response execute(Request.Builder request) throws IOException {
    if (userName != null) {
      request.addHeader(TRINO_USER_HEADER, userName);
    }
    OkHttpClient.Builder client = httpClient.newBuilder();
    if (user.isPresent() && password.isPresent()) {
      client.addInterceptor(basicAuth(user.get(), password.get()));
    }
    return client.build().newCall(request.build()).execute();
  }
}
