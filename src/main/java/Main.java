import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main started, waiting 5 more seconds to be sure InfluxDB is available");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }

        System.out.println("Creating client");

        InfluxDBClientOptions adminClientOptions = InfluxDBClientOptions.builder()
                .url(System.getenv("influxUrl"))
                .org(System.getenv("influxOrg"))
                .bucket(System.getenv("influxBucket"))
                .authenticateToken(System.getenv("influxToken").toCharArray())
                .build();

        InfluxDBClient client = InfluxDBClientFactory.create(adminClientOptions);

        System.out.println("Performing query");

        String flux = "from(bucket:\"%s\") |> range(start: 0)".formatted(System.getenv("influxBucket"));

        QueryApi queryApi = client.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        System.out.println("Query performed");
    }
}
