package com.ghrer.commerce.events.inventory

import com.ghrer.commerce.events.SnsEventPublisher
import io.awspring.cloud.sns.core.SnsTemplate

class SnsInventoryEventPublisher(
    inventorySns: String,
    snsTemplate: SnsTemplate,
) : InventoryEventPublisher, SnsEventPublisher(
    snsTemplate,
    inventorySns
)
