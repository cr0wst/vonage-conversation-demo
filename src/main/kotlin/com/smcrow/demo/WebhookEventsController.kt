package com.smcrow.demo

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WebhookEventsController {
    @PostMapping("/webhooks/events")
    fun incomingEvents(@RequestBody body: String) {
        logger.info(body)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
