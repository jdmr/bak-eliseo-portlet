package mx.edu.um.portlets.eliseo.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name="sesiones")
public class Sesion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    private Integer dia;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaInicial;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaFinal;
    @ManyToOne
    private Salon salon;

    public Sesion() {}

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
     * @return the dia
     */
    public Integer getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(Integer dia) {
        this.dia = dia;
    }

    /**
     * @return the horaInicial
     */
    public Date getHoraInicial() {
        return horaInicial;
    }

    /**
     * @param horaInicio the horaInicial to set
     */
    public void setHoraInicial(Date horaInicial) {
        this.horaInicial = horaInicial;
    }

    /**
     * @return the horaFinal
     */
    public Date getHoraFinal() {
        return horaFinal;
    }

    /**
     * @param horaFin the horaFinal to set
     */
    public void setHoraFinal(Date horaFinal) {
        this.horaFinal = horaFinal;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sesion other = (Sesion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Sesion{" + "dia=" + dia + ", horaInicial=" + horaInicial + ", horaFinal=" + horaFinal + ", salon=" + salon + '}';
    }
    
}
