package calculator.server;

import com.proto.sum.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        Sum sum = request.getSum();
        int firstNum = sum.getFirstNumber();
        int secondNum = sum.getSecondNumber();

        int result = firstNum + secondNum;

        SumResponse response = SumResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
