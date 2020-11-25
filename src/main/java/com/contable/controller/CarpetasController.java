package com.contable.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Carpeta;
import com.contable.model.Empresa;
import com.contable.service.ICarpetasService;

@Controller
@RequestMapping("/carpetas")
public class CarpetasController {
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@GetMapping("/buscar/{carpeta}")
	@ResponseBody
	public ResponseEntity<String> buscarCarpeta(@PathVariable("carpeta") String item, HttpSession session){
			
		Carpeta carpeta = null;
		
		if(session.getAttribute("empresa") != null) {
			carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}else {
			return new ResponseEntity<>("NO", HttpStatus.OK); 
		}
		
		if(carpeta == null) {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			return new ResponseEntity<>("No tienes carpetas creadas", HttpStatus.OK);
		}
		session.setAttribute("carpeta", carpeta.getId());
		return new ResponseEntity<>("SI", HttpStatus.OK);
	}
	
	@GetMapping("/buscarCarpetaCobros/{carpeta}")
	public String buscarCarpetaCobros(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = null;
		if(session.getAttribute("empresa") != null) {
			 carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}
		String msg = "0";
		if(carpeta == null) {
			if(session.getAttribute("carpeta")!=null) {
				model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
				carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
			}else {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
				model.addAttribute("carpeta", carpeta);
			}
		}else {
			msg = "1";
		}
		model.addAttribute("msgId", msg);
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		return "prestamos/prestamosPendientes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCobros")
	public String buscarCarpetaCobros(HttpSession session, Model model){
		String msg = "1";
		List<Carpeta> carpetas = new LinkedList<>();
		if(session.getAttribute("empresa") != null) {
			carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
		}else {
			msg = "0";
		}
		model.addAttribute("msgId", msg);
		return "prestamos/prestamosPendientes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaReportes/{carpeta}")
	public String buscarCarpetaReportes(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = null;
		if(session.getAttribute("empresa") != null) {
			 carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}
		String msg = "0";
		if(carpeta == null) {
			if(session.getAttribute("carpeta")!=null) {
				model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
				carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
			}else {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
				model.addAttribute("carpeta", carpeta);
			}
		}else {
			msg = "1";
		}
		model.addAttribute("msgId", msg);
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		return "impresiones/listaReportes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaReportes")
	public String buscarCarpetaReportes(HttpSession session, Model model){
		String msg = "1";
		List<Carpeta> carpetas = new LinkedList<>();
		if(session.getAttribute("empresa") != null) {
			carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
		}else {
			msg = "0";
		}
		model.addAttribute("msgId", msg);
		return "impresiones/listaReportes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaContabilidad/{carpeta}")
	public String buscarCarpetaContabilidad(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = null;
		if(session.getAttribute("empresa") != null) {
			 carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}
		String msg = "0";
		if(carpeta == null) {
			if(session.getAttribute("carpeta")!=null) {
				model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
				carpeta = serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta"));
			}else {
				carpeta = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa")).get(0);
				model.addAttribute("carpeta", carpeta);
			}
		}else {
			msg = "1";
		}
		model.addAttribute("msgId", msg);
		session.setAttribute("carpeta", carpeta.getId());
		model.addAttribute("carpeta", carpeta);
		return "contabilidad/contabilidad :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaContabilidad")
	public String buscarCarpetaContabilidad(HttpSession session, Model model){
		String msg = "1";
		List<Carpeta> carpetas = new LinkedList<>();
		if(session.getAttribute("empresa") != null) {
			carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
		}else {
			msg = "0";
		}
		model.addAttribute("msgId", msg);
		return "contabilidad/contabilidad :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCuentas")
	public String buscarCarpetaCuentas(HttpSession session, Model model){
		String msg = "1";
		List<Carpeta> carpetas = new LinkedList<>();
		if(session.getAttribute("empresa") != null) {
			carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
			model.addAttribute("msgId", msg);
		}else {
			msg = "0";
		}
		model.addAttribute("msgId", msg);
		return "cuentas/listaCuentas :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCuentas/{carpeta}")
	public String buscarCarpetaCuentas(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = null;
		if(session.getAttribute("empresa") != null) {
			 carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}
		String msg = "0";
		if(carpeta == null) {
			model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
		}else {
			msg = "1";
			session.setAttribute("carpeta", carpeta.getId());
			model.addAttribute("carpeta", carpeta);
		}
		model.addAttribute("msgId", msg);
		return "cuentas/listaCuentas :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCajas/{carpeta}")
	public String buscarCarpetaCajas(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = null;
		if(session.getAttribute("empresa") != null) {
			 carpeta = serviceCarpetas.buscarPorNombreEmpresa(item, (Empresa) session.getAttribute("empresa"));
		}
		String msg = "0";
		if(carpeta == null) {
			model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
		}else {
			msg = "1";
			session.setAttribute("carpeta", carpeta.getId());
			model.addAttribute("carpeta", carpeta);
		}
		model.addAttribute("msgId", msg);
		return "cajas/cuadreCaja :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCajas")
	public String buscarCarpetaCajas(HttpSession session, Model model){
		String msg = "1";
		List<Carpeta> carpetas = new LinkedList<>();
		if(session.getAttribute("empresa") != null) {
			carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, (Empresa) session.getAttribute("empresa"));
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
			model.addAttribute("msgId", msg);
		}else {
			msg = "0";
		}
		model.addAttribute("msgId", msg);
		return "cajas/cuadreCaja :: #buscarCarpeta";
	}
}
