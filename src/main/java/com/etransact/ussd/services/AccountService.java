/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.services;

import com.etransact.ussd.model.Account;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author HP
 */
public interface AccountService {
    Account createAccount(Map usersDetails);
    Number getUserAccountBalance(Account userAccount, String pin);
    Account getUserAccount(String phoneNumber);
    Boolean depositUserFund(String phoneNumber,Double amount);
    Map withdraw(Account userAccount, String pin,Double amount);
    
}
