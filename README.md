# vertx-tls-experiment

## Simple experiment

The goal is to start two verticles at the same time and have one use TLS with a self signed certificate (the certificate is created seperately)
This will then be added to the httpp3 server, in order to meet the requirement that user should not see a difference in the uri (same pages, same ports etc.) even though its being served from udp rather than tcp
