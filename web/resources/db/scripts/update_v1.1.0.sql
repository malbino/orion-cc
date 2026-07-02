/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  malbino
 * Created: Jul 1, 2026
 */

DELETE FROM orion.actividad;
ALTER TABLE orion.instituto DROP COLUMN PRECIOCREDITO;
ALTER TABLE orion.carrera CHANGE CREDITAJEMATRICULA PRECIO decimal(34,9) DEFAULT NULL NULL;
ALTER TABLE orion.modulo DROP COLUMN CREDITAJEMODULO;
DROP TABLE orion.descuento;