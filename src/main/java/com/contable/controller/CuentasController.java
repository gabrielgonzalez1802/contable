package com.contable.controller;

import java.math.BigDecimal;
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

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;
import com.contable.model.Empresa;
import com.contable.service.ICarpetasService;
import com.contable.service.ICuentasService;

@Controller
@RequestMapping("/cuentas")
public class CuentasController {
	
	@Autowired
	private ICuentasService serviceCuentas;
	
	@Autowired
	private ICarpetasService serviceCarpetas;

	@GetMapping("/listaCuentas")
	public String listaCuentas(Model model, HttpSession session) {
		Carpeta carpeta;
		String montoPlano = "0.0";
		
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
		}
		List<Cuenta> cuentas = serviceCuentas.buscarPorCarpeta(carpeta);
		for (Cuenta cuenta : cuentas) {
			if(cuenta.getMonto()!=null) {
				 montoPlano = new BigDecimal(cuenta.getMonto()).toPlainString();
			}
			BigDecimal bigDecimal = new BigDecimal(montoPlano);
			cuenta.setMontoPlano(montoPlano);
			cuenta.setMontoBigDecimal(bigDecimal);
		}
		model.addAttribute("cuentas", cuentas);
		model.addAttribute("carpeta", carpeta);
		return "cuentas/listaCuentas :: listaCuentas";
	}
	
	@GetMapping("/agregarCuenta")
	public String agregarCuenta(Model model) {
		Cuenta cuenta = new Cuenta();
		model.addAttribute("cuenta", cuenta);
		return "cuentas/formCuentas :: form";
	}
	
	@GetMapping("/modificarCuenta/{id}")
	public String modificarCuenta(Model model, @PathVariable("id") Integer id) {
		Cuenta cuenta = serviceCuentas.buscarPorId(id);
		String montoPlano = new BigDecimal(cuenta.getMonto()).toPlainString();
		BigDecimal bigDecimal = new BigDecimal(montoPlano);
		cuenta.setMontoPlano(montoPlano);
		cuenta.setMontoBigDecimal(bigDecimal);
		model.addAttribute("cuenta", cuenta);
		return "cuentas/formUpdateCuentas :: formUpdate";
	}
	
	@GetMapping("/eliminar/{id}")
	@ResponseBody
	public ResponseEntity<Integer> eliminar(@PathVariable(name = "id") Integer id){
		Integer response = 0;
		Cuenta cuenta = serviceCuentas.buscarPorId(id);
		serviceCuentas.eliminar(cuenta);
		Cuenta cuentaTemp = serviceCuentas.buscarPorId(id);
		if(cuentaTemp==null) {
			response = 1;
		}
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crear(Cuenta cuenta, HttpSession session){
		String response = "0";
		Carpeta carpeta;
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
		}
		cuenta.setCarpeta(carpeta);
		cuenta.setEmpresa((Empresa) session.getAttribute("empresa"));
		serviceCuentas.guardar(cuenta);
		if(cuenta.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(Cuenta cuenta, HttpSession session){
		Cuenta originalCuenta = serviceCuentas.buscarPorId(cuenta.getId());
		String response = "0";
		Carpeta carpeta;
		if(session.getAttribute("carpeta") != null) {
			carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
		}
		cuenta.setEmpresa((Empresa) session.getAttribute("empresa"));
		cuenta.setCarpeta(carpeta);
		cuenta.setMonto(originalCuenta.getMonto().doubleValue());
		cuenta.setMontoPlano(originalCuenta.getMonto().toString());
		cuenta.setMontoBigDecimal(originalCuenta.getMontoBigDecimal());
		serviceCuentas.guardar(cuenta);
		if(cuenta.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/depositar")
	public ResponseEntity<String> depositar(Integer idCuenta, Double montoBanco, HttpSession session){
		String response = "0";
		Cuenta cuenta = serviceCuentas.buscarPorId(idCuenta);
		cuenta.setMonto(cuenta.getMonto()+montoBanco);
		serviceCuentas.guardar(cuenta);
		if(cuenta.getMonto().doubleValue()>montoBanco.doubleValue()) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
