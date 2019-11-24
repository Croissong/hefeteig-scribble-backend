package io.hefeteig.hefeteigscribblebackend

import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.annotations.GraphQLSubscription
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Instant

@Component
@GraphQLApi
class ChatService {
    var subscribers: MutableMap<String, FluxSink<Any>> = mutableMapOf()
    var messages: MutableList<Message> = mutableListOf(Message("InitialUser1", "InitialText1", Instant.now().toEpochMilli()), Message("InitialUser1", "InitialText2", Instant.now().toEpochMilli()+5000))

    @GraphQLQuery
    fun messages() : List<Message>  = this.messages

    @GraphQLMutation
    fun sendMessage(@GraphQLArgument(name = "username", defaultValue = "") username: String, @GraphQLArgument(name = "message", defaultValue = "") message: String): Message {
        val m = Message(username, message, Instant.now().toEpochMilli())
        println(m)
        this.messages.add(m)
        return m
    }

    @GraphQLSubscription
    fun sub(username: String): Flux<Any> {
        println("sub : $username");
        return Flux.create<Any>({ subscriber -> this.subscribers[username] = subscriber.onDispose { this.subscribers.remove(username, subscriber) } }, FluxSink.OverflowStrategy.LATEST)
    }
}