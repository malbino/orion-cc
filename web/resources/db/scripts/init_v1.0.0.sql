-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 17, 2025 at 08:44 AM
-- Server version: 10.5.13-MariaDB
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `orion`
--

--
-- Dumping data for table `persona`
--

INSERT INTO `persona` (`ID_PERSONA`, `tipo`, `CELULAR`, `DIRECCION`, `DNI`, `EMAIL`, `FECHANACIMIENTO`, `FOTO`, `LUGAREXPEDICION`, `LUGARNACIMIENTO`, `NACIONALIDAD`, `NOMBRE`, `PRIMERAPELLIDO`, `SEGUNDOAPELLIDO`, `SEXO`, `TELEFONO`) VALUES
(1, 'Empleado', NULL, NULL, NULL, 'maacrazy@gmail.com', '1985-03-26', NULL, NULL, NULL, NULL, 'Admin', ' ', NULL, NULL, NULL);

--
-- Dumping data for table `empleado`
--

INSERT INTO `empleado` (`id_persona`, `ABREVIATURAPROFESION`, `CODIGO`) VALUES
(1, NULL, NULL);

--
-- Dumping data for table `usuario`
--

INSERT INTO `usuario` (`id_persona`, `CONTRASENA`, `USUARIO`) VALUES
(1, 'e239f67756bba3af660e4226c340183a9ca4bdc40038c0cfdea2fbaa59605be32548df2535e5a9f9ceedb12d9666c6fb153ada99830ed5cd84eb0c2c4d00260a', 'admin');

--
-- Dumping data for table `instituto`
--

INSERT INTO `instituto` (`ID_INSTITUTO`, `ABREVIATURA`, `CARACTER`, `CODIGO`, `EMAIL`, `LOGO`, `NOMBRE`, `PRECIOCREDITO`, `id_directoracademico`, `id_rector`) VALUES
(1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1);

--
-- Dumping data for table `rol`
--

INSERT INTO `rol` (`ID_ROL`, `NOMBRE`) VALUES
(1, 'Administrador'),
(2, 'Estudiante'),
(3, 'Docente');

--
-- Dumping data for table `recurso`
--

INSERT INTO `recurso` (`ID_RECURSO`, `NOMBRE`, `URLPATTERN`) VALUES
(1, 'Home', '/home.xhtml'),
(2, 'Calendario Academico', '/calendarioAcademico.xhtml'),
(3, 'Cambiar Contraseña', '/cambiarContrasena.xhtml'),

(4, 'Administrador > Instituto', '/administrador/instituto.xhtml'),
(5, 'Administrador > Empleados', '/administrador/empleado/'),
(6, 'Administrador > Roles', '/administrador/rol/'),
(7, 'Administrador > Usuarios', '/administrador/usuario/'),
(8, 'Administrador > Logs', '/administrador/logs.xhtml'),

(9, 'Planes de Estudio > Campus', '/planesEstudio/campus/'),
(10, 'Planes de Estudio > Carreras', '/planesEstudio/carrera/'),
(11, 'Planes de Estudio > Modulos', '/planesEstudio/modulo/'),

(12, 'Gestiónes Académicas > Calendario Académico', '/gestionesAcademicas/calendarioAcademico/'),
(13, 'Gestiónes Académicas > Gestiones Académicas', '/gestionesAcademicas/gestionAcademica/'),
(14, 'Gestiónes Académicas > Grupos', '/gestionesAcademicas/grupo/'),

(15, 'Horarios > Asignación Docente', '/horarios/asignacionDocente/'),
(16, 'Horarios > Aulas', '/horarios/aula/'),
(17, 'Horarios > Periodos', '/horarios/periodo/'),
(18, 'Horarios > Horario Paralelo', '/horarios/horarioParalelo.xhtml'),

(19, 'Inscripciones > Estudiante Nuevo', '/inscripciones/estudianteNuevo/'),
(20, 'Inscripciones > Estudiante Nuevo Postulación', '/inscripciones/estudianteNuevoPostulacion/'),
(21, 'Inscripciones > Estudiante Regular', '/inscripciones/estudianteRegular/'),
(22, 'Inscripciones > Cambio de Carrera', '/inscripciones/cambioCarrera/'),
(23, 'Inscripciones > Inscripción Manual', '/inscripciones/inscripcionManual/'),
(24, 'Inscripciones > Inscritos', '/inscripciones/inscritos.xhtml'),

(25, 'Cajas > Conceptos de Pago', '/cajas/conceptoPago/'),
(26, 'Cajas > Descuentos', '/cajas/descuento/'),
(27, 'Cajas > Nuevo Comprobante', '/cajas/nuevoComprobante/'),
(28, 'Cajas > Comprobantes', '/cajas/comprobantes/'),

(29, 'Registro Notas (Solo para administradores)', '/registroNotas/'),
(30, 'Registro Docente (Solo para docentes)', '/registroDocente/'),

(31, 'File Estudiante > Historial Academico', '/fileEstudiante/historialAcademico/'),
(32, 'File Estudiante > Historial Economico', '/fileEstudiante/historialEconomico/'),
(33, 'File Estudiante > Estudiantes', '/fileEstudiante/estudiante/'),

(34, 'Estudiante (Solo para estudiantes)', '/estudiante/'),

(35, 'Reportes > Dashboard', '/reportes/dashboard.xhtml'),
(36, 'Reportes > Plan de Estudios', '/reportes/planEstudios/'),

(37, 'Reportes > Inscripciones > Lista Inscritos Carrera', '/reportes/inscripciones/listaInscritosCarrera/'),
(38, 'Reportes > Inscripciones > Lista Inscritos Paralelo', '/reportes/inscripciones/listaInscritosParalelo/'),
(39, 'Reportes > Inscripciones > Lista Inscritos Grupo', '/reportes/inscripciones/listaInscritosGrupo/'),
(40, 'Reportes > Inscripciones > Libro de Inscripciones', '/reportes/inscripciones/libroInscripciones/'),
(41, 'Reportes > Inscripciones > Libro de Inscripciones Carrera', '/reportes/inscripciones/libroInscripcionesCarrera/'),
(42, 'Reportes > Inscripciones > Lista Inscritos Multicarrera', '/reportes/inscripciones/listaInscritosMulticarrera/'),

(43, 'Reportes > Horarios > Horario Aula', '/reportes/horarios/horarioAula/'),
(44, 'Reportes > Horarios > Horario Paralelo', '/reportes/horarios/horarioParalelo/'),
(45, 'Reportes > Horarios > Horario Docente', '/reportes/horarios/horarioDocente/'),

(46, 'Reportes > Ingresos > Concepto de Pago', '/reportes/ingresos/conceptoPago/'),
(47, 'Reportes > Ingresos > Modulo', '/reportes/ingresos/modulo/'),

(48, 'Reportes > Notas > Boletín Notas', '/reportes/notas/boletinNotas/'),
(49, 'Reportes > Notas > Boletín Notas Carrera', '/reportes/notas/boletinNotasCarrera/'),
(50, 'Reportes > Notas > Planilla Seguimiento', '/reportes/notas/planillaSeguimiento/'),
(51, 'Reportes > Notas > Registro Notas', '/reportes/notas/registroNotas/'),
(52, 'Reportes > Notas > Habilitados Recuperatorio', '/reportes/notas/habilitadosRecuperatorio/'),
(53, 'Reportes > Notas > Notas Faltantes', '/reportes/notas/notasFaltantes/'),

(54, 'Reportes > Historial Academico', '/reportes/historialAcademico/'),
(55, 'Reportes > Historial Economico', '/reportes/historialEconomico/');

--
-- Dumping data for table `privilegio`
--

INSERT INTO `privilegio` (`id_recurso`, `id_rol`) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1),
(16, 1),
(17, 1),
(18, 1),
(19, 1),
(20, 1),
(21, 1),
(22, 1),
(23, 1),
(24, 1),
(25, 1),
(26, 1),
(27, 1),
(28, 1),
(29, 1),
(31, 1),
(32, 1),
(33, 1),
(35, 1),
(36, 1),
(37, 1),
(38, 1),
(39, 1),
(40, 1),
(41, 1),
(42, 1),
(43, 1),
(44, 1),
(45, 1),
(46, 1),
(47, 1),
(48, 1),
(49, 1),
(50, 1),
(51, 1),
(52, 1),
(53, 1),
(54, 1),
(55, 1);

INSERT INTO `privilegio` (`id_recurso`, `id_rol`) VALUES
(1, 2),
(2, 2),
(3, 2),
(34, 2);

INSERT INTO `privilegio` (`id_recurso`, `id_rol`) VALUES
(1, 3),
(2, 3),
(3, 3),
(30, 3);

--
-- Dumping data for table `cuenta`
--

INSERT INTO `cuenta` (`id_persona`, `id_rol`) VALUES
(1, 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
