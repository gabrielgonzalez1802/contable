package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Cuenta;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;
import com.contable.service.IAbonosService;
import com.contable.service.ICarpetasService;
import com.contable.service.ICuentasService;
import com.contable.service.IPrestamosService;
import com.contable.service.IUsuariosService;

@Controller
@RequestMapping("/cajas")
public class CajasController {
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IAbonosService serviceAbonos;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private ICuentasService serviceCuentas;

	@GetMapping("/mostrarCuadre")
	public String mostrarCuadre(Model model, HttpSession session) {
		List<Abono> abonos = new LinkedList<>();
		double sumaEfectivo = 0;
		double sumaCheque = 0;
		double sumaDepositoTransferencia = 0;
		
		Carpeta carpeta = null;
		if(session.getAttribute("carpeta")!=null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpetaEmpresa(carpeta, (Empresa) session.getAttribute("empresa"));
		List<Abono> abonosTemp = serviceAbonos.buscarPorPrestamos(prestamos);
		List<Abono> abonosDepured = new LinkedList<>();
		LocalDateTime fechaAcct =  LocalDateTime.now();
		for (Abono abono : abonosTemp) {
			LocalDateTime fechaAbono = convertToLocalDateTimeViaInstant(abono.getFecha());
			if(fechaAcct.getYear() == fechaAbono.getYear() && 
					fechaAcct.getMonthValue() == fechaAbono.getMonthValue() && 
						fechaAcct.getDayOfMonth() == fechaAbono.getDayOfMonth()) {
				
				abonosDepured.add(abono);
			}
		}
		for (Abono abonoDepured : abonosDepured) {
			sumaEfectivo += abonoDepured.getEfectivo();
			sumaCheque += abonoDepured.getCheque();
			sumaDepositoTransferencia += abonoDepured.getTransferencia_deposito();
			abonos.add(abonoDepured);
		}
		
		List<Usuario> usuarios = serviceUsuarios.buscarPorEstado(1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
		model.addAttribute("sumaCheque", formato2d(sumaCheque));
		model.addAttribute("sumaDepositoTransferencia", formato2d(sumaDepositoTransferencia));
		model.addAttribute("abonos", abonos);
		model.addAttribute("fecha", new Date());
		model.addAttribute("userAcct", 0);
		model.addAttribute("carpeta", carpeta);
		return "cajas/cuadreCaja :: cuadreCaja";
	}
	
	@PostMapping("/mostrarCuadre")
	public String mostrarCuadre(String fecha, Integer userId
			,Model model, HttpSession session) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		LocalDateTime fechaBusqueda = null;
		
		if(fecha.equals("")) {
			fechaBusqueda = convertToLocalDateTimeViaInstant(new Date());
		}else {
			fechaBusqueda = convertToLocalDateTimeViaInstant(formato.parse(fecha));
		}
		
		Date fechaTemp = convertToDateViaInstant(fechaBusqueda);
		
		Carpeta carpeta = null;
		if(session.getAttribute("carpeta")!=null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		
		List<Abono> abonos = new LinkedList<>();
		double sumaEfectivo = 0;
		double sumaCheque = 0;
		double sumaDepositoTransferencia = 0;
		
		List<Abono> abonosTemp = new LinkedList<>();
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpetaEmpresa(carpeta, (Empresa) session.getAttribute("empresa"));
		if(userId>0) {
			Usuario usuarioTemp = serviceUsuarios.buscarPorId(userId);
			abonosTemp = serviceAbonos.buscarPorPrestamosUsuario(prestamos, usuarioTemp);
		}else {
			abonosTemp = serviceAbonos.buscarPorPrestamos(prestamos);
		}
		List<Abono> abonosDepured = new LinkedList<>();
		for (Abono abono : abonosTemp) {
			LocalDateTime fechaAbono = convertToLocalDateTimeViaInstant(abono.getFecha());
			if(fechaBusqueda.getYear() == fechaAbono.getYear() && 
					fechaBusqueda.getMonthValue() == fechaAbono.getMonthValue() && 
							fechaBusqueda.getDayOfMonth() == fechaAbono.getDayOfMonth()) {
				
				abonosDepured.add(abono);
			}
		}
		for (Abono abonoDepured : abonosDepured) {
			sumaEfectivo += abonoDepured.getEfectivo();
			sumaCheque += abonoDepured.getCheque();
			sumaDepositoTransferencia += abonoDepured.getTransferencia_deposito();
			abonos.add(abonoDepured);
		}
		
		List<Usuario> usuarios = serviceUsuarios.buscarPorEstado(1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
		model.addAttribute("sumaCheque", formato2d(sumaCheque));
		model.addAttribute("sumaDepositoTransferencia", formato2d(sumaDepositoTransferencia));
		model.addAttribute("abonos", abonos);
		model.addAttribute("fecha", fechaTemp);
		model.addAttribute("userAcct", userId);
		return "cajas/cuadreCaja :: cuadreCaja";
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	@ModelAttribute
	public void setGenericos(Model model, HttpSession session) {
		Carpeta carpeta = null;
		if(session.getAttribute("carpeta")!=null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			carpeta = carpetas.get(0);
		}
		 
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpetaEmpresa(carpeta, empresa);
 		model.addAttribute("cuentas", cuentas);
	}
}
