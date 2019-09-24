/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd;

import com.etransact.ussd.model.Account;
import com.etransact.ussd.model.PaymentRequest;
import com.etransact.ussd.services.AccountService;
import com.etransact.ussd.services.PaymentService;
import com.etransact.ussd.services.implementations.AccountServiceImp;
import com.etransact.ussd.util.AppLogger;
import com.etransact.ussd.util.SmsSender;
import hms.sdp.ussd.MchoiceUssdException;
import hms.sdp.ussd.MchoiceUssdMessage;
import hms.sdp.ussd.MchoiceUssdTerminateMessage;
import hms.sdp.ussd.client.MchoiceUssdReceiver;
import hms.sdp.ussd.client.MchoiceUssdSender;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 *
 * @author HP
 */
@Component
@WebServlet(urlPatterns = "/ussd/*", loadOnStartup = 1)
@Controller
public class UssdReceiverSender extends MchoiceUssdReceiver {
   
   @Autowired
   AccountService accountService; 
   @Autowired
   PaymentService paymentService;
   @Value("${ussd.url}")
   private String ussd_host_url;
   @Value("${ussd.username}")
   private String ussd_username;
   @Value("${ussd.password}")
   private String ussd_password; 
   AppLogger appLogger=new AppLogger(this.getClass());
   
   private ConcurrentMap<String,Object> usersMap = new ConcurrentHashMap<>(); // contains the address and the info of the users concurrently- in a threade-safe way
    private static final String[] menus= {"Welcome To E-Transact App\n------\n1 Create An account\n2 Deposit\n3 Withdraw\n4 Check Balance\n\n10) Exit\n\nChoose an option",
         "Enter your FullName:\n\n\n9) Back\n10) Exit",
         "Enter your Address: \n\n\n9) Back\n10) Exit",
         "Enter your UserName:\n\n\n9) Back\n10) Exit",
         "Enter your Pin: \n\n\n9) Back\n10) Exit",
         "Enter your Amount: \n\n\n9) Back\n10) Exit"};
    @Override
    public void onMessage(MchoiceUssdMessage ussdMessage) {
        String userMessage =ussdMessage.getMessage();
        String msisdnAddress =ussdMessage.getAddress();
        String sessionId = ussdMessage.getConversationId();
        String correlationId = ussdMessage.getCorrelationId();
        appLogger.log("User Message: " + userMessage);
        appLogger.log("Address: " + msisdnAddress);
        appLogger.log("Conversation ID: " + sessionId);
        appLogger.log("Correlation ID: " + correlationId);         
        try {
            MchoiceUssdSender mchoiceUssdSender = new MchoiceUssdSender(ussd_host_url,ussd_username,ussd_password);
            
             Map<String, Object> currentUserMap= (Map<String, Object>)usersMap.get(msisdnAddress);
             appLogger.log("currentUserMap =="+currentUserMap);
             // Is this user here?
             if(usersMap.containsKey(msisdnAddress)){
                 currentUserMap.forEach((k,v)->appLogger.log(k+" "+v));
                 /**
                 
                 */
                 switch(userMessage){
                     case "1" :
                     {
                         //Serve Account Creation  Menu
                         mchoiceUssdSender.sendMessage(menus[1], msisdnAddress, sessionId, true); 
                         currentUserMap.put("selectionNumber", 1);
                         currentUserMap.put("levelNumber", 1);
                         break;
                     }
                     case "2" :
                     {
                         //Serve Deposit  Menu
                         mchoiceUssdSender.sendMessage(menus[5], msisdnAddress, sessionId, true); 
                         currentUserMap.put("selectionNumber", 2);
                         currentUserMap.put("levelNumber", 1);
                         break;
                     }
                     case "3" :
                     {
                         //Serve Withdrawal  Menu
                         mchoiceUssdSender.sendMessage(menus[5], msisdnAddress, sessionId, true); 
                         currentUserMap.put("selectionNumber", 3);
                         currentUserMap.put("levelNumber", 1);
                         break;
                     }
                     case "4" :
                     {
                         //Serve Balance  Menu
                         mchoiceUssdSender.sendMessage(menus[4], msisdnAddress, sessionId, true); 
                         currentUserMap.put("selectionNumber", 4);
                         currentUserMap.put("levelNumber", 1);
                         break;
                     }
                     case "9" :
                     {
                         // Go back to the main menu
                         mchoiceUssdSender.sendMessage(menus[0], msisdnAddress, sessionId, true);
                         break;
                     }
                     case "10" :
                     {
                         // Remove the user and exit
                         currentUserMap.remove(msisdnAddress);
                         mchoiceUssdSender.sendMessage("Thank you for using E-transact Payment App", msisdnAddress, sessionId, true);
                         break;
                     }
                     default:
                     {
                         if(currentUserMap.get("selectionNumber")==null){
                             appLogger.log("Please select something");
                            // re-Send the menu
                            mchoiceUssdSender.sendMessage(menus[0], msisdnAddress, sessionId, true);
                         } 
                         else {
                             // Process other sub menus and levels
                             int selectionNumber=(Integer)currentUserMap.get("selectionNumber");//selectionNumber
                             int levelNumber = (Integer) currentUserMap.get("levelNumber");
                             appLogger.log("selectionNumber: " + selectionNumber);
                             appLogger.log("levelNumber: " + levelNumber);
                             
                             if(userMessage!=null && userMessage.length()>=2){ // did the user enter atleast 2 characters ?
                                 //Process Account Creation
                                 if(selectionNumber==1){
                                     this.processAccountCreation(userMessage,levelNumber, currentUserMap, mchoiceUssdSender, msisdnAddress, sessionId);
                                 }
                                 
                                 //Process Deposit 
                                 else if(selectionNumber==2){
                                     this.processAccountDeposit(userMessage, levelNumber, currentUserMap, mchoiceUssdSender, msisdnAddress, sessionId);
                                 }
                                 //Process Withdrawal
                                 else if(selectionNumber==3){
                                     this.processAccountWithdrawal(userMessage, levelNumber, currentUserMap, mchoiceUssdSender, msisdnAddress, sessionId);   
                                 }
                                 //Process Balance
                                 else if(selectionNumber==4){
                                     this.processAccountBalance(userMessage, levelNumber, currentUserMap, mchoiceUssdSender, msisdnAddress, sessionId); 
                                 }
                                 
                             }
                             
                     }
                         
                     }
                     
                         
                 }
             }
             else{
                 // a new user
                 appLogger.log("A first time user. Please select something");
                 usersMap.put(msisdnAddress, new HashMap(){{
                    usersMap.put("selectionNumber", 0);
                    usersMap.put("levelNumber", 0);
                    usersMap.put("FullName", "");
                    usersMap.put("UserName", "");
                    usersMap.put("Address", "");
                    usersMap.put("Amount", "");
                    usersMap.put("Pin", "");
                    usersMap.put("PhoneNumber", msisdnAddress);
                 }});
                 //Send the menu to new user for the first time
                 mchoiceUssdSender.sendMessage(menus[0], msisdnAddress, sessionId, true);
                
             }
        } catch (MchoiceUssdException ex) {
            Logger.getLogger(UssdReceiverSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onSessionTerminate(MchoiceUssdTerminateMessage ussdTerminate) {
        usersMap.remove(ussdTerminate.getAddress());
    }
    private void processAccountCreation(String userMessage,int levelNumber,Map<String, Object> currentUserMap,MchoiceUssdSender mchoiceUssdSender,String msisdnAddress, String sessionId) throws MchoiceUssdException{
        if(levelNumber ==1){
                                     
            currentUserMap.put("FullName", userMessage); // temporary store the user's data
            mchoiceUssdSender.sendMessage(menus[2], msisdnAddress, sessionId, true);
            currentUserMap.put("levelNumber", 2);
        }
        else if(levelNumber ==2){
            currentUserMap.put("Address", userMessage); // temporary store the user's data
            mchoiceUssdSender.sendMessage(menus[3], msisdnAddress, sessionId, true);
            currentUserMap.put("levelNumber", 3);
        }
        else if(levelNumber ==3){
            currentUserMap.put("UserName", userMessage); // temporary store the user's data
            mchoiceUssdSender.sendMessage(menus[4], msisdnAddress, sessionId, true);
            currentUserMap.put("levelNumber", 4);
        }
        else if(levelNumber ==4){
            currentUserMap.put("Pin", userMessage); // temporary store the user's data
            currentUserMap.put("PhoneNumber", msisdnAddress);// add the user's msisdn.
            Account userAccount = accountService.createAccount(currentUserMap);
            if(userAccount!=null)
                mchoiceUssdSender.sendMessage("Congratulations! "+currentUserMap.get("FullName")+" You have successfully created your account\n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            else
                mchoiceUssdSender.sendMessage("OOps! "+currentUserMap.get("FullName")+" Error creating your account\n Try again later\n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            appLogger.log("Completed Account Info::");
            currentUserMap.forEach((k,v)->appLogger.log(k+" "+v));
             // not the way it should be for now---testing
             new SmsSender().sendSMS("+2348064500095", "Thank you for using e-transact app");
        }

    }
    
    private void processAccountDeposit(String userMessage,int levelNumber,Map<String, Object> currentUserMap,MchoiceUssdSender mchoiceUssdSender,String msisdnAddress, String sessionId) throws MchoiceUssdException{
        if(levelNumber ==1){
            currentUserMap.put("Amount", userMessage); // temporary store the user's data
            //paymentService.initiatePayment(new PaymentRequest());// Disable Paystack Payment Api for now
            if(accountService.depositUserFund(msisdnAddress, Double.parseDouble(currentUserMap.get("Amount").toString())))
                mchoiceUssdSender.sendMessage("Congratulations! "+" You have successfully deposited your cash\n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            else
                mchoiceUssdSender.sendMessage("OOps!  Error occured depositing your cash\n Try again later\n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            appLogger.log("Completed Account Info::");
            currentUserMap.forEach((k,v)->appLogger.log(k+" "+v));
            currentUserMap.put("levelNumber", 2); 
            // not the way it should be for now---testing
            new SmsSender().sendSMS("+2348064500095", "Thank you for using e-transact app");
        }
    }
    
    private void processAccountWithdrawal(String userMessage,int levelNumber,Map<String, Object> currentUserMap,MchoiceUssdSender mchoiceUssdSender,String msisdnAddress, String sessionId) throws MchoiceUssdException{
        if(levelNumber ==1){
            currentUserMap.put("Amount", userMessage); // temporary store the user's data
            mchoiceUssdSender.sendMessage(menus[4], msisdnAddress, sessionId, true);
            currentUserMap.put("levelNumber", 2);
        }
        else if(levelNumber ==2){
            currentUserMap.put("Pin", userMessage); // temporary store the user's data
            currentUserMap.put("levelNumber", 3);
            Account userAccount = accountService.getUserAccount(msisdnAddress);
            Map withdrawDetails = accountService.withdraw(userAccount, currentUserMap.get("Pin").toString(), Double.parseDouble(currentUserMap.get("Amount").toString()));
            if(withdrawDetails!=null && withdrawDetails.get("status").toString().equals("success"))
                mchoiceUssdSender.sendMessage("Please take your withdrawn cash here: N"+withdrawDetails.get("withdrawn_amount").toString()+" \n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            else
                mchoiceUssdSender.sendMessage("OOps!  Error occured withdrawing your cash\n\n Error Message:"+withdrawDetails.get("status").toString()+" \n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            appLogger.log("Completed Account Info::");
            currentUserMap.forEach((k,v)->appLogger.log(k+" "+v));

        }
    }
    
    private void processAccountBalance(String userMessage,int levelNumber,Map<String, Object> currentUserMap,MchoiceUssdSender mchoiceUssdSender,String msisdnAddress, String sessionId) throws MchoiceUssdException{
        if(levelNumber ==1){
            currentUserMap.put("Pin", userMessage); // temporary store the user's data
            currentUserMap.put("levelNumber", 2);
            Account userAccount = accountService.getUserAccount(msisdnAddress);
            Number balance = accountService.getUserAccountBalance(userAccount, currentUserMap.get("Pin").toString());
            if(balance!=null && balance!= new Integer(-1))
                mchoiceUssdSender.sendMessage("Dear "+userAccount.getFullName()+"\n Your Account Balance is::N "+balance+" \n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            else
                mchoiceUssdSender.sendMessage("OOps! Ensure your pin is valid  \n\n\n9) Back\n10) Exit", msisdnAddress, sessionId, true);
            appLogger.log("Completed Account Info::");
            currentUserMap.forEach((k,v)->appLogger.log(k+" "+v));

        }
    }
    
}
