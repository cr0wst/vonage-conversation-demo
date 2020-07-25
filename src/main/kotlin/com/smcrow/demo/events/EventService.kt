package com.smcrow.demo.events

import com.nexmo.client.NexmoClient
import com.smcrow.demo.conversation.Conversation
import com.smcrow.demo.membership.Member
import com.thedeanda.lorem.LoremIpsum
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class EventService(
    @Qualifier("conversationWebClient") private val webClient: WebClient,
    private val nexmoClient: NexmoClient
) {
    fun sendMessage(conversation: Conversation, member: Member) = webClient
        .post()
        .uri("/${conversation.id}/events")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
        .bodyValue(EventPostBody(from = member.id))
        .retrieve()
        .bodyToMono(String::class.java)
        .block()!!

    data class EventPostBody(val type: String = "text", val from: String, val body: Body = Body()) {
        data class Body(val text: String = LoremIpsum.getInstance().getParagraphs(0, 3))
    }
}
