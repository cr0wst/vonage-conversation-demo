package com.smcrow.demo.conversation

import com.fasterxml.jackson.annotation.JsonProperty
import com.nexmo.client.NexmoClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Service
class ConversationService(
    @Qualifier("conversationWebClient") private val webClient: WebClient,
    private val nexmoClient: NexmoClient
) {
    fun createConversation() = webClient
        .post()
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
        .bodyValue(createNewConversationBody())
        .retrieve()
        .bodyToMono(Conversation::class.java)
        .block()!!

    fun deleteConversation(conversation: Conversation) {
        webClient
            .delete()
            .uri("/${conversation.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
            .retrieve()
            .toBodilessEntity()
            .block()
    }

    private fun createNewConversationBody() =
        ConversationRequestBody(
            name = UUID.randomUUID().toString(), displayName = UUID.randomUUID().toString()
        )

    data class ConversationRequestBody(val name: String, @JsonProperty("display_name") val displayName: String)
}
