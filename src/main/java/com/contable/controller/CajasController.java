package com.contable.controller;

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
import com.contable.model.Cliente;
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
		Integer idCliente = (Integer) session.getAttribute("cliente");
		
		if(idCliente == null || idCliente == 0) {
			return "redirect:/clientes/buscarCliente";
		}
		
		Cliente cliente = serviceClientes.buscarPorId(idCliente);
		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		List<Abono> abonos = new LinkedList<>();
		Carpeta carpeta = new Carpeta();
		double sumaEfectivo = 0;
		if(idCarpeta!=null) {
			carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			carpeta = carpetas.get(0);
		}
		if(cliente!=null) {
			List<Prestamo> prestamos = servicePrestamos.buscarPorClienteCarpetaPorFechaDesc(cliente, carpeta);
			List<Abono> abonosTemp = serviceAbonos.buscarPorPrestamosOrderByAbono(prestamos);
			for (Abono abono : abonosTemp) {
				sumaEfectivo += abono.getMonto();
				abonos.add(abono);
			}
			model.addAttribute("sumaEfectivo", formato2d(sumaEfectivo));
			model.addAttribute("abonos", abonos);
			return "cajas/cuadreCaja :: cuadreCaja";
		}
		return "redirect:/clientes/buscarCliente";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
