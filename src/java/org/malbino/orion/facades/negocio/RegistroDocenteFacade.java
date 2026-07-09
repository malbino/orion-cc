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
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.facades.NotaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class RegistroDocenteFacade {
    
    private static final Logger log = LoggerFactory.getLogger(RegistroDocenteFacade.class);
    
    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;
    
    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    NotaFacade notaFacade;
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean editarNotas(List<Nota> notas) {
        for (Nota nota : notas) {
            em.merge(nota);
        }
        
        return true;
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Nota> listaRecuperatorios(GestionAcademica gestionacademica, Campus campus, int id_persona) {
        List<Nota> l = new ArrayList();
        
        List<Inscrito> inscritos = inscritoFacade.listaInscritosPruebaRecuperacion(gestionacademica, campus, id_persona);
        log.info("inscritos=" + inscritos.size());
        for (Inscrito inscrito : inscritos) {
            List<Nota> notas = notaFacade.listaNotasPruebaRecuperacion(inscrito, id_persona);
            l.addAll(notas);
            
            log.info("l=" + l.size());
        }
        
        return l;
    }
    
}
