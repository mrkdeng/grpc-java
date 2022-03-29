package greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static int ONE = 1;
    public static void main(String[] args) {
        System.out.println("Hello, I'm a gRPC client.");
        GreetingClient main = new GreetingClient();
        main.run();
    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
//        doServerStreamingCall(channel);
//        doClientStreamingCall(channel);
       doBiDiStreamingCall(channel);
    }

    private void doUnaryCall(ManagedChannel channel) {
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Ke")
                .setLastName("Deng")
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse response = greetClient.greet(greetRequest);

        System.out.println(response.getResult());
        channel.shutdown();
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Ke")
                .setLastName("Deng")
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();

        greetClient.greetManyTimes(request).forEachRemaining(
                greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        // create an asynchronous client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // we get a response from server, will be called only once
                System.out.println("Received a response from server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // we get error from the server
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder()
                .setFirstName("Ke")
                .setLastName("Deng")).build());

        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder()
                .setFirstName("Minyan")
                .setLastName("Chen")).build());

        // tell the server client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        // create an asynchronous client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Ke", "Minyan").forEach(
                name -> requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                        .setGreeting(Greeting.newBuilder().setFirstName(name)).build())
        );

        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
