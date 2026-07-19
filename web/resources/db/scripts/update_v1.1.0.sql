/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  malbino
 * Created: Jul 1, 2026
 */

DELETE FROM actividad;
ALTER TABLE instituto DROP COLUMN PRECIOCREDITO;
ALTER TABLE carrera CHANGE CREDITAJEMATRICULA PRECIO decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE modulo DROP COLUMN CREDITAJEMODULO;
DROP TABLE descuento;
ALTER TABLE conceptopago MODIFY COLUMN PRECIOUNITARIO decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE detalle MODIFY COLUMN CANTIDAD decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE detalle MODIFY COLUMN DESCUENTO decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE detalle MODIFY COLUMN PRECIOUNITARIO decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE detalle MODIFY COLUMN SUBTOTAL decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE detalle DROP FOREIGN KEY FK_detalle_id_modulo;
ALTER TABLE detalle DROP COLUMN id_modulo;
ALTER TABLE detalle ADD id_cuota int(11) NULL;
ALTER TABLE detalle ADD CONSTRAINT FK_detalle_id_cuota FOREIGN KEY (id_cuota) REFERENCES cuota(ID_CUOTA) ON DELETE RESTRICT ON UPDATE RESTRICT;
UPDATE recurso SET NOMBRE='Reportes > Ingresos > Cuota', URLPATTERN='/reportes/ingresos/cuota/' WHERE NOMBRE='Reportes > Ingresos > Modulo';

INSERT INTO `recurso` (`ID_RECURSO`, `NOMBRE`, `URLPATTERN`) VALUES ((SELECT MAX(r.ID_RECURSO)+1 FROM `recurso` r), 'Reportes > Ficha Inscripción', '/reportes/fichaInscripcion/');
INSERT INTO `privilegio` (`id_recurso`, `id_rol`) VALUES ((SELECT MAX(r.ID_RECURSO) FROM `recurso` r), 1);

INSERT INTO `recurso` (`ID_RECURSO`, `NOMBRE`, `URLPATTERN`) VALUES ((SELECT MAX(r.ID_RECURSO)+1 FROM `recurso` r), 'Inscripciones > Cuotas Estudiante', '/inscripciones/cuotasEstudiante.xhtml');
INSERT INTO `privilegio` (`id_recurso`, `id_rol`) VALUES ((SELECT MAX(r.ID_RECURSO) FROM `recurso` r), 1);
