import org.apache.spark.sql.types._

val gameSchema = StructType(List(
  StructField("release_year", IntegerType, false),
  StructField("title", StringType, false),
  StructField("publishers", StringType, false),
  StructField("platforms", StringType, true)))


import org.apache.spark.sql.Row
val gameData = Seq(  
  Row(2017, "1-2-SWITCH", "NINTENDO", "Nintendo Switch"), 
  Row(2018, "7'SCARLET", "AKSYS GAMES", "Sony PS Vita"), 
  Row(2019, "8-BIT HORDES", "SOEDESCO", "Sony Playstation 4"), 
  Row(2017, "AEREA", "SOEDESCO", "Sony Playstation 4"), 
  Row(2018, "ARK PARK", "SNAIL GAMES", "Sony Playstation 4"), 
  Row(2017, "ARMS", "NINTENDO", "Nintendo Switch"), 
  Row(2017, "BAD APPLE WARS", "AKSYS", "Sony PS Vita"), 
  Row(2017, "CAVE STORY+", "SEGA/NICALIS", "Nintendo Switch"), 
  Row(2017, "COLLAR X MALICE", "AKSYS", "Sony PS Vita"), 
  Row(2018, "CONAN EXILES", "FUNCOM", "PC,Sony Playstation 4"), 
  Row(2018, "CONSTRUCTOR PLUS", "SYSTEM 3", "Nintendo Switch"), 
  Row(2017, "CULDCEPT REVOLT", "NIS AMERICA", "Nintendo 3DS"), 
  Row(2018, "DETECTIVE PIKACHU", "NINTENDO", "Nintendo 3DS"), 
  Row(2016, "DISNEY ART ACADEMY", "NINTENDO", "Nintendo 3DS"))

val gameDf = spark.createDataFrame(
  spark.sparkContext.parallelize(gameData), 
  gameSchema)


import org.apache.kudu.spark.kudu.KuduContext
val kuduContext = new KuduContext(
  "kudu-master-1:7051,kudu-master-2:7151,kudu-master-3:7251",
  spark.sparkContext)


val gameKuduTableName = "games"

if(kuduContext.tableExists(gameKuduTableName)) {
  kuduContext.deleteTable(gameKuduTableName)
}

import scala.collection.JavaConverters._
import org.apache.kudu.client.CreateTableOptions
kuduContext.createTable(gameKuduTableName,
  gameSchema, // Kudu schema with PK columns set as Not Nullable
  Seq("release_year", "title", "publishers"), // Primary Key Columns
  new CreateTableOptions().
    setNumReplicas(3).
    addHashPartitions(List("release_year").asJava, 2))

kuduContext.insertRows(gameDf, gameKuduTableName)

// We need to use leader_only because Kudu on Docker currently doesn't
// support Snapshot scans due to `--use_hybrid_clock=false`.
val gamesKuduDf = spark.read.
  option("kudu.master", 
  "kudu-master-1:7051,kudu-master-2:7151,kudu-master-3:7251"
    ).
  option("kudu.table", gameKuduTableName).
  option("kudu.scanLocality", "leader_only").
  format("kudu").
  load

gamesKuduDf.where($"release_year" === 2017).show


