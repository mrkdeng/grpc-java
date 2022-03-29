package greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {


        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build();
        server.start();

        System.out.println("Hello gRPC!");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));
        server.awaitTermination();

    }
}
