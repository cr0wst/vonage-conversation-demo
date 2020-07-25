package com.smcrow.demo

import com.smcrow.demo.conversation.Conversation
import com.smcrow.demo.conversation.ConversationService
import com.smcrow.demo.events.EventService
import com.smcrow.demo.membership.Member
import com.smcrow.demo.membership.MembershipService
import com.smcrow.demo.user.User
import com.smcrow.demo.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

@Component
class AutomaticConversation(
    private val conversationService: ConversationService,
    private val userService: UserService,
    private val membershipService: MembershipService,
    private val eventService: EventService
) {
    private var conversation: Conversation? = null
    private val users: MutableList<User> = mutableListOf()
    private val memberships: MutableList<Member> = mutableListOf()

    @EventListener
    fun setup(contextRefreshedEvent: ContextRefreshedEvent) {
        log.info("Creating new Conversation")
        conversation = conversationService.createConversation()
        log.info("Created: $conversation")

        repeat(5) {
            log.info("Creating new User")
            val user = userService.createUser()
            users.add(user)
            log.info("Created: $user")

            log.info("Adding ${user.id} to ${conversation?.id}")
            val membership = membershipService.createMembership(user, conversation!!)
            memberships.add(membership)
        }
    }

    @Scheduled(fixedDelay = 5000)
    fun haveConversation() {
        try {
            if (conversation != null && memberships.isNotEmpty()) {
                val member = memberships.random()
                eventService.sendMessage(conversation!!, member)
                log.info("Sent message from ${member.id} to ${conversation!!.id}")
            }
        } catch (e: Exception) {
            // Ignore any exceptions, sometimes a race condition happens or we send too fast for Nexmo.
        }
    }

    @PreDestroy
    fun tearDown() {
        log.info("Deleting all Conversations")
        conversationService.deleteConversation(conversation!!)
        log.info("Deleting all users")
        userService.deleteUsers(users)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
