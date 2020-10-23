

* Send Request-Response Command To The Server with The RSocket CLI  
```jvm
java -jar rsc.jar --debug --request --data "{\"origin\":\"Client\",\"interaction\":\"Request\"}" --route request-response tcp://localhost:7000
```  

This command contains 3 parts:  
- Interaction type ```- request``` means Request-Response
- Data - ```--data "{\"origin\":\"Client\",\"interaction\":\"Request\"}"```
- Route ```--route request-response``` which matches with the following annotation on com/saral/tutorial/rsocket/server/RsocketController.java
```
    @MessageMapping("request-response")
    Message requestResponse(final Message request) {
        log.info("Received request-response request: {}", request);
        // create a single Message and return it
        return new Message(SERVER, RESPONSE);
    }
``` 

 
```ignorelang
2020-03-27 11:55:25.254 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : sending ->

Frame => Stream ID: 1 Type: REQUEST_RESPONSE Flags: 0b100000000 Length: 69
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 10 72 65 71 75 65 73 74 2d 72 65 73 70 6f 6e 73 |.request-respons|
|00000010| 65                                              |e               |
+--------+-------------------------------------------------+----------------+
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 43 6c 69 65 6e |{"origin":"Clien|
|00000010| 74 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |t","interaction"|
|00000020| 3a 22 52 65 71 75 65 73 74 22 7d                |:"Request"}     |
+--------+-------------------------------------------------+----------------+
```  
```ignorelang
2020-03-27 11:55:25.687 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : receiving
->
Frame => Stream ID: 1 Type: NEXT_COMPLETE Flags: 0b1100000 Length: 81
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 53 65 72 76 65 |{"origin":"Serve|
|00000010| 72 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |r","interaction"|
|00000020| 3a 22 52 65 73 70 6f 6e 73 65 22 2c 22 69 6e 64 |:"Response","ind|
|00000030| 65 78 22 3a 30 2c 22 63 72 65 61 74 65 64 22 3a |ex":0,"created":|
|00000040| 31 35 38 35 33 32 34 35 32 35 7d                |1585324525}     |
+--------+-------------------------------------------------+----------------+
{"origin":"Server","interaction":"Response","index":0,"created":1585324525}
```

* Sending Request-Response from Client
```ignorelang
shell:>request-response
2020-03-27 13:28:04.702  INFO 14004 --- [           main] c.i.t.rsocket.client.RsocketShellClient  : 
Sending one request. Waiting for one response...
2020-03-27 13:28:04.905  INFO 14004 --- [           main] c.i.t.rsocket.client.RsocketShellClient  : 
Response was: Message(origin=Server, interaction=Response, index=0, created=1585330084)
```  

* Send Fire and Forget Command To The Server with The RSocket CLI  
```jvm
java -jar rsc.jar --debug --fnf --data "{\"origin\":\"Client\",\"interaction\":\"Request\"}" --route fire-and-forget tcp://localhost:7000
```  

This command contains 3 parts:  
- Interaction type ```- fnf``` means Fire and Forget
- Data - ```--data "{\"origin\":\"Client\",\"interaction\":\"Request\"}"```
- Route ```--route fire-and-forget``` which matches with the following annotation on com/saral/tutorial/rsocket/server/RsocketController.java
```ignorelang
    @MessageMapping("fire-and-forget")
    public void fireAndForget(final Message request) {
        log.info("Received fire-and-forget request: {}", request);
    }
``` 

```ignorelang
2020-03-27 14:30:31.994 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : sending ->
Frame => Stream ID: 1 Type: REQUEST_FNF Flags: 0b100000000 Length: 68
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 0f 66 69 72 65 2d 61 6e 64 2d 66 6f 72 67 65 74 |.fire-and-forget|
+--------+-------------------------------------------------+----------------+
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 43 6c 69 65 6e |{"origin":"Clien|
|00000010| 74 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |t","interaction"|
|00000020| 3a 22 52 65 71 75 65 73 74 22 7d                |:"Request"}     |
+--------+-------------------------------------------------+----------------+
```  

* Sending Fire and Forget Message from Client
```ignorelang
shell:>fire-and-forget
2020-03-27 14:26:18.030  INFO 25592 --- [           main] c.i.t.rsocket.client.RsocketShellClient  : 
Fire-And-Forget. Sending one request. Expect no response (check server log)...
```  

* Send Stream Command To The Server with The RSocket CLI  
```jvm
java -jar rsc.jar --debug --stream --data "{\"origin\":\"Client\",\"interaction\":\"Request\"}" --route stream tcp://localhost:7000
```  

This command contains 3 parts:  
- Interaction type ```- stream``` means Stream
- Data - ```--data "{\"origin\":\"Client\",\"interaction\":\"Request\"}"```
- Route ```--route stream``` which matches with the following annotation on com/saral/tutorial/rsocket/server/RsocketController.java
```ignorelang
    @MessageMapping("stream")
    Flux<Message> stream(final Message request) {
        log.info("Received stream request: {}", request);
        return Flux
                // create a new indexed Flux emitting one element every second
                .interval(Duration.ofSeconds(1))
                // create a Flux of new Messages using the indexed Flux
                .map(index -> new Message(SERVER, STREAM, index))
                // use the Flux logger to output each flux event
                .log();
    }
``` 

```ignorelang
2020-03-27 14:43:09.815 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : sending ->
Frame => Stream ID: 1 Type: REQUEST_STREAM Flags: 0b100000000 Length: 63
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 06 73 74 72 65 61 6d                            |.stream         |
+--------+-------------------------------------------------+----------------+
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 43 6c 69 65 6e |{"origin":"Clien|
|00000010| 74 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |t","interaction"|
|00000020| 3a 22 52 65 71 75 65 73 74 22 7d                |:"Request"}     |
+--------+-------------------------------------------------+----------------+
```  

```ignorelang
2020-03-27 14:43:11.282 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 79
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 53 65 72 76 65 |{"origin":"Serve|
|00000010| 72 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |r","interaction"|
|00000020| 3a 22 53 74 72 65 61 6d 22 2c 22 69 6e 64 65 78 |:"Stream","index|
|00000030| 22 3a 30 2c 22 63 72 65 61 74 65 64 22 3a 31 35 |":0,"created":15|
|00000040| 38 35 33 33 34 35 39 31 7d                      |85334591}       |
+--------+-------------------------------------------------+----------------+
{"origin":"Server","interaction":"Stream","index":0,"created":1585334591}
2020-03-27 14:43:12.263 DEBUG --- [actor-tcp-nio-1] i.r.FrameLogger : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 79
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 6f 72 69 67 69 6e 22 3a 22 53 65 72 76 65 |{"origin":"Serve|
|00000010| 72 22 2c 22 69 6e 74 65 72 61 63 74 69 6f 6e 22 |r","interaction"|
|00000020| 3a 22 53 74 72 65 61 6d 22 2c 22 69 6e 64 65 78 |:"Stream","index|
|00000030| 22 3a 31 2c 22 63 72 65 61 74 65 64 22 3a 31 35 |":1,"created":15|
|00000040| 38 35 33 33 34 35 39 32 7d                      |85334592}       |
+--------+-------------------------------------------------+----------------+
{"origin":"Server","interaction":"Stream","index":1,"created":1585334592}
...

* Sending Stream Request from Client
```ignorelang
2020-03-27 14:55:58.167  INFO 4148 --- [           main] c.i.t.r.c.RsocketShellClientApplication  : Started RsocketShellClientApplication in 4.575 seconds (JVM running for 5.651)
shell:>stream
2020-03-27 15:00:19.494  INFO 4148 --- [           main] c.i.t.rsocket.client.RsocketShellClient  : 

**** Request-Stream
**** Send one request.
**** Log responses.
**** Type 's' to stop.
2020-03-27 15:00:20.713  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=0, created=1585335620) (Type 's' to stop.)
2020-03-27 15:00:21.697  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=1, created=1585335621) (Type 's' to stop.)
2020-03-27 15:00:22.697  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=2, created=1585335622) (Type 's' to stop.)
2020-03-27 15:00:23.697  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=3, created=1585335623) (Type 's' to stop.)
2020-03-27 15:00:24.697  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=4, created=1585335624) (Type 's' to stop.)
2020-03-27 15:00:25.745  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=5, created=1585335625) (Type 's' to stop.)
2020-03-27 15:00:26.696  INFO 4148 --- [actor-tcp-nio-1] c.i.t.rsocket.client.RsocketShellClient  : Response: Message(origin=Server, interaction=Stream, index=6, created=1585335626) (Type 's' to stop.)
``` 
