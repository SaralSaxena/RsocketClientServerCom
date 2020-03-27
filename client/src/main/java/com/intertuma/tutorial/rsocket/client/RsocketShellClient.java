package com.intertuma.tutorial.rsocket.client;

import com.intertuma.tutorial.rsocket.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@ShellComponent
public class RsocketShellClient {
    private final RSocketRequester rSocketRequester;
    private static final String CLIENT = "Client";
    private static final String REQUEST = "Request";
    private static final String FIRE_AND_FORGET = "Fire-And-Forget";
    private static final String STREAM = "Stream";
    private static final String CHANNEL = "Channel";
    private static Disposable disposable;

    @Autowired
    public RsocketShellClient(RSocketRequester.Builder rSocketRequesterBuilder) {
        // The requester’s connectTcp() method needs to know the IP address and port of your RSocket server,
        // and you need to tell the requester to block() until a connection is established.
        // After that, you’re ready to communicate with the RSocket server over TCP.
        this.rSocketRequester = rSocketRequesterBuilder
                .connectTcp("localhost", 7000)
                .block();
    }

    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() throws InterruptedException {
        log.info("\nSending one request. Waiting for one response...");
        Message message = this.rSocketRequester
                .route("request-response")
                .data(new Message(CLIENT, REQUEST))
                .retrieveMono(Message.class)
                .block();
        log.info("\nResponse was: {}", message);
    }

    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server log)...");
        this.rSocketRequester
                .route("fire-and-forget")
                .data(new Message(CLIENT, FIRE_AND_FORGET))
                .send()
                .block();
    }

    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        log.info("\n\n**** Request-Stream\n**** Send one request.\n**** Log responses.\n**** Type 's' to stop.");
        disposable = this.rSocketRequester
                .route("stream")
                .data(new Message(CLIENT, STREAM))
                .retrieveFlux(Message.class)
                .subscribe(message -> log.info("Response: {} (Type 's' to stop.)", message));
    }

    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel(){
        log.info("\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

        Mono<Duration> delay1 = Mono.just(Duration.ofSeconds(1));
        Mono<Duration> delay2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
        Mono<Duration> delay3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

        Flux<Duration> settings = Flux.concat(delay1, delay2, delay3)
                .doOnNext(d -> log.info("\nSetting a {}-second interval.\n", d.getSeconds()));

        disposable = this.rSocketRequester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message.class)
                .subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));
    }

    @ShellMethod("Stop streaming.")
    public void s(){
        log.info("Stopping the incoming stream.");
        if(null != disposable){
            disposable.dispose();
        }
        log.info("Stream stopped.");
    }


}
