import os

from confluent_kafka import avro
from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro.serializer import SerializerError

bootstrap_servers = "v2-kafka1:9092"
schema_registry_url = "http://v2-schema:8081"
group_id = "s-gp-01"
auto_offset_reset = "earliest"
topic = ["schema-1"]


work_dir = os.path.dirname(os.path.abspath(__file__))
key_schema = None
value_schema = avro.load(os.path.join(work_dir, 'avro_schema', 'schema_01.avsc'))
avroConsumer = AvroConsumer({
    'bootstrap.servers': bootstrap_servers, 'schema.registry.url': schema_registry_url,
    'group.id': group_id, 'auto.offset.reset': auto_offset_reset},
    reader_key_schema=key_schema, reader_value_schema=value_schema)

avroConsumer.subscribe(topic)

while True:
    try:
        msg = avroConsumer.poll(10)
    except SerializerError as e:
        print("Message deserialization failed for {}: {}".format(msg, e))
        break
    if msg is None:
        continue
    if msg.error():
        print("AvroConsumer error: {}".format(msg.error()))
        continue
    print(msg.value())

avroConsumer.close()