package my.cloudlegato;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;

import java.util.HashMap;
import java.util.UUID;

@Path( "/resource" )
public class MyResource {

    @ConfigProperty( name = "quarkus.keycloak.admin-client.realm" )
    String realmName;

    @Inject
    Keycloak keycloak;

    @GET
    @Path( "/p01" )
    @Produces( MediaType.APPLICATION_JSON )
    public Uni< Response > endPoint1( ) {
        return Uni.createFrom( ).item( Response.ok( new HashMap< String, String >( ) {{
            put( "msg", "ok" );
        }} ).build( ) );
    }

    @GET
    @Path( "/p02" )
    @Produces( MediaType.APPLICATION_JSON )
    // @Blocking
    public Uni< Response > endPoint2( ) {
        /***
         * This is where the org.jboss.resteasy.reactive.common.core.BlockingNotAllowedException is thrown (without @Blocking annotated on the method).
         *
         * If the @Blocking annotation is there, it will run fine...
         */
        return Uni.createFrom( ).item( keycloak.realm( realmName ) )
                .flatMap( realmResource -> {
                    // Just get the total count of users in the realm "quarkus-playground"
                    return Uni.createFrom( ).item( realmResource.users( ).count( ) )
                            .map( c -> {
                                return Response.ok( new HashMap< String, Integer >( ) {{
                                            put( "count", c );
                                        }} )
                                        .build( );
                            } );
                } );
    }

    @POST
    @Path( "/p03" )
    @Produces( MediaType.APPLICATION_JSON )
    public Uni< Response > endPoint3( ) {

        /**
         * This end point runs fine. Only Reactive Hibernate, Panache stuffs are involved - with transaction.
         */
        return Panache.withTransaction( ( ) -> {
            return Uni.createFrom( ).item( UUID.randomUUID( ).toString( ) )
                    .flatMap( uuid -> {

                        // Just create a dummy entity and persist it the reactive way.
                        DummyEntity entity = new DummyEntity( );
                        entity.uuid = uuid;
                        entity.num = 3;

                        return DummyEntity.persist( entity )
                                .map( v -> {
                                    return Response.ok( new HashMap< String, String >( ) {{
                                        put( "uuid", uuid );
                                    }} ).build( );
                                } );
                    } );
        } );
    }

    @POST
    @Path( "/p04" )
    @Produces( MediaType.APPLICATION_JSON )
    public Uni< Response > endPoint4( ) {
        /**
         * The below is a combination of keycloak calls and persistence to DB. This end point fails (with mix of keycloak calls and transaction) regardless if it is @blocking or @NonBlocking.
         */
        return Panache.withTransaction( ( ) -> {
            return Uni.createFrom( ).item( keycloak.realm( realmName ) )
                    .flatMap( realmResource -> {

                        // Just call the keycloak admin client and get the number of users.
                        return Uni.createFrom( ).item( realmResource.users( ).count( ) )
                                .flatMap( c -> {
                                    // Use the user count 'c' variable and set it as num in the entity.
                                    String uuid = UUID.randomUUID( ).toString( );

                                    DummyEntity entity = new DummyEntity( );
                                    entity.uuid = uuid;
                                    entity.num = c; // user count, without any meaning...

                                    // Persists...
                                    return DummyEntity.persist( entity )
                                            .map( v -> {
                                                return Response.ok( new HashMap< String, String >( ) {{
                                                    put( "uuid", uuid );
                                                }} ).build( );
                                            } );
                                } );
                    } );
        } );
    }
}
