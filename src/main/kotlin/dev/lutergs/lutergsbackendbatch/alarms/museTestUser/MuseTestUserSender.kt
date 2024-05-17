package dev.lutergs.lutergsbackendbatch.alarms.museTestUser

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class MuseTestUserSender(
  scheduledAlarmRequester: ScheduledAlarmRequester,
  @Value("\${custom.batch.muse-test-user.id}") private val id: String,
  @Value("\${custom.batch.muse-test-user.url}") private val url: String
): Scheduler(scheduledAlarmRequester) {
  private val musicList = listOf(
    "1613139922",
    "1621089608",
    "1519364407",
    "1494673344",
    "1682502298"
  )
  private val requester: WebClient = WebClient.builder()
    .baseUrl(this.url)
    .defaultHeader("Content-Type", "application/json")
    .build()
  private val objectMapper: ObjectMapper = ObjectMapper()

  private fun getAccessToken(): String? {
    return this.requester.post()
      .uri { it.path("/user/token").build() }
      .bodyValue("{\"type\":\"APPLE\",\"uid\":\"${this.id}\"")
      .retrieve()
      .bodyToMono(String::class.java)
      .flatMap {
        val jsonNode = this.objectMapper.readTree(it)

        Mono.just(jsonNode.get("token").get("ACCESS_TOKEN").asText())
      }.block()
  }

  @Scheduled(fixedDelay = 1000 * 60L)
  override fun batch() {
    // 60 에서, 랜덤하게 4번의 요청을 보냄, 각각 음악 재생 - 정지
    // 음악 리스트는 다음과 같음
    val musics = musicList.shuffled().take(2)
    val reqSecond = (5..55).shuffled().take(4).sorted()

    val accessToken = this.getAccessToken() ?: "ERROR!"

    val reqSet = listOf(
      Triple(musics[0], reqSecond[0], "PLAYING"),
      Triple(musics[0], reqSecond[1], "PAUSED"),
      Triple(musics[1], reqSecond[2], "PLAYING"),
      Triple(musics[1], reqSecond[3], "PAUSED")
    )

    CoroutineScope(Dispatchers.IO).launch {
      reqSet.forEach { req ->
        delay(req.second * 1000L)
        requester.post()
          .uri { it.path("/track").build() }
          .header("Authorization", "Bearer $accessToken")
          .bodyValue("{" +
                "\"track\": {" +
                  "\"vendor\": \"Apple\"," +
                  "\"uid\": \"${req.first}\"" +
                "}," +
                "\"playbackStatus\": \"${req.third}\"" +
              "}")
          .retrieve()
          .toBodilessEntity()
          .subscribe()
      }
    }
  }

  override fun produceMessage(): Mono<TriggerTopicRequest> {
    TODO("Not yet implemented")
  }
}