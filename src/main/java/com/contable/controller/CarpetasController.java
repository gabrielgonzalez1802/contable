package com.contable.controller;

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
import com.contable.service.ICarpetasService;

@Controller
@RequestMapping("/carpetas")
public class CarpetasController {
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@GetMapping("/buscar/{carpeta}")
	@ResponseBody
	public ResponseEntity<String> buscarCarpeta(@PathVariable("carpeta") String item, HttpSession session){
		Carpeta carpeta = serviceCarpetas.buscarPorNombre(item);
		if(carpeta == null) {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			session.setAttribute("carpeta", carpetas.get(0).getId());
			return new ResponseEntity<>("No tienes carpetas creadas", HttpStatus.OK);
		}
		session.setAttribute("carpeta", carpeta.getId());
		return new ResponseEntity<>("SI", HttpStatus.OK);
	}
	
	@GetMapping("/buscarCarpetaCobros/{carpeta}")
	public String buscarCarpetaCobros(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = serviceCarpetas.buscarPorNombre(item);
		String msg = "0";
		if(carpeta == null) {
			model.addAttribute("carpeta", serviceCarpetas.buscarPorId((int) session.getAttribute("carpeta")));
		}else {
			msg = "1";
			session.setAttribute("carpeta", carpeta.getId());
			model.addAttribute("carpeta", carpeta);
		}
		model.addAttribute("msgId", msg);
		return "prestamos/prestamosPendientes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCobros")
	public String buscarCarpetaCobros(HttpSession session, Model model){
		String msg = "1";
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
			model.addAttribute("msgId", msg);
		return "prestamos/prestamosPendientes :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCuentas")
	public String buscarCarpetaCuentas(HttpSession session, Model model){
		String msg = "1";
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			session.setAttribute("carpeta", carpetas.get(0).getId());
			model.addAttribute("carpeta", carpetas.get(0));
			model.addAttribute("msgId", msg);
		return "cuentas/listaCuentas :: #buscarCarpeta";
	}
	
	@GetMapping("/buscarCarpetaCuentas/{carpeta}")
	public String buscarCarpetaCuentas(@PathVariable("carpeta") String item, HttpSession session, Model model){
		Carpeta carpeta = serviceCarpetas.buscarPorNombre(item);
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

}
