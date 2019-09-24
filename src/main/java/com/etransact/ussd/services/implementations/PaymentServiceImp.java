/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.services.implementations;

import com.etransact.ussd.model.PaymentRequest;
import com.etransact.ussd.model.PaymentResponse;
import com.etransact.ussd.services.PaymentService;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.iyanuadelekan.paystackjava.core.ApiConnection;
import me.iyanuadelekan.paystackjava.core.Customers;
import me.iyanuadelekan.paystackjava.core.Transactions;
import org.springframework.stereotype.Service;
import com.etransact.ussd.util.AppLogger;

/**
 *
 * @author HP
 */
@Service
public class PaymentServiceImp implements PaymentService {
    AppLogger appLogger=new AppLogger(this.getClass());

    @Override
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        Customers customers = new Customers();
        Transactions transactions = new Transactions();
        transactions.initializeTransaction("abcdefghijklmnopafam","5000","afamsimon@gmail.com","planA","http://cigagufu.com/response");
        ApiConnection.shutDown();
        appLogger.log("Paystack Payment Api successfully initiated");
        return new PaymentResponse();
    }

    @Override
    public PaymentResponse verifyPayment(PaymentRequest paymentRequest) {
        throw new UnsupportedOperationException("Yet to be implemented."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
