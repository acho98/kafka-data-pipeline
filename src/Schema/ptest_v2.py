import os
import pandas as pd
import numpy as np
import time, datetime

from configparser import ConfigParser
from confluent_kafka import avro
from confluent_kafka import KafkaError
from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro.serializer import SerializerError
from confluent_kafka.cimpl import TopicPartition

def read_csv(file):
    df = pd.read_csv(file, skiprows = 1)
    df = df.iloc[np.random.permutation(df.index)].reset_index(drop=True)
    list_from_df = df.values.tolist()
    return list_from_df

def kafka_producer():
    bootstrap_servers = "v2-kafka1:9092"
    schema_registry_url = "http://v2-schema:8081"
    group_id = "test-group-01"
    auto_offset_reset = "earliest"
    topic = "schema-1"

    # kafka producer
    work_dir = os.path.dirname(os.path.abspath(__file__))
    key_schema = None
    value_schema = avro.load(os.path.join(work_dir, 'avro_schema', 'schema_02.avsc'))
    avroProducer = AvroProducer({
        'bootstrap.servers': bootstrap_servers, 'schema.registry.url': schema_registry_url},
        default_key_schema=key_schema, default_value_schema=value_schema)

    return (avroProducer, topic)

if __name__ == '__main__':
    current_path = os.getcwd()
    save_file = os.path.join(current_path, 'tx.csv')

    txs = read_csv(save_file)
    producer, topic = kafka_producer()

    i = 0
    for tx in txs:
        value = {
            "userid": tx[0],
            "username": tx[1],
            "card_no":  tx[2]
        }
        print(value)
        time.sleep(0.25)
        i += 1
        producer.produce(topic=topic, value=value)
        if i > 100:
            producer.flush()
            i = 0
    producer.flush()
    producer.close()