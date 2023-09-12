package streams;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;

public class Kafka_Stream {

    protected static String counter = "not started";
    private static final DecimalFormat df = new DecimalFormat("0.0000");
    
    public static void main(String[] args) throws InterruptedException, IOException {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Trabalho3_v3");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, Double> creditPerClient = null;
        KTable<String, Double> paymentPerClient = null;
        KTable<String, Double> totalCredits = null;
        KTable<String, Double> totalPayments = null;
        KTable<String, Double> balancePerClient = null;
        KStream<String, String> creditsStream = builder.stream("Credits_Topic");
        KStream<String, String> paymentsStream = builder.stream("Payments_topic");
        
        /* 
        ------------------------------ TOPICS TO SEND DATA TO DATA BASE ----------------------------
        client
        results
        manager 
        ---------------------------------------------------------------------------------------------
        ------------------------------ TOPICS TO RECEIVE DATA FROM DATA BASE -----------------------
        DBclientIDsInfo
        --------------------------------------------------------------------------------------------
        */


        // ----------------------------------------------------------------------------------------------- credits per client
        creditPerClient = creditsStream
            .mapValues((k,v) -> convertCurrencyToEuro(k, v, "Credits per client"))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .reduce((v1, v2) -> v1 + v2);
        creditPerClient.toStream().mapValues((k,v) -> convertToJson(k, df.format(v), "double", "credits")).to("client");
        

        // ----------------------------------------------------------------------------------------------- payments per client     
        paymentPerClient = paymentsStream
            .mapValues((k,v) -> convertCurrencyToEuro(k, v, "Payments per client"))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .reduce((v1, v2) -> v1 + v2);
        paymentPerClient.toStream().mapValues((k,v) -> convertToJson(k, df.format(v), "double", "payments")).to("client");
        

        // ----------------------------------------------------------------------------------------------- balance per client 
        if(creditPerClient != null && paymentPerClient != null){
            ValueJoiner<Double, Double, Double> balanceJoiner = (leftValue, rightValue) -> {return  leftValue - rightValue;};
            balancePerClient = paymentPerClient.join(creditPerClient, balanceJoiner);
            balancePerClient.toStream().mapValues((k,v) -> convertToJson(k, df.format(v), "double", "balance")).to("client");
        }
        

        // ----------------------------------------------------------------------------------------------- total credits 
        totalCredits = creditsStream
            .mapValues(v -> convertCurrencyToEuro("-1", v, "Total credits"))
            .groupBy((k, v) -> "-1", Grouped.with(Serdes.String(), Serdes.Double()))
            .reduce((v1, v2) -> v1 + v2);
        totalCredits.toStream().mapValues((k,v) -> convertToJson("-1", df.format(v), "double", "total_credits")).to("results");
        
        
        // ----------------------------------------------------------------------------------------------- total payments 
        totalPayments = paymentsStream
            .mapValues(v -> convertCurrencyToEuro("-1", v, "Total payments"))
            .groupBy((k, v) -> "-1", Grouped.with(Serdes.String(), Serdes.Double()))
            .reduce((v1, v2) -> v1 + v2);
        totalPayments.toStream().mapValues((k,v) -> convertToJson("-1", df.format(v), "double", "total_payments")).to("results");
        
        
        // ----------------------------------------------------------------------------------------------- total balance
        if(totalCredits != null && totalPayments != null){
            ValueJoiner<Double, Double, Double> totalBalanceJoiner = (leftValue, rightValue) -> {return  leftValue - rightValue;};
            KTable<String, Double> totalBalance = totalPayments.join(totalCredits, totalBalanceJoiner);
            totalBalance.toStream().mapValues((k,v) -> convertToJson("-1", df.format(v), "double", "total_balance")).to("results");
        }
        

        // ------------------------------------------------------------------------------------------ last month bill per client 
        // NOTE: time window is set to 30 seconds
        // for this method it is necessary to use new windowed tables 
        KTable<Windowed<String>, Double> credidtBillPerclient = creditsStream
            .mapValues((k,v) -> convertCurrencyToEuro(k, v, "Credits per client for computing BILL "))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
            .reduce((v1, v2) -> v1 + v2, Materialized.as("windowStoreCredits"));

        KTable<Windowed<String>, Double> paymentBillPerClient = paymentsStream
            .mapValues((k,v) -> convertCurrencyToEuro(k, v, "Payments per client for computing BILL"))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
            .reduce((v1, v2) -> v1 + v2, Materialized.as("windowStorePayments"));

        ValueJoiner<Double, Double, Double> balanceJoiner = (leftValue, rightValue) -> {return  leftValue - rightValue;};
        KTable<Windowed<String>, Double> billPerClient = paymentBillPerClient.join(credidtBillPerclient, balanceJoiner);
        billPerClient
            .toStream()
            .map((wk,v) -> new KeyValue<String, String>(wk.key(), convertToJson(wk.key(), df.format(Math.abs(v)), "double", "last_month_bill")))
            .to("client");
        

        // ----------------------------------------------------------------------------------------------- count payments made in the past hour
        // NOTE: time window is ser to 1 hour
        KTable<Windowed<String>, Long> clients_w_out_payments = paymentsStream
            .mapValues((k,v) -> convertCurrencyToEuro(k, v, "Counting payments per client"))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .windowedBy(TimeWindows.of(Duration.ofSeconds(3600)))
            .count();
            
        clients_w_out_payments
            .toStream()
            .map((wk,v) -> new KeyValue<String, String>(wk.key(), convertToJson(wk.key(), Long.toString(Math.abs(v)), "double", "payments_made_in_past_hour")))
            .to("client");
        
        // ---------------------------------------------------------------------------------------------- get ID of client with biggest debt
        if(balancePerClient != null){
            KTable<String, Double> highestDebtPerClient = balancePerClient
                .toStream()
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Double.MAX_VALUE, (k, v, aggregate) -> Math.min(v, aggregate), Materialized.with(Serdes.String(), Serdes.Double()));

            highestDebtPerClient.toStream().mapValues((k,v) -> convertToJson("-1", k, "int64", "client_w_biggest_debt")).to("results");
        }
        
        // -------------------------------------------------------------------------------------------- get ID of manager with biggest revenue
        // create new table with managers with manger IDs has keys by trnasforming the keys to a new key
        KTable<String, Double> managerID = paymentsStream
            .map((k,v) -> new KeyValue<String, Double>(getManagerID(v), convertCurrencyToEuro(k, v, "Calculating manager ID of the manager with highest revenue")))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .aggregate(() -> Double.MIN_VALUE, (k, v, aggregate) -> Math.max(v, aggregate), Materialized.with(Serdes.String(), Serdes.Double()));

        // put the resulting manager ID in the results table 
        managerID.toStream().mapValues((k,v) -> convertToJson("-1", k, "int64", "manager_w_biggest_revenue")).to("results");
        
        
        // ------------------------------------------------------------------------------- calculate manager revenue just for visualization
        KTable<String, Double> managersRevenue = paymentsStream
            .map((k,v) -> new KeyValue<String, Double>(getManagerID(v), convertCurrencyToEuro(k, v, "Payments per client for manager revenue")))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
            .reduce((v1, v2) -> v1 + v2);

        // update manager revenue
        managersRevenue.toStream().mapValues((k,v) -> convertToJson(k, df.format(v), "double", "revenue")).to("manager");
        

        // ---------------------------------------------------------------- start the streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private static Double convertCurrencyToEuro(String key, String data_in_json_format, String data_type){

        if(key != null && data_in_json_format != null){
            
            // get values from data_in_json_format
            JsonObject convertedData = new Gson().fromJson(data_in_json_format, JsonObject.class);
            Double value = convertedData.get("Value").getAsDouble();
            String currency = convertedData.get("Currency").toString();
            Double rate = convertedData.get("Rate").getAsDouble();
            counter = convertedData.get("Counter").toString();
            Double value_in_euro = value / rate;
            
            if(currency.equals("\"Euro\"")){
                value_in_euro = value;
            }
            
            /*System.out.println("\n----------------RECEIVED----------------"+
                               "\n" + data_type + 
                               "\nCounter: " + counter + 
                               "\nID: " + key + 
                               "\nValue: " + value + 
                               "\ncurrency: " + currency +
                               "\nrate: " + rate +
                               "\nvalue in EURO: " + value_in_euro);*/
            return value_in_euro;
        }
        else{
            return 0.0;
        }
    }

    private static String convertToJson(String k, String v, String type, String tableColumn){
        
        String jsonString = 
        "{\"schema\":{\"type\":\"struct\",\"fields\":" +
            "["+
                "{\"type\":\"int64\",\"optional\":false,\"field\":\"id\"}," +
                "{\"type\":\""+type+"\",\"optional\":false,\"field\":\""+tableColumn+"\"}" +
            "],"+
            "\"optional\":false},"+
            "\"payload\":{\"id\":"+k+",\""+tableColumn+"\":"+v+"}"+
        "}";

        System.out.println("-------------------SENDING--------------------"+
                           "\nCounter: " + counter + 
                           "\nOperation: " + tableColumn+
                           "\nID: " + k +
                           "\nValue: " + v + 
                           "\n--------------------------------------------\n");
        return jsonString;
    }

    private static String getManagerID(String data_in_json_format){
    
        JsonObject convertedData = new Gson().fromJson(data_in_json_format, JsonObject.class);
        return convertedData.get("Manager").toString();
    }
}
