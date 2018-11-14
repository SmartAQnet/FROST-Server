package de.fraunhofer.iosb.ilt.sta.persistence.postgres.stringid.relationalpaths;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths.AbstractQMultiDatastreamsObsProperties;
import java.sql.Types;

/**
 * QMultiDatastreamsObsPropertiesString is a Querydsl query type for
 * QMultiDatastreamsObsPropertiesString
 */
public class QMultiDatastreamsObsPropertiesString extends AbstractQMultiDatastreamsObsProperties<QMultiDatastreamsObsPropertiesString, StringPath, String> {

    private static final long serialVersionUID = 1753892463;
    private static final String TABLE_NAME = "MULTI_DATASTREAMS_OBS_PROPERTIES";

    public static final QMultiDatastreamsObsPropertiesString MULTIDATASTREAMSOBSPROPERTIES = new QMultiDatastreamsObsPropertiesString(TABLE_NAME);

    public final StringPath multiDatastreamId = createString("multiDatastreamId");

    public final StringPath obsPropertyId = createString("obsPropertyId");

    public QMultiDatastreamsObsPropertiesString(String variable) {
        super(QMultiDatastreamsObsPropertiesString.class, forVariable(variable), "PUBLIC", TABLE_NAME);
        addMetadata();
    }

    private void addMetadata() {
        addMetadata(multiDatastreamId, ColumnMetadata.named("MULTI_DATASTREAM_ID").ofType(Types.VARCHAR).withSize(36).notNull());
        addMetadata(obsPropertyId, ColumnMetadata.named("OBS_PROPERTY_ID").ofType(Types.VARCHAR).withSize(36).notNull());
    }

    @Override
    public StringPath getMultiDatastreamId() {
        return multiDatastreamId;
    }

    @Override
    public StringPath getObsPropertyId() {
        return obsPropertyId;
    }

    @Override
    public QMultiDatastreamsObsPropertiesString newWithAlias(String variable) {
        return new QMultiDatastreamsObsPropertiesString(variable);
    }

}
