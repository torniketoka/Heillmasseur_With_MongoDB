import com.mongodb.MongoClient;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Migration {
  protected Connection connection;
  protected MongoClient mongoClient;

  Migration(Connection connection, MongoClient mongoClient) {
    this.connection = connection;
    this.mongoClient = mongoClient;
  }

  public abstract boolean migrate() throws SQLException;
}
