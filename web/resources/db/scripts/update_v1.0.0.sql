/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  malbino
 * Created: Mar 8, 2026
 */

ALTER TABLE orion.estudiante ADD CERTIFICADOMEDICO TINYINT(1) DEFAULT 0 NULL;
ALTER TABLE orion.estudiante CHANGE CERTIFICADOMEDICO CERTIFICADOMEDICO TINYINT(1) DEFAULT 0 NULL AFTER CERTIFICADOFELCN;