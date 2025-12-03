package com.estate.lectureproject.entity;

public enum OrderStatus {
    UNPAID,             // 未支付
    PAID,               // 已支付
    CONFIRMED,          // 已确认 (入住)
    REFUND_REQUESTED,   // 申请退款
    REFUND_COMPLETED,    // 退款成功
    CANCELLED
}