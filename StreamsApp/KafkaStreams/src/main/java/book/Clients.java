package book;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Clients {

    public static void main(String[] args) throws Exception {

        // create instance for properties to access producer configs
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaClients_v3");

        /*
        ------------------------------ TOPICS TO RECEIVE DATA FROM DATA BASE, AND TO PRODUCE -----------------------
        DBcurrenciesInfo
        DBclientIDsInfo
        "Credits_Topic"
        "Payments_topic"
        --------------------------------------------------------------------------------------------
        */

        //------------------------------------------------------------------------------------------
        // for consuming currencies and rates
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        Consumer<String, String> DBcurrenciesInfo = new KafkaConsumer<>(props);
        DBcurrenciesInfo.subscribe(Collections.singletonList("DBcurrenciesInfo"));
        
        // for consuming client IDs
        Consumer<String, String> DBclientIDsInfo = new KafkaConsumer<>(props);
        DBclientIDsInfo.subscribe(Collections.singletonList("DBclientIDsInfo"));
        //------------------------------------------------------------------------------------------
        

        //------------------------------------------------------------------------------------------
        // for producing credits and payments
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);
        //------------------------------------------------------------------------------------------

        Random rand = new Random();
        int counter = 0;
        HashMap<String,Double> currenciesAndRates = new HashMap<String,Double>();
        HashMap<String,String> clientIDList = new HashMap<String,String>();
        JsonObject convertedData = null, jsonPayload = null;

        while (true) {

            // consume conversion rates and currencies for 150 seconds
            ConsumerRecords<String, String> c_rates = DBcurrenciesInfo.poll(Duration.ofSeconds(150));
            if(!c_rates.isEmpty()){

                System.out.println("\n###### Getting currencies from DB ######");
                for(ConsumerRecord<String, String> record: c_rates){
                    convertedData = new Gson().fromJson(record.value().toString(), JsonObject.class);
                    jsonPayload = new Gson().fromJson(convertedData.get("payload"), JsonObject.class);
                    
                    // if the currency is not in the hash map add it
                    if(currenciesAndRates.get(jsonPayload.get("currency").getAsString()) == null){
                        currenciesAndRates.put(jsonPayload.get("currency").getAsString(), jsonPayload.get("rate").getAsDouble());
                        System.out.println("  -> New currency "+jsonPayload.get("currency").getAsString()+" added to list.");
                    }
                }
                System.out.println("########################################\n\n");
            }

            // consume client IDs for 150 seconds
            ConsumerRecords<String, String> clientIds = DBclientIDsInfo.poll(Duration.ofSeconds(150));
            if(!clientIds.isEmpty()){

                System.out.println("\n######### Getting IDs from DB ##########");
                for(ConsumerRecord<String, String> record: clientIds){
                    convertedData = new Gson().fromJson(record.value().toString(), JsonObject.class);
                    jsonPayload = new Gson().fromJson(convertedData.get("payload"), JsonObject.class);
                    
                    // if the id is not in the ID list add it
                    if(clientIDList.get(jsonPayload.get("id").getAsString()) == null){
                        clientIDList.put(jsonPayload.get("id").getAsString(), jsonPayload.get("mymanager_id").getAsString());
                        System.out.println("  -> New ID "+jsonPayload.get("id").getAsString()+" added to list.");
                    }
                }
                System.out.println("########################################\n\n");
            }


            if(clientIDList.size() > 0 && !currenciesAndRates.isEmpty()){
                System.out.println("\n############## PRODUCING ##############");

                Object[] clientIdsArray = clientIDList.keySet().toArray();
                String random_client = (String) clientIdsArray[rand.nextInt(clientIdsArray.length)];

                generateData("Credits_Topic", currenciesAndRates, random_client, clientIDList.get(random_client), rand, producer, counter);
                counter++;
                System.out.println("----------------------------------------\n\n");
                generateData("Payments_topic", currenciesAndRates, random_client, clientIDList.get(random_client), rand, producer, counter);
                counter++;          
                System.out.println("++++++++++++++++++++++++++++++++++++++++\n\n");
            }
        }
    }

    // produce credits and apayments to topic Credits_Topic Payments_topic
    public static void generateData(String topic,HashMap<String,Double> currenciesAndRates, String random_client, String managerID, Random rand, Producer<String, String> producer, int counter){

        // generate random credit and select random client
        Double Value = 1.0 + rand.nextInt(10);
        
        Object[] currencyArray = currenciesAndRates.keySet().toArray();
        String currency = (String) currencyArray[rand.nextInt(currencyArray.length)];

        // create string like json
        String data = "{\"Counter\":" + counter + 
                      ",\"Value\":" + Value + 
                      ",\"Currency\":\"" + currency + "\""+
                      ",\"Rate\":" + currenciesAndRates.get(currency) +
                      ",\"Manager\":" + managerID +
                      "}";

        producer.send(new ProducerRecord<String, String>(topic, random_client, data));
        System.out.println("Sending to "+topic+
                           "\nCounter: " + counter +
                           "\nID: " + random_client + 
                           "\nManager: " + managerID +
                           "\nValue: " + Value + 
                           "\nCurrency: " + currency +
                           "\nRate: " + currenciesAndRates.get(currency));
    }
}
