/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.Turno;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.util.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class ProgramacionGruposFacade {

    private static final Logger log = LoggerFactory.getLogger(ProgramacionGruposFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @EJB
    ModuloFacade moduloFacade;
    @EJB
    GrupoFacade grupoFacade;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Grupo> programarGrupos(GestionAcademica gestionAcademica, Carrera carrera, Campus campus, Modulo modulo, Turno turno, Integer capacidad) {
        List<Grupo> grupos = new ArrayList<>();

        log.info("gestionAcademica=" + gestionAcademica);
        log.info("carrera=" + carrera);
        log.info("modulo=" + modulo);
        log.info("turno=" + turno);
        log.info("capacidad=" + capacidad);

        Integer c1 = grupoFacade.cantidadGrupos(gestionAcademica.getId_gestionacademica(), modulo.getId_modulo(), campus.getId_campus(), turno).intValue();
        String codigo = Constantes.ABECEDARIO[c1];

        Grupo grupo = new Grupo(codigo, capacidad, turno, true, gestionAcademica, modulo, campus);
        em.persist(grupo);

        grupos.add(grupo);

        return grupos;
    }

}
