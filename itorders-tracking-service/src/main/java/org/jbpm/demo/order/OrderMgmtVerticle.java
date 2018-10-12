package org.jbpm.demo.order;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import org.jbpm.demo.itorders.Order;

public class OrderMgmtVerticle extends AbstractVerticle {

    private static final String USER_NAME = System.getProperty("user", "kieserver");
    private static final String PASSWORD = System.getProperty("password", "kieserver1!");

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private Map<Integer, Order> orders = new LinkedHashMap<>();
    private WebClient client;

    @Override
    public void start(Future<Void> fut) {

        WebClientOptions options = new WebClientOptions();
        options.setKeepAlive(false);
        client = WebClient.create(vertx, options);

        Router router = Router.router(vertx);

        router.route("/").handler(StaticHandler.create("assets"));

        router.get("/api/orders").handler(this::getAll);
        router.route("/api/orders*").handler(BodyHandler.create());
        router.post("/api/orders").handler(this::addOne);
        router.get("/api/orders/:id").handler(this::getOne);
        router.put("/api/orders/:id").handler(this::updateOne);
        router.delete("/api/orders/:id").handler(this::deleteOne);

        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8180), result -> {
            if (result.succeeded()) {
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });
    }

    private void addOne(RoutingContext routingContext) {
        final Order order = Json.decodeValue(routingContext.getBodyAsString(), Order.class);
        order.setId(COUNTER.incrementAndGet());
        orders.put(order.getId(), order);

        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(order));
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Order order = orders.get(idAsInteger);
            if (order == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(order));
            }
        }
    }

    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Order order = orders.get(idAsInteger);
            if (order == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                order.setOrderNumber(json.getString("orderNumber"));
                order.setReference(json.getString("reference"));
                order.setOrigin(json.getString("origin"));
                order.setName(json.getString("name"));

                if (order.getOrigin() != null) {
                    JsonObject body = new JsonObject("{\"orderNumber\" : \"" + order.getOrderNumber() +"\"}");
                    // send the order number updates
                    client
                    .postAbs(order.getOrigin())
                    .putHeader("Authorization", getAuthorization())
                    .sendJsonObject(body,  ar -> {
                      if (ar.succeeded()) {
                        System.out.println("Order number sent to " + order.getOrigin());
                      }
                    });
                }

                routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(order));
            }
        }
    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            orders.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(orders.values()));
    }

    private static String getAuthorization() {

        try {
            return "Basic " + Base64.getEncoder().encodeToString((USER_NAME + ":" + PASSWORD).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
