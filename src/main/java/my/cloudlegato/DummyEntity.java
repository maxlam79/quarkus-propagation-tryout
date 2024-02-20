package my.cloudlegato;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table( name = "t_dummy" )
public class DummyEntity extends PanacheEntityBase {

    @Id
    @Column( name = "uuid" )
    public String uuid;

    @Column( name = "num" )
    public int num;
}
