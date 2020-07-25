package com.smcrow.demo

import com.nexmo.client.NexmoBadRequestException
import com.nexmo.client.application.ApplicationClient
import com.nexmo.starter.NexmoCredentialsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient
import javax.annotation.PostConstruct

private const val APP_NOT_CONFIGURED_ERROR =
    "Configured Nexmo Application does not exist. Please go to https://dashboard.nexmo.com/applications/ and set one up."

@SpringBootApplication
@EnableScheduling
class NexmoConversationDemoApplication(
    private val applicationClient: ApplicationClient,
    private val nexmoCredentialsProperties: NexmoCredentialsProperties
) {
    @Bean
    fun conversationWebClient() = WebClient.create("https://api.nexmo.com/beta/conversations")

    @Bean
    fun userWebClient() = WebClient.create("https://api.nexmo.com/beta/users")

    @PostConstruct
    fun checkForValidRunningState() {
        try {
            applicationClient.getApplication(nexmoCredentialsProperties.applicationId)
        } catch (nbre: NexmoBadRequestException) {
            throw RuntimeException(APP_NOT_CONFIGURED_ERROR)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<NexmoConversationDemoApplication>(*args)
}
