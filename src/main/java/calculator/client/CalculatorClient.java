package calculator.client;

import com.proto.sum.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Hello, I'm a gRPC client.");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient =
                CalculatorServiceGrpc.newBlockingStub(channel);

        Sum sum = Sum.newBuilder().setFirstNumber(1).setSecondNumber(2).build();
        SumRequest request = SumRequest.newBuilder().setSum(sum).build();

        SumResponse response = calculatorClient.sum(request);
        System.out.println(response.getResult());
    }
}
