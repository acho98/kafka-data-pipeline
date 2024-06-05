from kafka import KafkaProducer 
from json import dumps
import time, os
import pandas as pd
import numpy as np

def read_csv(file):
    df = pd.read_csv(file, skiprows = 1)
    df = df.iloc[np.random.permutation(df.index)].reset_index(drop=True)
    list_from_df = df.values.tolist()
    return list_from_df

producer = KafkaProducer(acks=0, compression_type='gzip', bootstrap_servers=['v2-kafka1:9092'], 
                         value_serializer=lambda x: dumps(x).encode('utf-8')) 

# start = time.time() 
# print("elapsed :", time.time() - start)

if __name__ == '__main__':
    current_path = os.getcwd()
    save_file = os.path.join(current_path, 'tx.csv')

    txs = read_csv(save_file)
    topic_name = 'tt-01'

    i = 0
    for tx in txs:
        data = {
            "userid": tx[0],
            "username": tx[1],
            "card_no": tx[2],
            "card_provider": tx[3],
            "shop_no": tx[4],
            "shop_name": tx[5],
            "lat": tx[6],
            "lon": tx[7],
            "loc": tx[8],
            "prod_name": tx[9],
            "price": tx[10]
        }
        print(data)
        time.sleep(1)
        i += 1
        producer.send(topic_name, value=data)
        if i > 100:
            producer.flush()
            i = 0
    producer.close()
