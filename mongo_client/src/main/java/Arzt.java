import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Arzt {

  public static void handle(String[] args, MongoDatabase mdb){
    switch (args[1]) {
      case "create": {
        Document doc = new Document()
            .append("ArztID", args[2])
            .append("aNachname", args[3])
            .append("aVorname", args[4])
            .append("aFachgebiet", args[5])
            .append("patients",
                new Document("PatientID", args[6])
                    .append("Nachname", args[7])
                    .append("Vorname", args[8])
                    .append("PLZ", args[9])
                    .append("Ort", args[10])
                    .append("Strasse", args[11])
                    .append("Gdat", args[12])
                    .append("ArztID", args[13])
                    .append("Diagnosse", args[14])
                    .append("ADatum", args[15]));
        MongoCollection<Document> collection = mdb.getCollection("Arzt");
        collection.insertOne(doc);
        System.out.println("create successfull");
        break;
      }
      case "read": {
        MongoCollection<Document> collection = mdb.getCollection("Arzt");
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
        mdb.getCollection("Arzt").updateOne(
            eq("_id", new ObjectId(args[2])),
            set("aFachgebiet", args[3]));
        System.out.println("update successfull");
        break;
      }
      case "delete": {
        MongoCollection<Document> collection = mdb.getCollection("Arzt");
        collection.deleteOne(eq("ArztID", args[2]));
        System.out.println("delete successfull");
        break;
      }
      default: throw new IllegalArgumentException("unknown command");
    }
  }
}
