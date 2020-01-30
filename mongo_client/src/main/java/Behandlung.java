import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Behandlung {

  public static void handle(String[] args, MongoDatabase mdb){
    switch (args[1]) {
      case "create": {
        Document doc = new Document()
            .append("HelmasseurID", args[2])
            .append("patients",
                new Document("PatientID", args[3])
                    .append("BhDatum", args[4])
                    .append("BArt", args[5])
                    .append("BPreis", args[6])
                    .append("praxisgemeinschaft",
                        new Document("PgID", args[7])
                            .append("prName", args[8])
                            .append("Raumnummer", args[9])));


        MongoCollection<Document> collection = mdb.getCollection("Behandlung");
        collection.insertOne(doc);
        System.out.println("create successfull");
        break;
      }
      case "read": {
        MongoCollection<Document> collection = mdb.getCollection("Behandlung");
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
          while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
          }
        } finally {
          cursor.close();
        }
        break;
      }
      case "update": {
        mdb.getCollection("Behandlung").updateOne(
                eq("_id", new ObjectId(args[2])),
                set("patients.BArt", args[3]));
        System.out.println("update successfull");
        break;
      }
      case "delete": {
        MongoCollection<Document> collection = mdb.getCollection("Behandlung");
        collection.deleteOne(eq("HelmasseurID", args[2]));
        System.out.println("delete successfull");
        break;
      }
      default: throw new IllegalArgumentException("unknown command");
    }
  }
}
