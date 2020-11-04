package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Nota;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.Usuario;
import com.contable.service.INotasService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;
import com.contable.service.IUsuariosService;

@Controller
@RequestMapping("/notas")
public class NotasController {
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private INotasService serviceNotas;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@PostMapping("/guardarNota")
	@ResponseBody
	public ResponseEntity<String> guardarNota(Model model, HttpSession session,
			Integer idPrestamo, String nota, String fecha, Integer cuota) throws ParseException {
		String response = "0";
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		Nota notaNueva = new Nota();
		if(prestamo.getTipo().equals("2")) {
			//Interes
			PrestamoInteresDetalle prestamoInteresDetalle = servicePrestamosInteresesDetalles.buscarPorId(cuota);
			if(prestamoInteresDetalle!=null) {
				notaNueva.setFecha(formatter.parse(fecha));
				notaNueva.setUsuario(usuario);
				notaNueva.setPrestamo(prestamo);
				notaNueva.setPrestamoInteresDetalle(prestamoInteresDetalle);
				notaNueva.setTipo(2);
				notaNueva.setNota(nota);
				serviceNotas.guardar(notaNueva);
			}
		}else {
			//Cuotas
			PrestamoDetalle prestamoDetalle = servicePrestamosDetalles.buscarPorId(cuota);
			if(prestamoDetalle!=null) {
				notaNueva.setFecha(formatter.parse(fecha));
				notaNueva.setUsuario(usuario);
				notaNueva.setPrestamo(prestamo);
				notaNueva.setPrestamoDetalle(prestamoDetalle);
				notaNueva.setTipo(2);
				notaNueva.setNota(nota);
				serviceNotas.guardar(notaNueva);
			}
		}
		
		if(notaNueva.getId()>0) {
			response = "1";
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/listaNotas/{id}")
	public String listaNotas(@PathVariable("id") Integer idPrestamo, Model model) {
		Prestamo prestamo = servicePrestamos.buscarPorId(idPrestamo);
		List<Nota> notas = serviceNotas.buscarPorPrestamo(prestamo);
		List<Nota> notasDepured = new LinkedList<>();
		LocalDateTime fechaAcct =  LocalDateTime.now();
		for (Nota nota : notas) {
			LocalDateTime fechaNota = convertToLocalDateTimeViaInstant(nota.getFecha());
			if(fechaNota.isAfter(fechaAcct) || fechaNota.isEqual(fechaAcct)) {
				notasDepured.add(nota);
			}
		}
		model.addAttribute("notas", notasDepured);
		return "index :: #tablaNotas";
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
}
