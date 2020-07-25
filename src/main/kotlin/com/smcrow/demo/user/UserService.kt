package com.smcrow.demo.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.nexmo.client.NexmoClient
import com.thedeanda.lorem.LoremIpsum
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Service
class UserService(
    @Qualifier("userWebClient") private val webClient: WebClient,
    private val nexmoClient: NexmoClient
) {
    fun createUser() = webClient
        .post()
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
        .bodyValue(createNewUserBody())
        .retrieve()
        .bodyToMono(User::class.java)
        .block()!!

    fun deleteUsers(users: List<User>) {
        users.forEach {
            webClient
                .delete()
                .uri("/${it.id}")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${nexmoClient.generateJwt()}")
                .retrieve()
                .toBodilessEntity()
                .block()
        }
    }

    private fun createNewUserBody() =
        UserRequestBody(
            name = UUID.randomUUID().toString(),
            displayName = LoremIpsum.getInstance().name
        )

    data class UserRequestBody(val name: String, @JsonProperty("display_name") val displayName: String)
}
