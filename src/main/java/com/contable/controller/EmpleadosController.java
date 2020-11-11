package com.contable.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Asignacion;
import com.contable.model.Deduccion;
import com.contable.model.Empleado;
import com.contable.service.IAsignacionesServices;
import com.contable.service.IDeduccionesServices;
import com.contable.service.IEmpleadosService;

@Controller
@RequestMapping("/empleados")
public class EmpleadosController {

	@Autowired
	private IEmpleadosService serviceEmpleados;
	
	@Autowired
	private IAsignacionesServices serviceAsignaciones;
	
	@Autowired
	private IDeduccionesServices serviceDeducciones;
	
	@GetMapping("/listaEmpleados")
	public String listaEmpleados(Model model) {
		List<Empleado> empleados = serviceEmpleados.buscarTodos();
		model.addAttribute("empleados", empleados);
		return "empleados/listaEmpleados :: listaEmpleados";
	}
	
	@GetMapping("/agregarEmpleado")
	public String agregarEmpleado(Model model) {
		Empleado empleado = new Empleado();
		model.addAttribute("empleado", empleado);
		return "empleados/formEmpleados :: form";
	}
	
	@GetMapping("/modificarEmpleado/{id}")
	public String modificarEmpleado(Model model, @PathVariable(name = "id") Integer id) {
		Empleado empleado = serviceEmpleados.buscarPorId(id);
		model.addAttribute("empleado", empleado);
		return "empleados/formEmpleados :: form";
	}
	
	@GetMapping("/listaAsignaciones/{id}")
	public String listaAsignaciones(Model model, @PathVariable(name = "id") Integer idEmpleado) {
		Empleado empleado = serviceEmpleados.buscarPorId(idEmpleado);
		List<Asignacion> asignaciones = serviceAsignaciones.buscarPorEmpleado(empleado);
		model.addAttribute("asignaciones", asignaciones);
		return "empleados/listaEmpleados :: #asignacionesEmpleado";
	}
	
	@GetMapping("/listaDeducciones/{id}")
	public String listaDeducciones(Model model, @PathVariable(name = "id") Integer idEmpleado) {
		Empleado empleado = serviceEmpleados.buscarPorId(idEmpleado);
		List<Deduccion> deducciones = serviceDeducciones.buscarPorEmpleado(empleado);
		model.addAttribute("deducciones", deducciones);
		return "empleados/listaEmpleados :: #deduccionesEmpleado";
	}
	
	@PostMapping("/guardarAsignacion")
	@ResponseBody
	public ResponseEntity<String> guardarAsignacion(Model model, Integer idEmpleado, String motivo, Double monto){
		String response = "0";
		Empleado empleado = serviceEmpleados.buscarPorId(idEmpleado);
		Asignacion asignacion = new Asignacion();
		asignacion.setEmpleado(empleado);
		asignacion.setEstado(1);
		asignacion.setMonto(monto);
		asignacion.setMotivo(motivo);
		serviceAsignaciones.guardar(asignacion);
		if(asignacion.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/guardarDeduccion")
	@ResponseBody
	public ResponseEntity<String> guardarDeduccion(Model model, Integer idEmpleado, String motivo, Double monto){
		String response = "0";
		Empleado empleado = serviceEmpleados.buscarPorId(idEmpleado);
		Deduccion deduccion = new Deduccion();
		deduccion.setEmpleado(empleado);
		deduccion.setEstado(1);
		deduccion.setMonto(monto);
		deduccion.setMotivo(motivo);
		serviceDeducciones.guardar(deduccion);
		if(deduccion.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/eliminarAsignacion/{id}")
	public String eliminarAsignacion(Model model, @PathVariable(name = "id") Integer idAsignacion) {
		Asignacion asignacion = serviceAsignaciones.buscarPorId(idAsignacion);
		serviceAsignaciones.eliminar(asignacion);
		Empleado empleado = asignacion.getEmpleado();
		List<Asignacion> asignaciones = serviceAsignaciones.buscarPorEmpleado(empleado);
		model.addAttribute("asignaciones", asignaciones);
		return "empleados/listaEmpleados :: #asignacionesEmpleado";
	}
	
	@GetMapping("/eliminarDeduccion/{id}")
	public String eliminarDeduccion(Model model, @PathVariable(name = "id") Integer idDeduccion) {
		Deduccion deduccion = serviceDeducciones.buscarPorId(idDeduccion);
		serviceDeducciones.eliminar(deduccion);
		Empleado empleado = deduccion.getEmpleado();
		List<Deduccion> deducciones = serviceDeducciones.buscarPorEmpleado(empleado);
		model.addAttribute("deducciones", deducciones);
		return "empleados/listaEmpleados :: #deduccionesEmpleado";
	}
	
	@PostMapping("/guardar")
	@ResponseBody
	public ResponseEntity<String> crear(@ModelAttribute("empleado") Empleado empleado){
		String response = "0";
		
		if(empleado.getId()==null) {
			//Verificamos que el empleado no exista
			List<Empleado> usuarioTemp = serviceEmpleados.buscarPorCedula(empleado.getCedula());
			if(!usuarioTemp.isEmpty()) {
				response = "2";
			}else {
				serviceEmpleados.guardar(empleado);
				if(empleado.getId()!=null) {
					response = "1";
				}
			}
		}else {			
			serviceEmpleados.guardar(empleado);
			response = "4";
		}
		
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
}
