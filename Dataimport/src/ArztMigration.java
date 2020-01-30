import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;


public class ArztMigration extends Migration {
  public ArztMigration(Connection connection, MongoClient mongoClient) {
    super(connection, mongoClient);
  }


  public boolean migrate() throws SQLException {


      Statement statement = this.connection.createStatement();

      MongoDatabase mongoDatabase = this.mongoClient.getDatabase("mongo_massage");
      MongoCollection<Document> arztCollection = mongoDatabase.getCollection("Arzt");


      ResultSet resultSet = statement.executeQuery("SELECT * FROM Arzt");


      // Move cursor forward
      while (resultSet.next()) {
        int arztID = resultSet.getInt(1);
        String aNachname = resultSet.getString(2);
        String aVorname = resultSet.getString(3);
        String aFachgebiet = resultSet.getString(4);

        // Add Arzt in Dokument
        Document arztDocument = new Document();
        arztDocument.append("ArztID", arztID);
        arztDocument.append("aNachname", aNachname);
        arztDocument.append("aVorname", aVorname);
        arztDocument.append("aFachgebiet", aFachgebiet);



        // Add arztDokument in Collections
        arztCollection.insertOne(arztDocument);

      }


    // Performing a read operation on the collection.
    FindIterable<Document> fi = arztCollection.find();
    MongoCursor<Document> cursor = fi.iterator();
    try {
      while(cursor.hasNext()) {
        // List for patient
        List<Document> patientList = new ArrayList<Document>();

        Document current = cursor.next();

        // select all patient with existing ID
        ResultSet patiens = statement.executeQuery("Select * FROM Patient WHERE ArztID =" + current.getInteger("ArztID"));

        // Add in Document all Patients
        while (patiens.next()) {

          patientList.add(new Document().append("PatientID", patiens.getString(1))
              .append("Nachname", patiens.getString(2))
              .append("Vorname", patiens.getString(3))
              .append("PLZ", patiens.getString(4))
              .append("Ort", patiens.getString(5))
              .append("Strasse", patiens.getString(6))
              .append("Gdat", patiens.getString(7))
              .append("ArztID", patiens.getString(8))
              .append("Diagnosse", patiens.getString(9))
              .append("ADatum", patiens.getString(10)));


        }

        // Add arztDokument in Collections
        arztCollection.updateOne(
            current,
            new BasicDBObject("$set", new BasicDBObject("patients", patientList)));

      }
    } finally {
      cursor.close();
    }

    return false;
  }
}
