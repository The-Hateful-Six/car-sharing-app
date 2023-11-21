package thehatefulsix.carsharingapp.payment.strategy;

import thehatefulsix.carsharingapp.model.payment.PaymentType;

public interface OperationStrategy {

    OperationHandler get(PaymentType paymentType);
}
