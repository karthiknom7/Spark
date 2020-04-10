package cassandra;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class CassandraReadWrite {


    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local");
        sparkConf.setAppName("");
        sparkConf.set("spark.cassandra.connection.host", "localhost");
        sparkConf.set("spark.cassandra.auth.username", "cassandra");
        sparkConf.set("spark.cassandra.auth.password", "cassandra");
        sparkConf.set("spark.cassandra.output.consistency.level", "ONE");

        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();



        Dataset<Row> empDeptDataSet = sparkSession.read()
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "forecast")
                .option("table", "employee")
                .load();

        empDeptDataSet.show();

        // Do some mapping or aggregation of data and save result back to some other table

        empDeptDataSet.write()
                .mode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "forecast")
                .option("table", "employee")
                .save();
        System.out.println("Successful..........");

    }
}
