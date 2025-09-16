# TransferCalculate.calculate() 方法流程图

## 概述
本文档详细描述了 [`TransferCalculate.calculate()`](demo-junit/src/main/java/com/remember5/junit/card/TransferCalculate.java:27) 方法的完整逻辑流程，包括所有决策点、条件分支、循环、异常处理、输入验证、处理步骤和返回路径。

## 业务背景
该系统是一个卡片划拨计算工具，支持三种卡类型：
- **次卡 (COUNT)**: 按使用次数计算划拨
- **时长卡 (TIME)**: 按使用天数计算划拨  
- **金额卡 (AMOUNT)**: 按消费金额计算划拨

## 核心概念
- **留底资金**: 监管要求的资金保证金
- **可支用资金**: 可以直接使用的资金
- **划拨**: 从留底资金转移到可支用资金的过程
- **累计划拨**: 历史总划拨金额
- **预计划拨**: 本次计划划拨的金额

## 完整流程图

```mermaid
flowchart TD
    Start([开始: calculate方法]) --> Input["输入参数:<br/>BaseCard card<br/>Integer expenseCount"]
    
    Input --> Validate["调用 validateCard<br/>验证输入参数"]
    
    Validate --> ValidateCard{"validateCard 验证"}
    ValidateCard -->|卡片为null| E1["抛出 IllegalArgumentException:<br/>卡片对象不能为null"]
    ValidateCard -->|卡片属性为null| E2["抛出 IllegalArgumentException:<br/>卡片属性不能为null"]
    ValidateCard -->|留底资金≤0| E3["抛出 IllegalStateException:<br/>留底资金必须大于0"]
    ValidateCard -->|非金额卡且核销次数大于剩余次数| E4["抛出 IllegalStateException:<br/>本次核销次数不可以大于剩余权益数"]
    ValidateCard -->|非金额卡且剩余次数等于0| E5["抛出 IllegalStateException:<br/>核销次数已达到上限"]
    ValidateCard -->|验证通过| GetPlan["调用 getPlanTransferAmount<br/>获取预计划拨金额"]
    
    GetPlan --> PlanCalc{"getPlanTransferAmount 计算"}
    PlanCalc -->|金额卡| AmountCardPlan{"订单金额 == 权益金额?"}
    AmountCardPlan -->|是| AmountPlan1["预计划拨金额 = 本次消费金额"]
    AmountCardPlan -->|否| AmountPlan2["预计划拨金额 = 本次消费金额 × 划拨比例<br/>保留2位小数，向下舍入"]
    AmountPlan1 --> CheckZero
    AmountPlan2 --> CheckZero
    
    PlanCalc -->|次卡或时长卡| CountPlan["预计划拨金额 = 单次划拨金额 × 核销次数"]
    CountPlan --> CheckZero
    
    CheckZero{"预计划拨金额 == 0?"}
    CheckZero -->|是| E6["抛出 IllegalStateException:<br/>预计划拨金额不得为0"]
    CheckZero -->|否| CalcNew["计算新的累计划拨金额:<br/>newCumulativeAmount =<br/>累计划拨金额 + 预计划拨金额"]
    
    CalcNew --> CheckCardType{"判断卡类型"}
    
    CheckCardType -->|金额卡| AmountCardLogic["金额卡逻辑处理"]
    CheckCardType -->|次卡或时长卡| CountCardLogic["次卡/时长卡逻辑处理"]
    
    AmountCardLogic --> AmountCheck{"订单金额 == 权益金额?"}
    AmountCheck -->|是| AmountSpecialCheck{"预计划拨金额 > 当前留底?"}
    AmountSpecialCheck -->|是| E7["抛出 IllegalArgumentException:<br/>预计划拨金额大于留底资金"]
    AmountSpecialCheck -->|否| AmountLastCheck
    AmountCheck -->|否| AmountLastCheck
    
    AmountLastCheck["计算新的累计权益金额:<br/>newCumulativeUsedEquityAmount =<br/>累计权益金额 + 本次消费金额"]
    AmountLastCheck --> AmountLastDecision{"判断是否最后一次划拨"}
    AmountLastDecision -->|新累计权益金额大于等于权益金额或新累计划拨金额大于等于到账金额| SetAmountLast["isLastTransfer = true"]
    AmountLastDecision -->|否| SetAmountNotLast["isLastTransfer = false"]
    
    CountCardLogic --> CountRemaining["计算剩余核销次数:<br/>remainingCount = 剩余次数 - 消费次数"]
    CountRemaining --> CountLastCheck{"remainingCount ≤ 0?"}
    CountLastCheck -->|是| SetCountLast["isLastTransfer = true<br/>更新剩余核销次数"]
    CountLastCheck -->|否| SetCountNotLast["isLastTransfer = false<br/>更新剩余核销次数"]
    
    SetAmountLast --> GetActual
    SetAmountNotLast --> GetActual
    SetCountLast --> GetActual
    SetCountNotLast --> GetActual
    
    GetActual["调用 getActualTransferAmount<br/>获取实际划拨金额"]
    
    GetActual --> ActualCheck{"getActualTransferAmount 判断"}
    ActualCheck -->|isLastTransfer等于true| LastTransfer["执行最后一次划拨逻辑"]
    ActualCheck -->|新累计划拨小于等于卡可支用资金| NotTransfer["记账阶段，不划拨"]
    ActualCheck -->|新累计划拨大于卡可支用资金| RealTransfer["留底阶段，需要划拨"]
    
    LastTransfer --> LastCalc["计算预计划拨金额:<br/>planAmount = 到账金额 - 累计划拨金额"]
    LastCalc --> LastCompare{"预计划拨 ≥ 卡初始留底?"}
    LastCompare -->|是| LastFull["实际划拨 = 卡初始留底资金<br/>更新累计划拨 = 到账金额 - 累计划拨<br/>更新累计权益金额卡 = 权益金额 - 累计权益"]
    LastCompare -->|否| LastPartial["实际划拨 = 预计划拨金额<br/>更新累计划拨 = 预计划拨金额<br/>更新累计权益金额卡 = 本次消费金额"]
    LastFull --> LastFinish["设置当前留底资金 = 0"]
    LastPartial --> LastFinish
    LastFinish --> ReturnLast["返回实际划拨金额"]
    
    NotTransfer --> NotUpdate["更新累计划拨金额<br/>更新累计权益金额金额卡"]
    NotUpdate --> ReturnZero["返回 BigDecimal.ZERO"]
    
    RealTransfer --> RealCalc{"是否已触发留底转账?"}
    RealCalc -->|是| RealAmount1["实际划拨金额 = 预计划拨金额"]
    RealCalc -->|否| RealAmount2["实际划拨金额 = 新累计划拨 - 卡可支用资金"]
    RealAmount1 --> RealTrigger
    RealAmount2 --> RealTrigger["设置触发留底转账 = true"]
    
    RealTrigger --> RealCheck{"实际划拨 ≥ 当前留底?"}
    RealCheck -->|是| RealMax["实际划拨 = 卡初始留底资金<br/>设置触发留底转账 = true"]
    RealCheck -->|否| RealNormal["保持计算的实际划拨金额"]
    
    RealMax --> RealUpdate
    RealNormal --> RealUpdate["更新当前留底 = 当前留底 - 实际划拨<br/>更新累计划拨金额<br/>更新累计权益金额金额卡"]
    RealUpdate --> ReturnReal["返回实际划拨金额"]
    
    ReturnLast --> End([结束])
    ReturnZero --> End
    ReturnReal --> End
    
    E1 --> End
    E2 --> End
    E3 --> End
    E4 --> End
    E5 --> End
    E6 --> End
    E7 --> End
    
    style Start fill:#e1f5fe
    style End fill:#e8f5e8
    style E1 fill:#ffebee
    style E2 fill:#ffebee
    style E3 fill:#ffebee
    style E4 fill:#ffebee
    style E5 fill:#ffebee
    style E6 fill:#ffebee
    style E7 fill:#ffebee
    style ValidateCard fill:#fff3e0
    style PlanCalc fill:#fff3e0
    style CheckCardType fill:#f3e5f5
    style ActualCheck fill:#e8f5e8
```

## 关键决策点说明

### 1. 输入验证 ([`validateCard`](demo-junit/src/main/java/com/remember5/junit/card/TransferCalculate.java:68))
- 检查卡片对象和关键属性是否为null
- 验证留底资金必须大于0
- 对于非金额卡，检查核销次数限制

### 2. 预计划拨金额计算 ([`getPlanTransferAmount`](demo-junit/src/main/java/com/remember5/junit/card/TransferCalculate.java:195))
- **金额卡**: 根据订单金额与权益金额关系决定计算方式
  - 相等时：预计划拨 = 本次消费金额
  - 不等时：预计划拨 = 本次消费金额 × 划拨比例
- **次卡/时长卡**: 预计划拨 = 单次划拨金额 × 核销次数

### 3. 最后一次划拨判断
- **金额卡**: 累计权益金额达到权益金额 OR 累计划拨达到到账金额
- **次卡/时长卡**: 剩余核销次数 ≤ 0

### 4. 实际划拨金额计算 ([`getActualTransferAmount`](demo-junit/src/main/java/com/remember5/junit/card/TransferCalculate.java:99))
- **最后一次划拨**: 划拨剩余所有资金
- **记账阶段**: 可支用资金充足，不需要划拨
- **留底阶段**: 可支用资金不足，需要动用留底资金

## 测试用例覆盖的关键场景

### 次卡测试场景
1. **正常核销**: 单次和批量核销
2. **边界测试**: 核销次数为0、超过剩余次数
3. **最后一次核销**: 剩余次数刚好用完

### 金额卡测试场景  
1. **订单金额等于权益金额**: 特殊验证逻辑
2. **订单金额不等于权益金额**: 按比例计算
3. **权益金额耗尽**: 累计权益达到上限
4. **大额消费**: 单次消费超过权益金额

### 异常场景
1. **参数验证**: null值、负数、零值
2. **业务规则**: 核销次数超限、留底资金不足
3. **边界条件**: 最后一次核销的特殊处理

## 流程图符号说明
- **椭圆**: 开始/结束节点
- **矩形**: 处理步骤
- **菱形**: 决策点/条件判断
- **红色**: 异常处理
- **蓝色**: 开始节点
- **绿色**: 结束节点
- **橙色**: 关键验证点
- **紫色**: 卡类型判断

## 方法调用关系
```
calculate()
├── validateCard()
├── getPlanTransferAmount()
└── getActualTransferAmount()
    ├── doLastTransfer()
    ├── notTransfer()
    │   ├── updateCumulativeAmount()
    │   └── updateCumulativeUsedEquityAmount()
    └── realTransfer()
        ├── updateCumulativeAmount()
        └── updateCumulativeUsedEquityAmount()
```

## 总结
该流程图完整展示了 [`TransferCalculate.calculate()`](demo-junit/src/main/java/com/remember5/junit/card/TransferCalculate.java:27) 方法的执行逻辑，包括：
- 完整的输入验证流程
- 三种卡类型的差异化处理逻辑
- 复杂的最后一次划拨判断
- 三种不同的划拨策略（最后一次、记账阶段、留底阶段）
- 全面的异常处理机制
- 测试用例验证的关键业务场景

该算法的核心是根据不同卡类型和当前状态，智能决定是否需要从留底资金中划拨资金到可支用账户，确保资金使用的合规性和准确性。
