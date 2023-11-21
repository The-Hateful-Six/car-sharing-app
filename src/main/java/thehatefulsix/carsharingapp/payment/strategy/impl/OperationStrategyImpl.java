package thehatefulsix.carsharingapp.payment.strategy.impl;

import java.util.HashMap;
import org.springframework.stereotype.Component;
import thehatefulsix.carsharingapp.model.PaymentType;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;
import thehatefulsix.carsharingapp.payment.strategy.OperationStrategy;

@Component
public class OperationStrategyImpl implements OperationStrategy {
    private final HashMap<PaymentType, OperationHandler> paymentStrategy;

    public OperationStrategyImpl() {
        paymentStrategy = new HashMap<>();
        paymentStrategy.put(PaymentType.PAYMENT, new PaymentOperation());
        paymentStrategy.put(PaymentType.FINE, new FineOperation());
    }

    @Override
    public OperationHandler get(PaymentType paymentType) {
        return paymentStrategy.get(paymentType);
    }
}
