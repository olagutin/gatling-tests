package likesapp

import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import ru.tinkoff.gatling.kafka.Predef._

import scala.concurrent.duration.DurationInt

class KafkaLoadTesting_1k extends Simulation {
  val kafkaConf = kafka
    // Kafka topic name
    .topic("likesTopic")
    // Kafka producer configs
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        // in most cases, StringSerializer or ByteArraySerializer
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Kafka Test")
    .exec(
      kafka("request")
        // message to send
        .send[String]("{\"userId\":null,\"nickName\":\"jdoe\",\"likes\":1}"))
    .exec(
      kafka("request")
        // message to send
        .send[String]("{\"userId\":1,\"nickName\":null,\"likes\":1}"))

  setUp(
    scn
      .inject(constantUsersPerSec(500) during(2.seconds)))
    .protocols(kafkaConf)
}
