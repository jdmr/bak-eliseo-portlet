package mx.edu.um.portlets.eliseo.web;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import mx.edu.um.portlets.eliseo.dao.Sesion;

/**
 *
 * @author jdmr
 */
public class SesionVO implements Serializable {
    private Long id;
    private Integer dia;
    private String horaInicial;
    private String horaFinal;
    
    public SesionVO() {}
    
    public SesionVO(Sesion sesion, SimpleDateFormat sdf) {
        id = sesion.getId();
        dia = sesion.getDia();
        horaInicial = sdf.format(sesion.getHoraInicial());
        horaFinal = sdf.format(sesion.getHoraFinal());
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
    public String getHoraInicial() {
        return horaInicial;
    }

    /**
     * @param horaInicial the horaInicial to set
     */
    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    /**
     * @return the horaFinal
     */
    public String getHoraFinal() {
        return horaFinal;
    }

    /**
     * @param horaFinal the horaFinal to set
     */
    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }
    
}
