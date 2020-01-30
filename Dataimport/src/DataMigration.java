import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

public class DataMigration {

  public static void main(String[] args) {
    try {
      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:8081/imse_db", "imse_user", "dodo");


      MongoCredential credential = (MongoCredential) MongoCredential.createCredential("imse_user", "mongo_massage", "massage".toCharArray());
      MongoClient mongoClient = new MongoClient(
          new ServerAddress("localhost", 27017),
          Collections.singletonList(credential)
      );


      Migration arztMigration = new ArztMigration(connection, mongoClient);
      Migration behandlungMigration = new BehandlungMigration(connection, mongoClient);

      arztMigration.migrate();
      behandlungMigration.migrate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

}
