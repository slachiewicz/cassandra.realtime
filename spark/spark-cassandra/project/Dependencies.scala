import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val sparkCore = "org.apache.spark" %% "spark-core" % "3.0.1"
  lazy val sparkSQL = "org.apache.spark" %% "spark-sql" % "3.0.1"
  lazy val datastaxSCC = "com.datastax.spark" %% "spark-cassandra-connector" % "3.0.0"
  lazy val kafka = "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.0.1"
  lazy val sparkAvro = "org.apache.spark" %% "spark-avro" % "3.0.1"
}
