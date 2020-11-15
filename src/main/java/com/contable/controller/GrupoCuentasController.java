package com.contable.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.GrupoCuenta;
import com.contable.service.IGruposCuentasService;

@Controller
@RequestMapping("/grupoCuentas")
public class GrupoCuentasController {
	
	@Autowired
	private IGruposCuentasService serviceGruposCuentas;

	@GetMapping("/buscarGrupoCuenta/{codigoCuenta}")
	public ResponseEntity<String> buscarGrupoCuenta(Model model, HttpSession session, 
			@PathVariable(name = "codigoCuenta") Integer codigoCuenta){
		GrupoCuenta grupoCuenta = serviceGruposCuentas.buscarPorCodigo(codigoCuenta);
		return new ResponseEntity<String>(grupoCuenta==null?"0":grupoCuenta.getTipo(), HttpStatus.ACCEPTED);
	}
	
}
