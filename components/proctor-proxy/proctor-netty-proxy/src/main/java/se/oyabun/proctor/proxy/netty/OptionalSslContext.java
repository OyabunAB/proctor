package se.oyabun.proctor.proxy.netty;

import io.netty.handler.ssl.SslContext;

import java.util.Optional;
import java.util.function.Consumer;

public class OptionalSslContext {

    private Optional<SslContext> optionlSslContext;

    public OptionalSslContext() {

        this.optionlSslContext = Optional.empty();

    }

    public OptionalSslContext(SslContext sslContext) {

        this.optionlSslContext = Optional.of(sslContext);

    }

    public void ifPresent(Consumer<SslContext> consumer) {

        this.optionlSslContext.ifPresent(consumer);

    }

    public boolean isPresent() {

        return this.optionlSslContext.isPresent();

    }

    public SslContext get() {

        return this.optionlSslContext.get();

    }

}
