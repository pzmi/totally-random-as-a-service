package io.github.pzmi.totallyrandom;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class TotallyRandomApplication {
    private static final Flux<Integer> generate = Flux.generate(sink -> sink.next(random()));
    private static int[] TOTALLY_RANDOM_NUMBER = {1, 121, 12, 41, 55, 86, 27, 58};
    private static int i = 0; // we don't care about consistency

    public static void main(String[] args) throws InterruptedException {
        RouterFunction router = route(GET("/random"), TotallyRandomApplication::randomResponse);
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(router);

        HttpServer.create("localhost", 8080)
                .newHandler(new ReactorHttpHandlerAdapter(httpHandler))
                .block();

        Thread.currentThread().join();
    }

    private static int random() {
        i++;
        i &= 7;

        return TOTALLY_RANDOM_NUMBER[i];
    }

    private static Mono<ServerResponse> randomResponse(ServerRequest request) {
        return ServerResponse.ok().body(generate.next(), Integer.class);
    }
}
