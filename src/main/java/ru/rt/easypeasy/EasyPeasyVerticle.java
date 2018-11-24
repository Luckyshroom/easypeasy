package ru.rt.easypeasy;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import ru.rt.easypeasy.model.Data;
import ru.rt.easypeasy.util.Runner;

import java.util.*;
import java.util.stream.Collectors;

public class EasyPeasyVerticle extends RestfulVerticle{
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPeasyVerticle.class);

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_NAME = "easy-peasy";

    public static void main(String[] args) {
        Runner.run(EasyPeasyVerticle.class);
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        int port = config().getInteger("http.port", DEFAULT_PORT);
        String host = config().getString("http.address", "localhost");

        Router router = Router.router(vertx);

        enableCorsSupport(router);

        router.get("/consumer").handler(this::consumer);

        vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(port, host)
                .ignoreElement()
                .subscribe(() -> LOGGER.info("Service <" + DEFAULT_NAME + "> start at port: " + port),
                        error -> LOGGER.info(error.getCause()));
    }

    private void consumer(RoutingContext context) {
        Map<String, String> numParams = new TreeMap<>();
        Map<String, String> stringParams = new TreeMap<>();

        Observable.just(context.queryParams().entries())
                .flatMapIterable(e -> e)
                .doOnNext(entry -> {
                    String param = entry.getValue();
                    List<String> list = Arrays.asList(param.replace(',','.').split("\\."));
                    if (list.size() > 2) {
                        stringParams.put(entry.getKey() ,param);
                    } else {
                        int size = list.stream()
                                .filter(this::isLong)
                                .filter(s -> s.length() < 11)
                                .collect(Collectors.toList())
                                .size();
                        if (list.size() == size) {
                            numParams.put(entry.getKey() ,param);
                        } else {
                            stringParams.put(entry.getKey() ,param);
                        }
                    }
                })
                .ignoreElements()
                .subscribe(() -> context.response()
                        .putHeader("content-type", "application/xml")
                        .end(new XmlMapper().writeValueAsString(new Data(numParams, stringParams))));
    }

    private boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
