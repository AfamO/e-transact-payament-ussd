/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.services;
import com.etransact.ussd.model.PaymentRequest;
import com.etransact.ussd.model.PaymentResponse;

/**
 *
 * @author HP
 */
public interface PaymentService {
    PaymentResponse initiatePayment(PaymentRequest paymentRequest);

    PaymentResponse verifyPayment(PaymentRequest paymentRequest);
    
}
