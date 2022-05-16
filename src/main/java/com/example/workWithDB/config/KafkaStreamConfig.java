package com.example.workWithDB.config;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

import com.example.Record;
import com.example.workWithDB.model.RecordModel;
import com.example.workWithDB.model.ReversedRecordModel;
import com.example.workWithDB.repository.RecordRepository;
import com.example.workWithDB.repository.ReversedRecordRepository;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@Configuration
@EnableKafkaStreams
@PropertySource("classpath:application.yaml")
@Slf4j
@RequiredArgsConstructor
public class KafkaStreamConfig {

  @Value("${kafka.application-id}")
  private String applicationId;

  @Value("${kafka.bootstrap-server-url}")
  private String bootstrapServerUrl;

  @Value("${kafka.schema-registry-url}")
  private String schemaRegistryUrl;

  @Value("${topic.name.source}")
  private String recordTopic;

  @Value("${topic.name.reversed}")
  private String reversedTopic;

  private final RecordRepository recordRepository;
  private final ReversedRecordRepository reversedRecordRepository;


  public SpecificAvroSerde<Record> getAvroSerde() {
    Map<String, String> serdeConf = Collections.singletonMap(
        AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    SpecificAvroSerde<Record> valueAvroSerde = new SpecificAvroSerde<>();
    valueAvroSerde.configure(serdeConf, false);
    return valueAvroSerde;
  }


  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  KafkaStreamsConfiguration kStreamsConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(APPLICATION_ID_CONFIG, applicationId);
    props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

    return new KafkaStreamsConfiguration(props);
  }


  @Bean
  public KStream<String, Record> saveRecord(StreamsBuilder streamsBuilder) {
    SpecificAvroSerde<Record> valueAvroSerde = getAvroSerde();
    KStream<String, Record> stream = streamsBuilder.stream(recordTopic,
            Consumed.with(Serdes.String(), valueAvroSerde))
        .peek((s, record) -> {
          RecordModel recordModel = new RecordModel();
          recordModel.setNumber(record.getNumber().toString());
          recordModel.setText(record.getText().toString());
          recordRepository.save(recordModel);
          log.info(
              "The record with number=[{}] and text=[{}] successfully saved. ", record.getNumber(),
              record.getText());
        });
    return stream;
  }

  @Bean
  public KStream<String, Record> saveReversedRecord(StreamsBuilder streamsBuilder) {
    SpecificAvroSerde<Record> valueAvroSerde = getAvroSerde();
    KStream<String, Record> stream = streamsBuilder.stream(reversedTopic,
            Consumed.with(Serdes.String(), valueAvroSerde))
        .peek((s, record) -> {
          ReversedRecordModel recordModel = new ReversedRecordModel();
          recordModel.setNumber(record.getNumber().toString());
          recordModel.setText(record.getText().toString());
          reversedRecordRepository.save(recordModel);
          log.info(
              "The record with number=[{}] and text=[{}] successfully saved. ", record.getNumber(),
              record.getText());
        });
    return stream;
  }

}
