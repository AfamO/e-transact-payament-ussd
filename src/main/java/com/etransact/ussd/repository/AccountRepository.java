/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.etransact.ussd.repository;

import com.etransact.ussd.model.Account;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author HP
 */
public interface AccountRepository extends PagingAndSortingRepository<Account,Long> {
    
    Account findByPhoneNumber(String phoneNumber);
    List<Account> findByBalanceAmount(Double amount);
    
}
