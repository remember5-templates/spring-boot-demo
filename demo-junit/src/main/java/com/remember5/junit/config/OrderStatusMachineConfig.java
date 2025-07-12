/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.junit.config;

import com.remember5.junit.entity.OrderStatusChangeEventEnum;
import com.remember5.junit.entity.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * 订单状态机
 *
 * @author wangjiahao
 * @date 2025/6/16 14:53
 */
@Configuration
@EnableStateMachine
public class OrderStatusMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusChangeEventEnum> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusChangeEventEnum> states) throws Exception {
        states.withStates()
                .initial(OrderStatusEnum.WAIT_PAYMENT)
                .end(OrderStatusEnum.FINISH)
                .states(EnumSet.allOf(OrderStatusEnum.class));
    }

    /**
     * 配置状态转换事件关系
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusChangeEventEnum> transitions) throws Exception {
        transitions.withExternal().source(OrderStatusEnum.WAIT_PAYMENT)
                .target(OrderStatusEnum.WAIT_DELIVER)
                .event(OrderStatusChangeEventEnum.PAYED)
            .and().withExternal().source(OrderStatusEnum.WAIT_DELIVER)
                .target(OrderStatusEnum.WAIT_RECEIVE)
                .event(OrderStatusChangeEventEnum.DELIVERY)
            .and().withExternal().source(OrderStatusEnum.WAIT_RECEIVE)
                .target(OrderStatusEnum.FINISH)
                .event(OrderStatusChangeEventEnum.RECEIVED);
    }
}
