package com.smcrow.demo.membership

import com.fasterxml.jackson.annotation.JsonProperty
import com.nexmo.client.NexmoClient
import com.smcrow.demo.conversation.Conversation
import com.smcrow.demo.user.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class MembershipService(
    @Qualifier("conversationWebClient") private val webClient: WebClient,
    private val nexmoClient: NexmoClient
) {
    fun createMembership(user: User, conversation: Conversation) = webClient
        .post()
        .uri("/${conversation.id}/members")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
        .bodyValue(MemberPostBody(userId = user.id))
        .retrieve()
        .bodyToMono(Member::class.java)
        .block()!!

    data class MemberPostBody(
        @JsonProperty("user_id") val userId: String,
        val action: String = "join",
        val channel: Channel = Channel()
    ) {
        data class Channel(val type: String = "app")
    }
}
