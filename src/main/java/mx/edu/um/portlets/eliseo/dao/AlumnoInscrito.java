package mx.edu.um.portlets.eliseo.dao;

import com.liferay.portal.model.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "alumnos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"alumno_id", "salon_id"})})
public class AlumnoInscrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;
    @Column(length = 32, nullable = false)
    private String usuario;
    @Column(length = 128, nullable = false)
    private String correo;
    @Column(length = 200, nullable = false)
    private String nombreCompleto;
    @ManyToOne
    private Salon salon;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;

    public AlumnoInscrito() {
    }

    public AlumnoInscrito(User alumno, Salon salon) {
        alumnoId = alumno.getUserId();
        usuario = alumno.getScreenName();
        correo = alumno.getEmailAddress();
        nombreCompleto = alumno.getFullName();
        this.salon = salon;
        fecha = new Date();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the alumnoId
     */
    public Long getAlumnoId() {
        return alumnoId;
    }

    /**
     * @param alumnoId the alumnoId to set
     */
    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the nombreCompleto
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * @param nombreCompleto the nombreCompleto to set
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * @return the salon
     */
    public Salon getSalon() {
        return salon;
    }

    /**
     * @param salon the salon to set
     */
    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
