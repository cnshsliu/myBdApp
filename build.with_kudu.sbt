name := "MyApp"
version := "1.0"
scalaVersion := "2.12.12"
val kuduVersion = "1.13.0.7.2.6.1-1"
resolvers += "aliyun" at "https://maven.aliyun.com/repository/public"
resolvers += "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
val kudu = Seq (
  "org.apache.kudu" % "kudu-client" % kuduVersion % "compile" ,
  "org.apache.kudu" %% "kudu-spark3" % kuduVersion % "compile"
)
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.0.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "3.0.1" % "provided",
  "org.apache.spark" %% "spark-core" % "3.0.1" % "provided"
) ++ kudu



assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
