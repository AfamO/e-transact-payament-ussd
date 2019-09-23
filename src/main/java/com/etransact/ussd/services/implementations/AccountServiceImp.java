/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.services.implementations;

import com.etransact.ussd.model.Account;
import com.etransact.ussd.repository.AccountRepository;
import com.etransact.ussd.services.AccountService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author HP
 */
@Service
public class AccountServiceImp implements AccountService {
    
    @Autowired
    AccountRepository accountRepostory;
    

    @Override
    @Transactional
    public Account createAccount(Map usersDetails) {
        Account newUserAccount = new Account();
        newUserAccount.setFullName(usersDetails.get("FullName").toString());
        newUserAccount.setAddress(usersDetails.get("Address").toString());
        newUserAccount.setPhoneNumber(usersDetails.get("PhoneNumber").toString());
        newUserAccount.setBalanceAmount(Double.parseDouble(usersDetails.get("Amount").toString()));
        newUserAccount.setPin(usersDetails.get("Pin").toString());
        newUserAccount.setUserName(usersDetails.get("UserName").toString());
        newUserAccount.setAccountNumber(usersDetails.get("PhoneNumber").toString()+usersDetails.get("FullName").toString().length());
        Account savedAccount = this.accountRepostory.save(newUserAccount);
        return savedAccount;
    }

    @Override
    public Account getUserAccount(String phoneNumber) {
        Account userAccount = this.accountRepostory.findByPhoneNumber(phoneNumber);
        if(userAccount!=null)
            return userAccount;
        else
            return null;
    }

    @Override
    @Transactional
    public Boolean depositUserFund(String phoneNumber, Double amount) {
        Account userAccount = this.accountRepostory.findByPhoneNumber(phoneNumber);
        if(userAccount!=null)
        {
            Double currentBalance = userAccount.getBalanceAmount();
            userAccount.setBalanceAmount(currentBalance+amount);
            this.accountRepostory.save(userAccount);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Map withdraw(Account account, String pin, Double amount) {
        Map<String,Object> transactionDetails= new HashMap<>();
        if(account!=null){
            if(account.getPin().equalsIgnoreCase(pin)){
                if(account.getBalanceAmount() < amount){
                    Double newBalance = account.getBalanceAmount() - amount;
                    account.setBalanceAmount(newBalance);
                    this.accountRepostory.save(account); // save and update the new user balance
                    transactionDetails.put("withdrawn_amount", amount);
                    transactionDetails.put("status", "success");
                }
                else{
                    transactionDetails.put("status", "Insufficient amount");
                }
            }
            else{
                transactionDetails.put("status", "Invalid pin");
            }
        }
        else{
            transactionDetails.put("status", "Unknown user");
        }
        return transactionDetails;
    }

    @Override
    public Number getUserAccountBalance(Account account, String pin) {
       if(account!=null){
           if(account.getPin().equalsIgnoreCase(pin)){
               return account.getBalanceAmount();
           }
           else
               return -1; // invalid pin
       }
       return null; // unknown user
    }
    
}
