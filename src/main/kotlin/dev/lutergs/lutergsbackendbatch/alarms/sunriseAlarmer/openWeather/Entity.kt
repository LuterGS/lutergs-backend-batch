package dev.lutergs.lutergsbackendbatch.alarms.sunriseAlarmer.openWeather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import dev.lutergs.lutergsbackendbatch.util.TimeZoneDeserializer
import dev.lutergs.lutergsbackendbatch.util.ZoneOffsetDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


data class OpenWeatherResponse(
    @JsonProperty("lat") val latitude: Float,
    @JsonProperty("longitude") val longitude: Float,
    @JsonProperty("timezone")
    @JsonDeserialize(using = TimeZoneDeserializer::class)
    val timezone: TimeZone,
    @JsonProperty("timezone_offset")
    @JsonDeserialize(using = ZoneOffsetDeserializer::class)
    val offset: ZoneOffset,
    @JsonProperty("current") val current: CurrentForecast
) {
    fun getSunset(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.current.sunset), this.timezone.toZoneId())
    fun getSunrise(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.current.sunrise), this.timezone.toZoneId())
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentForecast (
    @JsonProperty("dt")          val timestamp: Long,
    @JsonProperty("sunrise")     val sunrise: Long,
    @JsonProperty("sunset")      val sunset: Long,
    @JsonProperty("temp")        val temperature: Float,
    @JsonProperty("feels_like")  val feelsLike: Float,
    @JsonProperty("pressure")    val pressure: Float,
    @JsonProperty("humidity")    val humidity: Float,
    @JsonProperty("dewPoint")    val dewPoint: Float,
    @JsonProperty("uvi")         val uvi: Float,
    @JsonProperty("clouds")      val clouds: Float,
    @JsonProperty("visibility")  val visibility: Float,
    @JsonProperty("windSpeed")   val windSpeed: Float,
    @JsonProperty("windDeg")     val windDeg: Float,
    @JsonProperty("weather")     val weather: List<Weather>
)

data class Weather (
    @JsonProperty("id")          val id: Int,
    @JsonProperty("main")        val status: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("icon")        val icon: String
) {
    fun getIconUrl(): String {
        return "https://openweathermap.org/img/wn/${this.icon}@2x.png"
    }
}

class Entity {
}

//fun t2 () {
//    Timestamp.value
//    Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(9)))
//        .let { }
//}
//val t = LocalDateTime.now().stamp