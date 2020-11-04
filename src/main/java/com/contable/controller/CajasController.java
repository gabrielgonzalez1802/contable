package com.contable.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Prestamo;
import com.contable.service.IAbonosService;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.IPrestamosService;

@Controller
@RequestMapping("/cajas")
public class CajasController {
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IAbonosService serviceAbonos;

	@GetMapping("/mostrarCuadre")
	public String mostrarCuadre(Model model, HttpSession session) {
		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		List<Abono> abonos = new LinkedList<>();
		Carpeta carpeta = new Carpeta();
		double sumaEfectivo = 0;
		double sumaCheque = 0;
		double sumaDepositoTransferencia = 0;
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			carpeta = carpetas.get(0);
		}
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorCarpeta(carpeta);
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
		
		model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
		model.addAttribute("sumaCheque", formato2d(sumaCheque));
		model.addAttribute("sumaDepositoTransferencia", formato2d(sumaDepositoTransferencia));
		model.addAttribute("abonos", abonos);
		return "cajas/cuadreCaja :: cuadreCaja";
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
