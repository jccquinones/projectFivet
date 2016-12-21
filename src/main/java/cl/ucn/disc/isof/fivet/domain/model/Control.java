package cl.ucn.disc.isof.fivet.domain.model;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.*;

import java.util.Date;

/**
 * Clase que representa a un Control de un Paciente
 *
 * Created by JOHN on 09-11-2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Control {

    /**
     * Fecha
     */
    @Getter
    @NotEmpty
    @Column
    private Date fecha;

    /**
     * Fecha del próximo control
     */
    @Getter
    @NotEmpty
    @Column
    private Date proximoControl;

    /**
     * Temperatura
     */
    @Getter
    @NotEmpty
    @Column
    private double temperatura;

    /**
     * Peso
     */
    @Getter
    @NotEmpty
    @Column
    private double peso;

    /**
     * Altura
     */
    @Getter
    @NotEmpty
    @Column
    private double altura;

    /**
     * Diagnostico
     */
    @Getter
    @NotEmpty
    @Column
    private String diagnostico;

    /**
     * Nota
     */
    @Getter
    @NotEmpty
    @Column
    private String nota;

    /**
     * Id
     */
    @Getter
    @NotEmpty
    @Column
    private int id;

    /**
     * Veterinario
     */
    @Getter
    @NotEmpty
    @Column
    @ManyToOne
    private Persona veterinario;

}