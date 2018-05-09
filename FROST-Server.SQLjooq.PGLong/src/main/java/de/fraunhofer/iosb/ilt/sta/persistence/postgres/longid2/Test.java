/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fraunhofer.iosb.ilt.sta.persistence.postgres.longid2;

import de.fraunhofer.iosb.ilt.sta.persistence.postgres.longid2.jooq.Tables;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.longid2.jooq.tables.Things;
import java.sql.Connection;
import java.sql.DriverManager;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class Test {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {
        String userName = "sensorthings";
        String password = "ChangeMe";
        String url = "jdbc:postgresql://localhost:5432/sensorthingsTest";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        Class.forName("org.postgresql.Driver");
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            Settings settings = new Settings()
                    .withRenderSchema(false);

            DSLContext dbContext = DSL.using(conn, SQLDialect.POSTGRES, settings);

            Param<Double> constDouble = DSL.val(5.5);

            SelectSelectStep<Record> select = dbContext.select();
            SelectJoinStep<Record> from = select.from(Tables.DATASTREAMS);
            Things ThingsA1 = Tables.THINGS.as("a1");
            from.join(ThingsA1).on(ThingsA1.ID.eq(Tables.DATASTREAMS.THING_ID));
            from.where(ThingsA1.ID.plus(constDouble).gt(6L));

            Result<Record> result = select.fetch();
            for (Record record : result) {
                String thingName = record.get(ThingsA1.NAME);
                String datastreamName = record.get(Tables.DATASTREAMS.NAME);
                LOGGER.info("Found {} - {}", thingName, datastreamName);
            }
            conn.close();
        } catch (Exception exc) {
            LOGGER.error("oops", exc);
        }
    }

}
