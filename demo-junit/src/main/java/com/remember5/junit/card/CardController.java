package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;
import com.remember5.junit.card.response.AmountCardExpenseResponse;
import com.remember5.junit.card.response.CountCardExpenseResponse;
import com.remember5.junit.card.response.ExpenseResponse;
import com.remember5.junit.card.response.TimeCardExpenseResponse;
import com.remember5.junit.card.vo.AmountCardVO;
import com.remember5.junit.card.vo.CountCardVO;
import com.remember5.junit.card.vo.TimeCardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author wangjiahao
 * @date 2025/9/15 10:05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/card")
public class CardController {


    @PostMapping("/countCardExpense")
    public CountCardExpenseResponse countCardExpense(@RequestBody CountCardVO countCardVO) {
        final CountCardExpenseResponse response = new CountCardExpenseResponse();
        // 创建次卡
        CountCard card = new CountCard(countCardVO.getOrderAmount(), countCardVO.getArrivalAmount(), countCardVO.getReservePrecent(), countCardVO.getTotalCount());
        card.printCardInfo();
        response.setCard(card);

        // 进行多次计算
        for (int i = 1; i <= card.getTotalCount(); i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            final ExpenseResponse expenseResponse = new ExpenseResponse(i, card.getEachAmount(), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
            response.getExpense().add(expenseResponse);
        }
        return response;
    }

    @PostMapping("/timeCardExpense")
    public TimeCardExpenseResponse timeCardExpense(@RequestBody TimeCardVO timeCardVO) {
        final TimeCardExpenseResponse response = new TimeCardExpenseResponse();
        // 创建次卡
        TimeCard card = new TimeCard(timeCardVO.getOrderAmount(), timeCardVO.getArrivalAmount(), timeCardVO.getReservePrecent(), timeCardVO.getTotalCount(),  timeCardVO.getStartTime());
        card.printCardInfo();
        response.setCard(card);

        // 进行多次计算
        for (int i = 1; i <= card.getTotalCount(); i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            final ExpenseResponse expenseResponse = new ExpenseResponse(i, card.getEachAmount(), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
            response.getExpense().add(expenseResponse);
        }
        return response;
    }

    @PostMapping("/amountCardExpense")
    public AmountCardExpenseResponse timeCardExpense(@RequestBody AmountCardVO amountCardVO) {
        final AmountCardExpenseResponse response = new AmountCardExpenseResponse();
        // 创建次卡
        AmountCard card = new AmountCard(amountCardVO.getOrderAmount(), amountCardVO.getArrivalAmount(), amountCardVO.getReservePrecent());
        card.printCardInfo();
        response.setCard(card);
        // 进行多次计算
        for (int i = 1; i <= amountCardVO.getExpenseAmount().size(); i++) {
            final BigDecimal currentExpenseAmount = amountCardVO.getExpenseAmount().get(i - 1);
            card.setCurrentExpenseAmount(currentExpenseAmount);
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            final ExpenseResponse expenseResponse = new ExpenseResponse(i, currentExpenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
            response.getExpense().add(expenseResponse);
        }

        return response;
    }

}

