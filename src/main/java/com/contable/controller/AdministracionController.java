package com.contable.controller;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Token;
import com.contable.service.ITokensService;

@Controller
@RequestMapping("/administracion")
public class AdministracionController {
	
	@Autowired
	private ITokensService serviceTokens;
	
	public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static final int SECURE_TOKEN_LENGTH = 8;

	private static final SecureRandom random = new SecureRandom();

	private static final char[] symbols = CHARACTERS.toCharArray();

	private static final char[] buf = new char[SECURE_TOKEN_LENGTH];

	@GetMapping("/")
	public String principal(Model model, HttpSession session) {
		List<Token> tokens = serviceTokens.buscarTodos();
		model.addAttribute("tokens", tokens);
		model.addAttribute("token", new Token());
		return "administracion/tokens :: listaTokens";
	}
	
	@GetMapping("/crearToken")
	public ResponseEntity<String> crearToken(Model model, HttpSession session) {
		String code = "";
		code = nextToken();
		Token token = new Token();
		token.setToken(code);
		serviceTokens.guardar(token);
		return new ResponseEntity<String>(code, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/actualizarToken")
	public ResponseEntity<String> actualizarToken(Model model, HttpSession session,
			Integer tokenId, String expire, Integer estado) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		String response = "1";
		Token token = serviceTokens.buscarPorId(tokenId);
		if(!expire.equals("")) {
			LocalDateTime fechaExpire = convertToLocalDateTimeViaInstant(formato.parse(expire));
			Date fecha = convertToDateViaInstant(fechaExpire);
			token.setFechaExpiracion(fecha);
		}
		token.setEstado(estado);
		serviceTokens.guardar(token);
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/eliminarToken")
	public ResponseEntity<String> eliminarToken(Model model, HttpSession session,
			Integer tokenId) {
		String response = "1";
		Token token = serviceTokens.buscarPorId(tokenId);
		serviceTokens.eliminar(token);
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/modificarToken/{idToken}")
	public String modificarToken(Model model, HttpSession session, @PathVariable("idToken") Integer idToken) {
		Token token = serviceTokens.buscarPorId(idToken);
		model.addAttribute("token", token);
		return "administracion/tokens :: #editToken";
	}
	
	public static String nextToken() {
	       for (int idx = 0; idx < buf.length; ++idx)
	           buf[idx] = symbols[random.nextInt(symbols.length)];
	       return new String(buf);
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
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
