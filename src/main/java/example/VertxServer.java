package example;

// import io.vertx.core.VerticleBase;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.*;
import io.vertx.ext.web.Router;
import java.lang.Integer;
import io.vertx.core.dns.AddressResolverOptions;
// import io.vertx.launcher.application.VertxApplication;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;

public class VertxServer extends VerticleBase {

  public static void main(String[] args) {
    // VertxApplication.main(new String[] { Server.class.getName() });
	  AddressResolverOptions addressResolverOptions = new AddressResolverOptions();
	  VertxOptions vertxOptions = new VertxOptions().setAddressResolverOptions(addressResolverOptions);;
    Vertx vertx = Vertx.vertx(vertxOptions);
    System.out.println("Deploying Verticles");
    vertx
      .deployVerticle("example.VertxServer")
      .onComplete(res -> {
        if (res.succeeded()) {
          System.out.println("HTTP Deployment id is: " + res.result());
        } else {
          System.out.println("HTTP Deployment failed!");
          System.out.println(res.cause());
        }
      });
    System.out.println("Deployed Verticles");
  }

  @Override
  public Future<?> start() throws Exception {
	  super.start();
    Router router = Router.router(vertx);

    // I do not know if it just a browser thing but the
    // browser always returns an ssl error
    // but nc responds as expected
    HttpServerOptions secureOptions = new HttpServerOptions();
    secureOptions.setUseAlpn(true);
    secureOptions.setSsl(true);
    secureOptions.setKeyCertOptions(new PemKeyCertOptions().setKeyPath("key.pem").setCertPath("certs.pem"));

    String responseText = new String("Just a test");
    router
      .route("/*")
      .handler(routingContext -> {
        routingContext
          .response()
          .putHeader("Content-Type", "text/plain")
          .putHeader("Content-Length", Integer.toString(responseText.length()))
          .putHeader("Alt-Svc", "h3=\"localhost:8443\", quic=\"localhost:8443\"")
          .putHeader("Application-Protocol", "h3,quic,h2,http/1.1");
          routingContext.response().write(responseText);
          routingContext.response().end();
      });
    return vertx.createHttpServer(secureOptions).requestHandler(router).listen(8443,"localhost");
  }
}
