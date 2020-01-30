import static com.mongodb.client.model.Filters.eq;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
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
import java.util.Set;
import org.bson.Document;

public class BehandlungMigration extends Migration {

  public BehandlungMigration(Connection connection, MongoClient mongoClient) {
    super(connection, mongoClient);
  }

  public boolean migrate() throws SQLException {

    Statement statement = this.connection.createStatement();

    MongoDatabase mongoDatabase = this.mongoClient.getDatabase("mongo_massage");
    MongoCollection<Document> behandlungCollection = mongoDatabase.getCollection("Behandlung");

    ResultSet behandlungresultSet = statement.executeQuery("SELECT * FROM Heilmasseur");

    // Move cursor forward
    while (behandlungresultSet.next()) {

      int heilmasseurId = behandlungresultSet.getInt(1);

      // Add behandlung in Dokument
      Document behandlungDocument = new Document();

      behandlungDocument.append("HeilmasseurID", heilmasseurId);


     // Document myDoc = behandlungCollection.find(eq("HeilmasseurID", heilmasseurId)).first();


      // Add behandlungsDokument in Collections
      behandlungCollection.insertOne(behandlungDocument);


    }

    // Performing a read operation on the collection.
    FindIterable<Document> fi = behandlungCollection.find();
    MongoCursor<Document> cursor = fi.iterator();
    try {
      while(cursor.hasNext()) {
        // List for patient
        List<Document> patientList = new ArrayList<Document>();

        Document current = cursor.next();

        // select all patient with existing ID
        ResultSet patiens = statement.executeQuery("Select * FROM Behandlung WHERE HeilmasseurID =" + current.getInteger("HeilmasseurID"));

        // Add in Document all Patients
        while (patiens.next()) {

          patientList.add(new Document().append("PatientID", patiens.getInt(1))
              //.append("HeilmasseurID", patiens.getString(2))
              //.append("PgID", patiens.getString(3))
              .append("BhDatum", patiens.getString(4))
              .append("BArt", patiens.getString(5))
              .append("BPreis", patiens.getString(6)));


        }

        // Add arztDokument in Collections
        behandlungCollection.updateOne(
            current,
            new BasicDBObject("$set", new BasicDBObject("patients", patientList)));

      }
    } finally {
      cursor.close();
    }




    // Performing a read operation on the collection.
    FindIterable<Document> fi_pg = behandlungCollection.find();
    //MongoCursor<Document> cursor_pg = fi_pg.iterator();
    try {

      // select all patient with existing ID
      ResultSet praxisgemeinschaft = statement.executeQuery("Select * FROM Behandlung");

      while(praxisgemeinschaft.next()) {
        // List for patient
        List<Document> praxisList = new ArrayList<Document>();


        praxisList.add(new Document().append("PgID", praxisgemeinschaft.getInt(3))
                                     .append("prName", "HeilmassageRaum")
                                     .append("Raumnummer", "2"));


          // Add arztDokument in Collections
          behandlungCollection.updateOne(
              fi_pg.iterator().next(),
              new BasicDBObject("$set", new BasicDBObject("praxisgemeinschaft", praxisList)));

        }



    } finally {
      cursor.close();
    }


    return false;

  }
}
