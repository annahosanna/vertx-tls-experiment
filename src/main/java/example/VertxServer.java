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
import java.util.Random;
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

    /*
    router.get("/img/:x/:y").handler(ctx -> {
        ctx.response()
                .putHeader("Content-Type", "image/png")
                .end(image.getPixel(Integer.parseInt(ctx.pathParam("x")), Integer.parseInt(ctx.pathParam("y"))));
      });
      */
    // I feel like this is caching
    Random random = new Random();
    // String responseText = new String("<html><a href=\"/fortune\">fortune</a></html>");
    router
      .route("/*")
      .handler(routingContext -> {
    	  String responseText = new String("<html><a href=\"/fortune" + Integer.toString(random.nextInt()) + "\">fortune</a></html>");  
        routingContext
          .response()
          .putHeader("Content-Type", "text/html")
          .putHeader("Content-Length", Integer.toString(responseText.length()))
          .putHeader("Alt-Svc", "h3=\":443\"; ma=86400, h3-29=\":443\"; ma=86400, h3-Q050=\":443\"; ma=86400, h3-Q046=\":443\"; ma=86400, h3-Q043=\":443\"; ma=86400, quic=\":443\"; ma=86400; v=\"43,46\"")
          .putHeader("Application-Protocol", "h3,quic,h2,http/1.1");
        // .putHeader("X-Quic", "h3");
        
          routingContext.response().write(responseText);
          routingContext.response().end();
      });
    return vertx.createHttpServer(secureOptions).requestHandler(router).listen(8443,"localhost");
  }
}
