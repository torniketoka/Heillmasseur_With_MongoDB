import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;

public class Mongo_Client{
    public static void main(String[] args){

      MongoCredential credential = MongoCredential.createCredential(
          "imse_user", "mongo_massage", "massage".toCharArray()
      );
      MongoClient mongoClient = new MongoClient(new ServerAddress(
          "localhost", 27017), Arrays.asList(credential)
      );
      MongoDatabase mongoDatabase = mongoClient.getDatabase("mongo_massage");

      if (args.length > 1) {
        System.out.println(args[0]);
        switch (args[0]) {
          case "Arzt": {
            Arzt.handle(args, mongoDatabase);
            break;
          }
          case "Behandlung": {
            Behandlung.handle(args, mongoDatabase);
            break;
          }
          default:
            throw new IllegalArgumentException("no such usecase");
        }
      } else {
        throw new IllegalArgumentException("check the arguments");
      }
    }
  }



