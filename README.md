# Vonage Conversation API Demo

This application demonstrates how the [Vonage Conversation API](https://developer.nexmo.com/conversation/overview) works.

The `AutomaticConversation` class has two components to it.

## Automatically Create a Conversation and Users

The `setup` method creates a conversation and 5 users. It then creates a member relationship between the users and the conversation:

```kt
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
```

While the application is running, there is a scheduled task that sends a text event from a random user into the conversation. This occurs every 5 seconds:

```kt
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
```

When the application is shut down, the `tearDown` method deletes the created conversation and users:

```kt
@PreDestroy
fun tearDown() {
    log.info("Deleting all Conversations")
    conversationService.deleteConversation(conversation!!)
    log.info("Deleting all users")
    userService.deleteUsers(users)
}
```

## Run the Application

Todo