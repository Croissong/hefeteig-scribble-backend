package io.hefeteig.hefeteigscribblebackend

import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.annotations.GraphQLSubscription
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.leangen.graphql.spqr.spring.util.ConcurrentMultiMap
import org.reactivestreams.Publisher
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

import java.time.Instant

@Service
@GraphQLApi
@EnableScheduling
class ChatService {
    val subscribers: ConcurrentMultiMap<String, FluxSink<Message>> = ConcurrentMultiMap()

    @Scheduled(fixedRate = 5000)
    fun testMessage() {
        var time = Instant.now().toEpochMilli()
        subscribers.get("test").forEach { subscriber -> subscriber.next(Message("TestUser", "TestMessage $time, subscribers: ${subscribers.get("test").size}", time))}
    }

    @GraphQLMutation
    fun sendMessage(@GraphQLArgument(name = "username", defaultValue = "") username: String, @GraphQLArgument(name = "message", defaultValue = "") message: String): Message {
        val m = Message(username, message, Instant.now().toEpochMilli())
        this.subscribers.get(username).forEach {subscriber -> subscriber.next(m)}
        return m
    }

    @GraphQLSubscription
    fun messagesSubscription(username: String): Publisher<Message> {
        return Flux.create({ subscriber -> this.subscribers.add(username, subscriber.onDispose { this.subscribers.remove(username, subscriber) }) }, FluxSink.OverflowStrategy.LATEST)
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 2
        scheduler.setThreadNamePrefix("scheduled-task-")
        scheduler.isDaemon = true
        return scheduler
    }
}