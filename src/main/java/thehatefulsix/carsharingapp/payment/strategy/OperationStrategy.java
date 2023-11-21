package thehatefulsix.carsharingapp.payment.strategy;

import thehatefulsix.carsharingapp.model.PaymentType;

public interface OperationStrategy {

    OperationHandler get(PaymentType paymentType);
}
