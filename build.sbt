name := "MyApp"
version := "1.0"
scalaVersion := "2.12.12"
resolvers += "aliyun" at "https://maven.aliyun.com/repository/public"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.0.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "3.0.1" % "provided",
  "org.apache.spark" %% "spark-core" % "3.0.1" % "provided"
)

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
