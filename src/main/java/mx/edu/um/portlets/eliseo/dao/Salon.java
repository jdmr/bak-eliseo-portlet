package mx.edu.um.portlets.eliseo.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name="salones", uniqueConstraints = {@UniqueConstraint(columnNames={"nombre","curso_id"})})
public class Salon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Column(length = 128, nullable = false)
    private String nombre;
    @ManyToOne
    private Curso curso;
    private Long maestroId;
    private String maestroNombre;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date termina;
    @OneToMany(mappedBy="salon")
    private Set<Sesion> periodos;
    
    public Salon() {}

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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the curso
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /**
     * @return the maestroId
     */
    public Long getMaestroId() {
        return maestroId;
    }

    /**
     * @param maestroId the maestroId to set
     */
    public void setMaestroId(Long maestroId) {
        this.maestroId = maestroId;
    }

    public String getMaestroNombre() {
        return maestroNombre;
    }

    public void setMaestroNombre(String maestroNombre) {
        this.maestroNombre = maestroNombre;
    }

    public Date getInicia() {
        return inicia;
    }

    public void setInicia(Date inicia) {
        this.inicia = inicia;
    }

    public Date getTermina() {
        return termina;
    }

    public void setTermina(Date termina) {
        this.termina = termina;
    }

    public Set<Sesion> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(Set<Sesion> periodos) {
        this.periodos = periodos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Salon other = (Salon) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Salon{" + "nombre=" + nombre + ", curso=" + curso + ", maestroNombre=" + maestroNombre + '}';
    }
    
}
