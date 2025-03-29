package com.ghrer.commerce.events.orders

import com.ghrer.commerce.events.SnsEventPublisher
import io.awspring.cloud.sns.core.SnsTemplate

class SnsOrdersEventPublisher(
    ordersSns: String,
    snsTemplate: SnsTemplate,
) : OrdersEventPublisher, SnsEventPublisher(
    snsTemplate,
    ordersSns
)
