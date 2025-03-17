package example;

import io.vertx.core.Future;
// import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.*;
import io.vertx.ext.web.Router;
import java.lang.Integer;
// import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
// https://github.com/hakdogan/IntroduceToEclicpseVert.x/tree/master
public class VertxServer extends AbstractVerticle {

  public static void main(String[] args) {
    // VertxApplication.main(new String[] { Server.class.getName() });
  }

  @Override
  public void start(Promise<Void> promise) throws Exception {
    Router router = Router.router(vertx);

    String responseText = new String("Just a test");
    router
      .route()
      .handler(routingContext -> {
        routingContext
          .response()
          .putHeader("Content-Type", "text/plain")
          .putHeader("Content-Length", Integer.toString(responseText.length()))
          .putHeader("Alt-Svc", "\"h3=\":8443")
          .end(responseText);
      });

    vertx.createHttpServer().requestHandler(router).listen(8080);
  }
}
