name := "MyApp"
version := "1.0"
scalaVersion := "2.12.10"
resolvers += "aliyun" at "https://maven.aliyun.com/repository/central"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.0.1" % "provided"
)
