package sparkCassandra

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd._
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro.functions._
import java.nio.file.{Files, Paths}

object Leaves {

    def main(args: Array[String]){

        val spark = SparkSession
            .builder()
            .appName("Leaves")
            .master("spark://ws-a079cda6-7ce0-4381-9843-8a14b54c3899:7077")
            .config("spark.cassandra.connection.config.cloud.path", "secure-connect-demo.zip")
            .config("spark.cassandra.auth.username", "username")
            .config("spark.cassandra.auth.password", "password")
            .config("spark.sql.extensions", "com.datastax.spark.connector.CassandraSparkExtensions")
            .getOrCreate()

        // spark.conf.set(s"spark.sql.catalog.mycatalog", "com.datastax.spark.connector.datasource.CassandraCatalog")

        import spark.implicits._
        
        // spark.sql("use mycatalog.test")

        // val leavesByTag = spark.sql("select tags as tag, title, url, tags from leaves").withColumn("tag", explode($"tag"))
        // val tagsDF = spark.sql("select tags as tag from leaves").withColumn("tag", explode($"tag")).groupBy("tag").count()

        // leavesByTag.createCassandraTable("test", "leaves_by_tag", partitionKeyColumns = Some(Seq("tag")), clusteringKeyColumns = Some(Seq("title")))
        // leavesByTag.write.cassandraFormat("leaves_by_tag", "test").mode("append").save()

        // tagsDF.createCassandraTable("test", "tags")
        // tagsDF.write.cassandraFormat("tags", "test").mode("append").save()

        val df = spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", "localhost:9092")
        .option("subscribe", "record-cassandra-leaves-avro")
        .load()

        val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("/workspace/cassandra.realtime/kafka/leaves-record-schema.avsc")))

        val leavesDF = df.select(from_avro(col("value"), jsonFormatSchema).as("leaves"))
        .select("leaves.*")

        val query = leavesDF.writeStream
        .outputMode("append")
        .format("console")
        .start()
        
        query.awaitTermination();

        spark.stop()



        // val conf = new SparkConf()
        // .setAppName("Leaves")
        // .setMaster("spark://ws-fe610c02-c570-4476-a579-66d8510a7307:7077")
        // .set("spark.cores.max", "10")
        // conf.set("spark.cassandra.connection.config.cloud.path", "secure-connect-demo.zip")
        // conf.set("spark.cassandra.auth.username", "username")
        // conf.set("spark.cassandra.auth.password", "password")
        // conf.set("spark.sql.extensions", "com.datastax.spark.connector.CassandraSparkExtensions")
        // conf.set("spark.executor.cores", "3")
        // val sc = new SparkContext(conf)
        // sc.addFile("/workspace/cassandra.realtime/spark/secure-connect-demo.zip")

        // val rdd = sc.cassandraTable("test", "leaves").select("tags").collect()

        // rdd.take(5).foreach(println)
        
        // sc.stop()
    }
}