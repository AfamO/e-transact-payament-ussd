/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.util;

/**
 *
 * @author HP
 */
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class SmsSender {
    @Value("${sms.sid}")
    public String ACCOUNT_SID;
    @Value("${sms.token}")
    public String AUTH_TOKEN;
    @Value("${sms.from}")
    public String FROM;
    public void sendSMS(String to, String text){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new PhoneNumber(to), // to
                        new PhoneNumber(FROM), // from
                        text)
                .create();

        System.out.println("The sms sid:: "+message.getSid());
    }
}
